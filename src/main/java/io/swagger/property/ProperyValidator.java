package io.swagger.property;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class ProperyValidator {

	private ProperyValidator() {
	}

	public static String getCleanProperty(String property) {
		if (Objects.isNull(property)) {
			return StringUtils.EMPTY;
		}
		property = property.replaceAll("[\r\n]+", " ").replace("'", " ").replace("\\", " ").replace("\t", " ")
				.replace("\"", " "); // log forging escape
		return property;
	}
}
