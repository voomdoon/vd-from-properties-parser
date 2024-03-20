package de.voomdoon.parser.fromproperties.testobjects.collection;

import java.util.List;

import de.voomdoon.logging.LogLevel;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class EnumListTestObjectWithSetter {

	/**
	 * @since 0.1.0
	 */
	private List<LogLevel> internalName;

	/**
	 * @param list
	 *            list
	 * @since 0.1.0
	 */
	public void setList(List<LogLevel> list) {
		this.internalName = list;
	}

	/**
	 * @return list
	 * @since 0.1.0
	 */
	public List<LogLevel> getList() {
		return internalName;
	}
}
