package de.voomdoon.parser.fromproperties.testobjects.map;

import java.util.Map;

import de.voomdoon.logging.LogLevel;

/**
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class Enum_Enum_MapTestObjectWithSetter {

	/**
	 * @since 0.1.0
	 */
	private Map<LogLevel, LogLevel> internalName;

	/**
	 * @return map
	 * @since 0.1.0
	 */
	public Map<LogLevel, LogLevel> getMap() {
		return internalName;
	}

	/**
	 * @param map
	 *            map
	 * @since 0.1.0
	 */
	public void setMap(Map<LogLevel, LogLevel> map) {
		this.internalName = map;
	}
}
