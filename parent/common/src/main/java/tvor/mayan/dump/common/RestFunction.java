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
	LIST_DOCUMENTS_FOR_CABINET("api/cabinets/cabinets/{0}/documents"),
	//
	LIST_DOCUMENTS_FOR_TAG("api/tags/tags/{0}/documents/"),
	//
	LIST_METADATA_VALUES_FOR_DOCUMENT("/api/metadata/documents/{0}/metadata"),
	//
	LIST_VERSIONS_FOR_DOCUMENT("api/documents/documents/{0}/versions/"),
	//
	MAYAN_CABINETS("api/cabinets/cabinets/"),
	//
	MAYAN_DOCUMENT_TYPES("api/documents/document_types/"),
	//
	MAYAN_DOCUMENTS("api/documents/documents/"),
	//
	MAYAN_METADATA_TYPES("/api/metadata/metadata_types/"),
	//
	MAYAN_TAGS("api/tags/tags/"),
	//
	METADATA_TYPES_FOR_DOCUMENT_TYPE("api/metadata/document_types/{0}/metadata_types/"),
	//
	;

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
