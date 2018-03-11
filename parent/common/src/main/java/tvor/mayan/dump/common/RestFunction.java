/**
 *
 */
package tvor.mayan.dump.common;

import java.text.MessageFormat;

/**
 * @author shore
 *
 */
public enum RestFunction {
	//
	LIST_MAYAN_TAGS("api/tags/tags/");

	private String function;

	RestFunction(final String function) {
		this.function = function;
	}

	public String getFunction(final Object... parameter) {
		if (parameter.length <= 0) {
			return function;
		}
		return MessageFormat.format(function, parameter);
	}
}
