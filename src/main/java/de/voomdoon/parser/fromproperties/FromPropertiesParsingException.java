package de.voomdoon.parser.fromproperties;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class FromPropertiesParsingException extends Exception {

	/**
	 * @since 0.1.0
	 */
	private static final long serialVersionUID = 7067185854430956748L;

	/**
	 * DOCME add JavaDoc for constructor FromPropertiesParsingException
	 * 
	 * @param message
	 *            detail message as {@link String}
	 * @since 0.1.0
	 */
	public FromPropertiesParsingException(String message) {
		super(message);
	}

	/**
	 * DOCME add JavaDoc for constructor FromPropertiesParsingException
	 * 
	 * @param message
	 *            detail message as {@link String}
	 * @param cause
	 *            underlying {@link Throwable}
	 * @since 0.1.0
	 */
	public FromPropertiesParsingException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
}
