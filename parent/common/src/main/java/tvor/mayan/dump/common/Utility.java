package tvor.mayan.dump.common;

import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

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
	public String buildUrl(final Map<ArgKey, String> argMap, final String function) {
		final String base = argMap.get(ArgKey.MAYAN_BASE_URL);
		if (base.endsWith("/")) {
			return base + function;
		}
		return base + "/" + function;
	}

	/**
	 * Set up a call to a REST service
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
	public WebTarget setUpRestCall(final Map<ArgKey, String> argMap, final String fullUrl,
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
