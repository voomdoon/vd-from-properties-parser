package de.voomdoon.parser.fromproperties.testobjects;

/**
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class StringTestObject {

	/**
	 * @since 0.1.0
	 */
	public String string;

	/**
	 * @since 0.1.0
	 */
	public StringTestObject() {
		// nothing to do
	}

	/**
	 * @param string
	 * @since 0.1.0
	 */
	public StringTestObject(String string) {
		this.string = string;
	}

	/**
	 * @since 0.1.0
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		StringTestObject other = (StringTestObject) obj;

		if (string == null) {
			if (other.string != null) {
				return false;
			}
		} else if (!string.equals(other.string)) {
			return false;
		}

		return true;
	}

	/**
	 * @since 0.1.0
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((string == null) ? 0 : string.hashCode());

		return result;
	}

	/**
	 * @since 0.1.0
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StringTestObject(string: ");
		builder.append(string);
		builder.append(")");
		return builder.toString();
	}
}
