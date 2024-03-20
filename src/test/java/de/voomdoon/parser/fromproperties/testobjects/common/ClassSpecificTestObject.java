package de.voomdoon.parser.fromproperties.testobjects.common;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class ClassSpecificTestObject {

	/**
	 * @since 0.1.0
	 */
	public Class<? extends Error> clazz;

	/**
	 * @since 0.1.0
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClassSpecificTestObject(clazz: ");
		builder.append(clazz);
		builder.append(")");
		return builder.toString();
	}
}
