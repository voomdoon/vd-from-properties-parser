package de.voomdoon.parser.fromproperties.testobjects.inheritance;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class TestInterfaceImpl implements TestInterface {

	/**
	 * @since 0.1.0
	 */
	public String string1;

	/**
	 * @since 0.1.0
	 */
	@Override
	public String getString() {
		return string1;
	}
}
