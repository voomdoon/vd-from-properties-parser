package de.voomdoon.parser.fromproperties;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

import de.voomdoon.logging.LogLevel;
import de.voomdoon.logging.LogManager;
import de.voomdoon.logging.Logger;
import de.voomdoon.parser.fromstring.FromStringParsers;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class FromPropertiesParser {

	/**
	 * DOCME add JavaDoc for PropertyParser
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	public static class ParsingConfiguration {

		/**
		 * @since 0.1.0
		 */
		private LogLevel defaultLogLevel = LogLevel.INFO;

		/**
		 * @param defaultLogLevel
		 *            defaultLogLevel
		 * @since 0.1.0
		 */
		public void setDefaultLogLevel(LogLevel defaultLogLevel) {
			this.defaultLogLevel = defaultLogLevel;
		}
	}

	/**
	 * DOCME add JavaDoc for PropertiesParser
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	private class CollectionProcessor {

		/**
		 * DOCME add JavaDoc for method getCollection
		 * 
		 * @param context
		 * @param targetType
		 * @param indentation
		 * @return
		 * @throws ParseException
		 * @since 0.1.0
		 */
		private Collection<?> getCollection(Context context, Type targetType, int indentation) throws ParseException {
			log(LogLevel.DEBUG, indentation, "getCollection " + targetType);

			List<String> subKeys = getSubKeys(context.properties);
			log(LogLevel.DEBUG, indentation + 1, "sub-keys: " + subKeys);

			Collection<Object> result = new ArrayList<>();

			for (String subKey : subKeys) {
				processElement(context, subKey, targetType, indentation, result);
			}

			return result;
		}

		/**
		 * DOCME add JavaDoc for method parseCollection
		 * 
		 * @param string
		 * @param targetType
		 * @param indentation
		 * @return
		 * @throws ParseException
		 * @since 0.1.0
		 */
		private Collection<?> parseCollection(String string, Type targetType, int indentation) throws ParseException {
			Collection<Object> result = new ArrayList<>();
			String[] elements = string.split(",");

			for (String element : elements) {
				Object value = objectParser.parse((Class<?>) targetType, element);
				result.add(value);
			}

			return result;
		}

		/**
		 * DOCME add JavaDoc for method processListAccessor
		 * 
		 * @param object
		 * @param context
		 * @param accessor
		 * @param indentation
		 * @throws ParseException
		 * @since 0.1.0
		 */
		private void processAccessor(Object object, Context context, DataAccessor accessor, int indentation)
				throws ParseException {
			log(LogLevel.TRACE, indentation, "processing collection accessor " + accessor);
			Type targetType = accessor.getGenericType(0);
			log(LogLevel.DEBUG, indentation + 1, "element target type: " + targetType);

			Context subContext = context.getContext(accessor.getName(), indentation + 1);
			maybeLogProperties(subContext.properties, indentation + 1);

			if (!subContext.properties.isEmpty()) {
				Collection<?> collection = getCollection(subContext, targetType, indentation + 1);
				accessor.setValue(object, collection);
			} else if (context.getKey(accessor.getName()) != null) {
				Collection<?> collection = parseCollection(context.properties.getProperty(accessor.getName()),
						targetType, indentation + 1);
				accessor.setValue(object, collection);
			}
		}

		/**
		 * DOCME add JavaDoc for method processElement
		 * 
		 * @param context
		 * @param key
		 * @param targetType
		 * @param indentation
		 * @param result
		 * @throws ParseException
		 * @since 0.1.0
		 */
		private void processElement(Context context, String key, Type targetType, int indentation,
				Collection<Object> result) throws ParseException {
			log(LogLevel.DEBUG, indentation, "• processing collection element");
			maybeLogProperties(context.properties, indentation + 1);
			Object value = getObject(context, key, targetType, indentation + 2);
			log(LogLevel.DEBUG, indentation + 2, "value: " + quote(value) + " for " + targetType);
			result.add(value);
		}
	}

	/**
	 * DOCME add JavaDoc for PropertiesParser
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	private class Context {

		/**
		 * @since 0.1.0
		 */
		private String keyPrefix;// XXX why is this unused?

		/**
		 * @since 0.1.0
		 */
		private Properties properties;

		/**
		 * @param properties
		 * @since 0.1.0
		 */
		public Context(Properties properties, String keyPrefix) {
			this.properties = properties;
			this.keyPrefix = keyPrefix;
		}

		/**
		 * DOCME add JavaDoc for method getContext
		 * 
		 * @param key
		 * @param indentation
		 * @return
		 * @since 0.1.0
		 */
		public Context getContext(String key, int indentation) {
			return new Context(getSubProperties(this, key, indentation), getKey(key));
		}

		/**
		 * DOCME add JavaDoc for method getKey
		 * 
		 * @param key
		 * @return
		 * @since 0.1.0
		 */
		public String getKey(String key) {
			return "";
			// TESTME
			// if (keyPrefix != null) {
			// return keyPrefix + "." + key;
			// } else {
			// return key;
			// }
		}

		/**
		 * DOCME add JavaDoc for method getKeyPrefix
		 * 
		 * @return
		 * @since 0.1.0
		 */
		public String getKeyPrefix() {
			return keyPrefix;
		}
	}

	/**
	 * DOCME add JavaDoc for PropertyParser
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	private abstract class DataAccessor {

		/**
		 * DOCME add JavaDoc for method getGenericType
		 * 
		 * @param index
		 * 
		 * @return
		 * @since 0.1.0
		 */
		protected abstract Type getGenericType(int index);

		/**
		 * DOCME add JavaDoc for method getName
		 * 
		 * @return
		 * @since 0.1.0
		 */
		protected abstract String getName();

		/**
		 * DOCME add JavaDoc for method getType
		 * 
		 * @return
		 * @since 0.1.0
		 */
		protected abstract Type getType();

		/**
		 * DOCME add JavaDoc for method setValue
		 * 
		 * @param object
		 * @param value
		 * @throws ParseException
		 * @since 0.1.0
		 */
		protected abstract void setValue(Object object, Object value) throws ParseException;
	}

	/**
	 * DOCME add JavaDoc for PropertyParser
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	private class FieldAccessor extends DataAccessor {

		/**
		 * @since 0.1.0
		 */
		private Field field;

		/**
		 * DOCME add JavaDoc for constructor FieldAccessor
		 * 
		 * @param field
		 * @since 0.1.0
		 */
		public FieldAccessor(Field field) {
			this.field = field;
		}

		/**
		 * @since 0.1.0
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("FieldAccessor(name: ");
			sb.append(getName());
			sb.append(", type: ");
			sb.append(getType());
			sb.append(")");
			return sb.toString();
		}

		@Override
		protected Type getGenericType(int index) {
			if (field.getGenericType() instanceof ParameterizedType type) {
				return type.getActualTypeArguments()[index];
			} else {
				// TODO implement getGenericType
				throw new UnsupportedOperationException("Method 'getGenericType' not implemented yet");
			}
		}

		/**
		 * @since 0.1.0
		 */
		@Override
		protected String getName() {
			return field.getName();
		}

		/**
		 * @since 0.1.0
		 */
		@Override
		protected Type getType() {
			return field.getType();
		}

		/**
		 * @throws ParseException
		 * @since 0.1.0
		 */
		@Override
		protected void setValue(Object object, Object value) throws ParseException {
			Object v;

			try {
				v = convert(field.getType(), value, "TODO");
			} catch (UnsupportedOperationException e) {
				v = value;
			}

			try {
				field.set(object, v);
			} catch (IllegalArgumentException e) {
				// TODO implement error handling
				throw new RuntimeException("Error at 'setValue': " + e.getMessage(), e);
			} catch (IllegalAccessException e) {
				// TODO implement error handling
				throw new RuntimeException("Error at 'setValue': " + e.getMessage(), e);
			}
		}
	}

	/**
	 * DOCME add JavaDoc for PropertiesParser
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	private class MapProcessor {

		/**
		 * DOCME add JavaDoc for method getMap
		 * 
		 * @param context
		 * @param keyTargetType
		 * @param valueTargetType
		 * @param key
		 * @param indentation
		 * @return
		 * @throws ParseException
		 * @since 0.1.0
		 */
		private Map<Object, Object> getMap(Context context, Type keyTargetType, Type valueTargetType, String key,
				int indentation) throws ParseException {
			log(LogLevel.DEBUG, indentation,
					"getMap " + keyTargetType + " -> " + valueTargetType + " " + getAt(context, key));

			List<String> subKeys = getSubKeys(context.properties);
			log(LogLevel.DEBUG, indentation + 1, "sub-keys: " + subKeys);

			Map<Object, Object> result = new HashMap<>();

			for (String subKey : subKeys) {
				processElement(context, subKey, keyTargetType, valueTargetType, indentation + 1, result);
			}

			return result;
		}

		/**
		 * DOCME add JavaDoc for method processMapAccessor
		 * 
		 * @param object
		 * @param context
		 * @param accessor
		 * @param indentation
		 * @throws ParseException
		 * @since 0.1.0
		 */
		private void processAccessor(Object object, Context context, DataAccessor accessor, int indentation)
				throws ParseException {
			log(LogLevel.TRACE, indentation, "processing map accessor " + accessor);
			Context subContext = context.getContext(accessor.getName(), indentation + 1);
			maybeLogProperties(subContext.properties, indentation + 1);

			Type keyTargetType = accessor.getGenericType(0);
			Type valueTargetType = accessor.getGenericType(1);

			Map<Object, Object> map = getMap(subContext, keyTargetType, valueTargetType, accessor.getName(),
					indentation);

			accessor.setValue(object, map);
		}

		/**
		 * DOCME add JavaDoc for method processElement
		 * 
		 * @param context
		 * @param key
		 * @param keyTargetType
		 * @param valueTargetType
		 * @param indentation
		 * @param result
		 * @param subKey2
		 * @throws ParseException
		 * @since 0.1.0
		 */
		private void processElement(Context context, String key, Type keyTargetType, Type valueTargetType,
				int indentation, Map<Object, Object> result) throws ParseException {
			log(LogLevel.DEBUG, indentation, "• processing map element index " + getAt(context, key));

			Object value = context.properties.get(key);
			log(LogLevel.DEBUG, indentation + 1, "value for '" + key + "': " + quote(value));

			Object keyResult;
			Object valueResult;

			if (value != null) {
				keyResult = convert(keyTargetType, key, "key");
				valueResult = convert(valueTargetType, value, "value");

				result.put(keyResult, valueResult);
			} else {
				keyResult = getObject(context.getContext(key, indentation + 1), "key", keyTargetType, indentation + 1);

				valueResult = getObject(context.getContext(key, indentation + 1), "value", valueTargetType,
						indentation + 1);
			}

			log(LogLevel.DEBUG, indentation + 1, "key: " + keyResult);
			log(LogLevel.DEBUG, indentation + 1, "value: " + valueResult);

			if (keyResult != null && valueResult != null) {
				result.put(keyResult, valueResult);
			}
		}
	}

	/**
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	private class RecursiveObjectProcessor {

		/**
		 * DOCME add JavaDoc for method getClass
		 * 
		 * @param className
		 * @param type
		 * @param indentation
		 * @return
		 * @since 0.1.0
		 */
		private Class<?> getClass(String className, Type type, int indentation) {
			log(LogLevel.DEBUG, indentation, "getClass '" + className + "' (" + type + ")");

			String name = className;

			if (!className.contains(".")) {
				name = ((Class<?>) type).getPackageName() + "." + className;
			}

			try {
				return Class.forName(name);
			} catch (ClassNotFoundException e) {
				// TODO implement error handling
				throw new RuntimeException("Error at 'getClass': " + e.getMessage(), e);
			}
		}

		private Object getInstance(Class<?> clazz) {
			try {
				return clazz.getConstructor().newInstance();
			} catch (InstantiationException e) {
				// TODO implement error handling
				throw new RuntimeException("Error at 'getInstance': " + e.getMessage(), e);
			} catch (IllegalAccessException e) {
				// TODO implement error handling
				throw new RuntimeException("Error at 'getInstance': " + e.getMessage(), e);
			} catch (IllegalArgumentException e) {
				// TODO implement error handling
				throw new RuntimeException("Error at 'getInstance': " + e.getMessage(), e);
			} catch (InvocationTargetException e) {
				// TODO implement error handling
				throw new RuntimeException("Error at 'getInstance': " + e.getMessage(), e);
			} catch (NoSuchMethodException e) {
				// TODO implement error handling
				throw new RuntimeException("Error at 'getInstance': " + e.getMessage(), e);
			} catch (SecurityException e) {
				// TODO implement error handling
				throw new RuntimeException("Error at 'getInstance': " + e.getMessage(), e);
			}
		}

		/**
		 * DOCME add JavaDoc for method getInstance
		 * 
		 * @param type
		 * @param context
		 * @param indentation
		 * @return
		 * @since 0.1.0
		 */
		private Object getInstance(Type type, Context context, int indentation) {
			log(LogLevel.DEBUG, indentation, "getInstance " + type);
			maybeLogProperties(context.properties, indentation + 1);

			String className = context.properties.getProperty("class");

			if (className != null) {
				Class<?> clazz = getClass(className, type, indentation + 1);
				return getInstance(clazz);
			} else if (type instanceof Class<?> clazz) {
				return getInstance(clazz);
			}

			// TODO implement getInstance
			throw new UnsupportedOperationException("'getInstance' not implemented at 'PropertiesParser'!");
		}

		/**
		 * DOCME add JavaDoc for method getRecursiveObject
		 * 
		 * @param context
		 * @param type
		 * @param indentation
		 * @return
		 * @throws ParseException
		 * @since 0.1.0
		 */
		private Object getRecursiveObject(Context context, Type type, int indentation) throws ParseException {
			log(LogLevel.DEBUG, indentation, "getRecursiveObject " + type);
			Object result = getInstance(type, context, indentation + 1);
			log(LogLevel.DEBUG, indentation + 1, "instance: " + result);

			parseInternal(result, context, indentation + 1);

			return result;
		}
	}

	/**
	 * DOCME add JavaDoc for PropertiesParser
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	private class SetterAccessor extends DataAccessor {

		/**
		 * Returns whether the given {@link Method} is a setter.
		 * 
		 * @param method
		 * @return {@code true} if the {@link Method} is a setter, {@code true} otherwise.
		 * @since 0.1.0
		 */
		private static boolean isSetter(Method method) {
			return method.getName().startsWith("set") && method.getParameterTypes().length == 1;
		}

		/**
		 * @since 0.1.0
		 */
		private Method setter;

		/**
		 * DOCME add JavaDoc for constructor SetterAccessor
		 * 
		 * @param setter
		 * @since 0.1.0
		 */
		public SetterAccessor(Method setter) {
			this.setter = setter;
		}

		/**
		 * @since 0.1.0
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("SetterAccessor(name: ");
			sb.append(getName());
			sb.append(", type: ");
			sb.append(getType());
			sb.append(")");
			return sb.toString();
		}

		/**
		 * @since 0.1.0
		 */
		@Override
		protected Type getGenericType(int index) {
			return ((ParameterizedType) setter.getGenericParameterTypes()[0]).getActualTypeArguments()[index];
		}

		/**
		 * @since 0.1.0
		 */
		@Override
		protected String getName() {
			return setter.getName().substring(3).toLowerCase();
		}

		/**
		 * @since 0.1.0
		 */
		@Override
		protected Type getType() {
			return setter.getParameterTypes()[0];
		}

		/**
		 * @since 0.1.0
		 */
		@Override
		protected void setValue(Object object, Object value) {
			try {
				setter.invoke(object, value);
			} catch (IllegalAccessException e) {
				// TODO implement error handling
				throw new RuntimeException("Error at 'setValue': " + e.getMessage(), e);
			} catch (IllegalArgumentException e) {
				// TODO implement error handling
				throw new RuntimeException("Error at 'setValue': " + e.getMessage(), e);
			} catch (InvocationTargetException e) {
				// TODO implement error handling
				throw new RuntimeException("Error at 'setValue': " + e.getMessage(), e);
			}
		}
	}

	/**
	 * @since 0.1.0
	 */
	private static final String INDENTATION = "    ";

	/**
	 * DOCME add JavaDoc for method quote
	 * 
	 * @param value
	 * @return
	 * @since 0.1.0
	 */
	private static String quote(Object value) {
		// TESTME
		if (value != null) {
			return "'" + value + "'";
		} else {
			return "null";
		}
	}

	/**
	 * @since 0.1.0
	 */
	private CollectionProcessor collectionProcessor = new CollectionProcessor();

	/**
	 * @since 0.1.0
	 */
	private ParsingConfiguration config = new ParsingConfiguration();

	/**
	 * @since 0.1.0
	 */
	private Map<Class<?>, List<DataAccessor>> dataAccessors = new HashMap<>();

	/**
	 * @since 0.1.0
	 */
	private final Logger logger = LogManager.getLogger(getClass());

	/**
	 * @since 0.1.0
	 */
	private MapProcessor mapProcessor = new MapProcessor();

	/**
	 * @since 0.1.0
	 */
	private FromStringParsers objectParser = FromStringParsers.DEFAULT;

	/**
	 * @since 0.1.0
	 */
	private RecursiveObjectProcessor recursiveObjectProcessor = new RecursiveObjectProcessor();

	/**
	 * DOCME add JavaDoc for method parse
	 * 
	 * @param object
	 * @param properties
	 * @throws ParseException
	 * @since 0.1.0
	 */
	public void parse(Object object, Properties properties) throws ParseException {
		maybeLogProperties(properties, 0);

		parseInternal(object, new Context(properties, null), 0);
	}

	/**
	 * @param defaultLogLevel
	 * @return
	 * @since 0.1.0
	 */
	public FromPropertiesParser setDefaultLogLevel(LogLevel defaultLogLevel) {
		config.setDefaultLogLevel(defaultLogLevel);

		return this;
	}

	/**
	 * DOCME add JavaDoc for method convert
	 * 
	 * @param type
	 * @param value
	 * @param key
	 * @return
	 * @throws ParseException
	 * @since 0.1.0
	 */
	private Object convert(Type type, Object value, String key) throws ParseException {
		if (type.equals(value.getClass())//
				|| type.equals(List.class)) {
			return value;
		}

		try {
			return objectParser.parse((Class<?>) type, value.toString());
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Failed to parse " + type + " for '" + key + "' from '" + value + "'!");
		} catch (ParseException e) {
			throw new ParseException("Failed to parse " + type + " for '" + key + "' from '" + value + "'!",
					e.getErrorOffset());
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("Failed to parse " + type + " for '" + key + "' from '" + value + "'!");
		}
	}

	/**
	 * DOCME add JavaDoc for method getAt
	 * 
	 * @param context
	 * @param key
	 * @return
	 * @since 0.1.0
	 */
	private String getAt(Context context, String key) {
		StringBuilder sb = new StringBuilder();

		sb.append("@ ").append(key).append(" (");

		if (context.getKeyPrefix() != null) {
			sb.append(context.getKeyPrefix()).append(".");
		}

		sb.append(key).append(")");

		return sb.toString();
	}

	/**
	 * @param clazz
	 * @return
	 * @since 0.1.0
	 */
	private List<DataAccessor> getDataAccessors(Class<?> clazz) {
		log(LogLevel.TRACE, 0, "⏷ getDataAccessors " + clazz);
		Field[] fields = clazz.getFields();

		List<DataAccessor> result = new ArrayList<>();

		for (Field field : fields) {
			log(LogLevel.TRACE, 1, "⏵ field: " + field);
			result.add(new FieldAccessor(field));
		}

		for (Method method : clazz.getMethods()) {
			if (!Modifier.isStatic(method.getModifiers())//
					&& !Modifier.isNative(method.getModifiers())) {
				if (SetterAccessor.isSetter(method)) {
					log(LogLevel.TRACE, 1, "⏵ method: " + method);
					result.add(new SetterAccessor(method));
				}
			}
		}

		return result;
	}

	/**
	 * DOCME add JavaDoc for method getDataAccessors
	 * 
	 * @param object
	 * @return
	 * @since 0.1.0
	 */
	private List<DataAccessor> getDataAccessors(Object object) {
		return dataAccessors.computeIfAbsent(object.getClass(), this::getDataAccessors);
	}

	/**
	 * DOCME add JavaDoc for method getObject
	 * 
	 * @param context
	 * @param key
	 * @param type
	 * @param indentation
	 * @return
	 * @throws ParseException
	 * @since 0.1.0
	 */
	private Object getObject(Context context, String key, Type type, int indentation) throws ParseException {
		Object value = context.properties.get(key);
		log(LogLevel.TRACE, indentation + 1, "value: " + quote(value) + " " + getAt(context, key));

		if (value != null) {
			return convert(type, value, key);
		} else {
			Context subContext = context.getContext(key, indentation);

			if (!subContext.properties.isEmpty()) {
				return recursiveObjectProcessor.getRecursiveObject(subContext, type, indentation);
			} else {
				return null;
			}
		}
	}

	/**
	 * DOCME add JavaDoc for method getIndices
	 * 
	 * @param properties
	 * @return
	 * @since 0.1.0
	 */
	private List<String> getSubKeys(Properties properties) {
		return properties.keySet().stream().map(key -> {
			int index = key.toString().indexOf('.');

			if (index > -1) {
				return key.toString().substring(0, index);
			} else {
				return key.toString();
			}
		}).filter(Objects::nonNull).distinct().sorted().toList();
	}

	/**
	 * DOCME add JavaDoc for method getSubProperties
	 * 
	 * @param context
	 * @param keyPrefix
	 * @param indentation
	 * @return
	 * @since 0.1.0
	 */
	private Properties getSubProperties(Context context, String keyPrefix, int indentation) {
		log(LogLevel.TRACE, indentation, "getSubProperties " + getAt(context, keyPrefix));

		Properties result = new Properties();

		for (Entry<Object, Object> entry : context.properties.entrySet()) {
			if (entry.getKey().toString().startsWith(keyPrefix + ".")) {
				String subKey = entry.getKey().toString().substring(keyPrefix.length() + 1);
				result.put(subKey, entry.getValue());
			}
		}

		return result;
	}

	/**
	 * DOCME add JavaDoc for method log
	 * 
	 * @param level
	 * @param indentation
	 * @param message
	 * @since 0.1.0
	 */
	private void log(LogLevel level, int indentation, String message) {
		logger.log(level,
				INDENTATION.repeat(indentation) + message.replace("\n", "\n" + INDENTATION.repeat(indentation)));
	}

	/**
	 * DOCME add JavaDoc for method maybeLogProperties
	 * 
	 * @param properties
	 * @param indentation
	 * @since 0.1.0
	 */
	private void maybeLogProperties(Properties properties, int indentation) {
		if (LogLevel.TRACE.getPriority() < config.defaultLogLevel.getPriority()) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("properties:");

		properties.forEach((key, value) -> sb.append("\n").append(INDENTATION).append(key).append("=").append(value));

		log(LogLevel.TRACE, indentation, sb.toString());
	}

	/**
	 * DOCME add JavaDoc for method parse
	 * 
	 * @param object
	 * @param context
	 * @param indentation
	 * @throws ParseException
	 * @since 0.1.0
	 */
	private void parseInternal(Object object, Context context, int indentation) throws ParseException {
		List<DataAccessor> accessors = getDataAccessors(object);
		log(LogLevel.TRACE, indentation, "○ processing accessors for " + object.getClass().getSimpleName() + ": "
				+ accessors.stream().map(DataAccessor::getName).toList());

		for (DataAccessor accessor : accessors) {
			processAccessor(object, context, accessor, indentation + 1);
		}
	}

	/**
	 * DOCME add JavaDoc for method processAccessor
	 * 
	 * @param object
	 * @param context
	 * @param accessor
	 * @param indentation
	 * @throws ParseException
	 * @since 0.1.0
	 */
	private void processAccessor(Object object, Context context, DataAccessor accessor, int indentation)
			throws ParseException {
		log(LogLevel.TRACE, indentation, "• processing accessor '" + accessor.getName() + "': " + accessor);

		if (accessor.getType().equals(List.class)) {
			collectionProcessor.processAccessor(object, context, accessor, indentation + 1);
		} else if (accessor.getType().equals(Map.class)) {
			mapProcessor.processAccessor(object, context, accessor, indentation + 1);
		} else {
			processAccessorForObject(object, context, accessor, indentation + 1);
		}
	}

	/**
	 * DOCME add JavaDoc for method processObject
	 * 
	 * @param object
	 * @param context
	 * @param accessor
	 * @param indentation
	 * @throws ParseException
	 * @since 0.1.0
	 */
	private void processAccessorForObject(Object object, Context context, DataAccessor accessor, int indentation)
			throws ParseException {
		String key = accessor.getName();
		Object child = getObject(context, key, accessor.getType(), indentation);

		if (child != null) {
			accessor.setValue(object, child);
		}
	}
}
