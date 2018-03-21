package tvor.mayan.dump.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.EnumMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

public class Utility {
	/**
	 * Given the argument map and a (relative) URL path, build the full URL
	 *
	 * @param argMap
	 *            the map of command-line arguments. Among other things, this map
	 *            contains the base URL
	 * @param function
	 *            the path to be appended to the base URL
	 * @return
	 */
	public static String buildUrl(final Map<ArgKey, String> argMap, final String function) {
		final String base = argMap.get(ArgKey.MAYAN_BASE_URL);
		if (base.endsWith("/")) {
			return base + function;
		}
		return base + "/" + function;
	}

	/**
	 * Call a getter-type REST service
	 *
	 * @param theClass
	 *            the data class into which the returned JSON gets parsed
	 * @param targetUrl
	 *            the (full) target URL for the service call
	 * @param argMap
	 *            the map of command-line parameters. Among other things, this
	 *            contains the Mayan userid and password
	 *
	 * @return an instance of the class, containing the JSON data
	 */
	public static <T> T callApiGetter(final Class<T> theClass, final String targetUrl,
			final Map<ArgKey, String> argMap) {
		final WebTarget target = Utility.setUpRestCall(argMap, targetUrl);
		final Invocation.Builder ib = target.request(MediaType.APPLICATION_JSON);
		return ib.get(theClass);
	}

	/**
	 * Call a post-type REST service
	 *
	 * @param theData
	 *            the data to be posted
	 * @param targetUrl
	 *            the full URL for the service
	 * @param argMap
	 *            the map of command-line arguments. Among other things, this
	 *            contains the Mayan userid and password
	 *
	 * @return the Response object from the request
	 */
	public static <T, R> R callApiPoster(final T theData, final Class<R> theResponse, final String targetUrl,
			final Map<ArgKey, String> argMap) {
		return Utility.callApiPoster(theData, theResponse, targetUrl, argMap, false);
	}

	/**
	 * Call a post-type REST service
	 *
	 * @param theData
	 *            the data to be posted
	 * @param targetUrl
	 *            the full URL for the service
	 * @param argMap
	 *            the map of command-line arguments. Among other things, this
	 *            contains the Mayan userid and password
	 * @param registerResponseFilter
	 *            'true' means to attach a logging filter to the post request
	 *
	 * @return the Response object from the request
	 */
	public static <T, R> R callApiPoster(final T theData, final Class<R> theResponse, final String targetUrl,
			final Map<ArgKey, String> argMap, final boolean registerResponseFilter) {
		final Entity<T> entity = Entity.entity(theData, MediaType.APPLICATION_JSON);
		final WebTarget target = Utility.setUpRestCall(argMap, targetUrl, registerResponseFilter);
		final Invocation.Builder ib = target.request(MediaType.APPLICATION_JSON);

		return ib.post(entity, theResponse);
	}

	/**
	 * Extract the individual named fields from the command-line array
	 *
	 * @param arg
	 *            the command line array
	 *
	 * @return a map indicating the values for each of the named fields
	 *
	 * @throws IOException
	 *             if there is a problem prompting the user for argument data
	 */
	public static Map<ArgKey, String> extractCommandLineData(final String[] arg, final String header)
			throws IOException {
		if (arg.length <= 0) {
			return Utility.promptForArgs(header);
		}
		return Utility.processArgs(arg);
	}

	// static void populateCabinetEntryFrom(final EntryCabinet entry, final
	// MayanCabinet cabinet,
	// final Map<ArgKey, String> argMap) {
	// entry.setDocuments_count(cabinet.getDocuments_count());
	// entry.setDocuments_url(cabinet.getDocuments_url());
	// entry.setFull_path(cabinet.getFull_path());
	// entry.setId(cabinet.getId());
	// entry.setLabel(cabinet.getLabel());
	// entry.setParent(cabinet.getParent());
	// entry.setParent_url(cabinet.getParent_url());
	// entry.setUrl(cabinet.getUrl());
	// String nextUrl = entry.getDocuments_url();
	// do {
	// final ListCabinetDocuments docs =
	// Utility.callApiGetter(ListCabinetDocuments.class, nextUrl, argMap);
	// Arrays.asList(docs.getResults()).forEach(doc -> {
	// entry.getDocument_uuid().add(doc.getUuid());
	// });
	// nextUrl = docs.getNext();
	// } while (nextUrl != null);
	//
	// Arrays.asList(cabinet.getChildren()).stream().forEach(child -> {
	// final EntryCabinet e2 = new EntryCabinet();
	// Utility.populateCabinetEntryFrom(e2, child, argMap);
	// // entry.getChildren().add(e2);
	// });
	// }

	/**
	 * Print usage information
	 */
	public static void printHelp() {
		final PrintStream p = System.out;
		p.println("Usage: java Main -b <base URL> -d <data directory> -h -p <Mayan password> -u <Mayan userid>");
		p.println("Switch interpretations (switches are case-insensitive; values are case-sensitive):");
		p.println("  -b: set the base URL for REST service calls.");
		p.println("  -d: directory containing the Mayan dump.");
		p.println("  -h: print this help.");
		p.println("  -p: password for Mayan access.");
		p.println("  -u: userid for Mayan access.");
	}

	/**
	 * Process the argument array into the argument map.
	 *
	 * @param arg
	 *            the argument array passed to the main() method
	 *
	 * @return the argument map. The map key is one of the values in the ArgKey
	 *         enum. The value is the value for the corresponding argument in the
	 *         array. Note that all the values are required, but defaults are
	 *         provided for the true/false values.
	 */
	private static Map<ArgKey, String> processArgs(final String[] arg) {
		final Map<ArgKey, String> rtn = new EnumMap<>(ArgKey.class);
		for (int i = 0; i < arg.length; ++i) {
			switch (arg[i]) {
			case "-b":
			case "-B":
				++i;
				if ("".equals(arg[i]) || arg[i] == null) {
					continue;
				}
				final String a = arg[i].trim();
				if (arg[i].endsWith("/")) {
					rtn.put(ArgKey.MAYAN_BASE_URL, a.substring(0, a.length() - 1));
				} else {
					rtn.put(ArgKey.MAYAN_BASE_URL, a);
				}
			case "-d":
			case "-D":
				++i;
				if ("".equals(arg[i]) || arg[i] == null) {
					continue;
				}
				final String d = arg[i].trim();
				rtn.put(ArgKey.DUMP_DATA_DIRECTORY, d);
				break;
			case "-h":
			case "-H":
				Utility.printHelp();
				System.exit(0);
				break;
			case "-p":
			case "-P":
				++i;
				if ("".equals(arg[i]) || arg[i] == null) {
					continue;
				}
				rtn.put(ArgKey.MAYAN_PASSWORD, arg[i].trim());
				break;
			case "-u":
			case "-U":
				++i;
				if ("".equals(arg[i]) || arg[i] == null) {
					continue;
				}
				rtn.put(ArgKey.MAYAN_USERID, arg[i].trim());
				break;
			default:
				System.out.println("Unrecognized command-line argument: " + arg[i]);
				Utility.printHelp();
				System.exit(1);
			}
		}
		if (rtn.get(ArgKey.MAYAN_BASE_URL) == null) {
			throw new RuntimeException("Base URL not specified");
		}
		if (rtn.get(ArgKey.MAYAN_PASSWORD) == null) {
			throw new RuntimeException("Mayan password not specified");
		}
		if (rtn.get(ArgKey.MAYAN_USERID) == null) {
			throw new RuntimeException("Mayan userid not specified");
		}
		if (rtn.get(ArgKey.DUMP_DATA_DIRECTORY) == null) {
			throw new RuntimeException("Dump data directory not specified");
		}
		return rtn;
	}

	/**
	 * If the command-line argument array is empty, then this class prompts for all
	 * the information required to populate the argument map. This method is
	 * necessary to allow the Main class to be run from inside the Eclipse IDE
	 * without having to define a special run entry in Eclipse.
	 *
	 * @param header
	 *
	 * @return a map of values as entered by the user.
	 *
	 * @throws IOException
	 *             if there is a problem reading data from the terminal
	 */
	private static Map<ArgKey, String> promptForArgs(final String header) throws IOException {
		System.out.println(header);
		final Map<ArgKey, String> rtn = new EnumMap<>(ArgKey.class);
		rtn.put(ArgKey.MAYAN_BASE_URL, "http://mayan.tvor.support:29880");

		final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("    (-h on the command line prints help)");

		System.out.print("Mayan userid (-u): ");
		System.out.flush();
		String x = in.readLine();
		if (x == null) {
			throw new RuntimeException("Null userid");
		}
		if ((x = x.trim()).equals("")) {
			throw new RuntimeException("Empty userid");
		}
		rtn.put(ArgKey.MAYAN_USERID, x);

		System.out.print("Mayan password (-p): ");
		System.out.flush();
		x = in.readLine();
		if (x == null) {
			throw new RuntimeException("Null password");
		}
		if ((x = x.trim()).equals("")) {
			throw new RuntimeException("Empty password");
		}
		rtn.put(ArgKey.MAYAN_PASSWORD, x);

		System.out.print("Base URL (-b) (" + rtn.get(ArgKey.MAYAN_BASE_URL) + "): ");
		System.out.flush();
		x = in.readLine();
		if (x == null) {
			// do nothing - take the default
		} else if ((x = x.trim()).equals("")) {
			// do nothing - take the default
		} else {
			rtn.put(ArgKey.MAYAN_BASE_URL, x);
		}

		System.out.print("Data directory (-d): ");
		System.out.flush();
		x = in.readLine();
		if (x == null) {
			throw new RuntimeException("Null data directory");
		} else if ((x = x.trim()).equals("")) {
			throw new RuntimeException("Empty data directory");
		} else {
			rtn.put(ArgKey.DUMP_DATA_DIRECTORY, x);
		}

		return rtn;
	}

	/**
	 * Set up a call to a REST service
	 *
	 * @param argMap
	 *            the map of command-line arguments
	 * @param fullUrl
	 *            the full URL for the service
	 *
	 * @return the WebTarget object used to access the service
	 */
	public static WebTarget setUpRestCall(final Map<ArgKey, String> argMap, final String fullUrl) {
		return Utility.setUpRestCall(argMap, fullUrl, false);
	}

	/**
	 * Set up a call to a REST service. This method is used both for all HTTP
	 * methods.
	 *
	 * @param argMap
	 *            the map of command-line arguments
	 * @param fullUrl
	 *            the full URL for the service
	 * @param registerResponseFilter
	 *            'true' means to attach a logging filter to the service
	 *
	 * @return the WebTarget object used to access the service
	 */
	public static WebTarget setUpRestCall(final Map<ArgKey, String> argMap, final String fullUrl,
			final boolean registerResponseFilter) {
		final HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
				.credentials(argMap.get(ArgKey.MAYAN_USERID), argMap.get(ArgKey.MAYAN_PASSWORD)).build();
		final ClientConfig config = new ClientConfig();
		config.register(feature);
		if (registerResponseFilter) {
			config.register(ResponseFilter.class);
		}
		final Client client = ClientBuilder.newClient(config);

		final WebTarget target = client.target(fullUrl);
		return target;
	}

}
