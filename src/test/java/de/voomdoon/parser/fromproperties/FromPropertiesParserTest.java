package de.voomdoon.parser.fromproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.voomdoon.logging.LogLevel;
import de.voomdoon.parser.fromproperties.testobjects.PrimitivesPublicFieldTestObject;
import de.voomdoon.parser.fromproperties.testobjects.PrimitivesPublicSetterTestObject;
import de.voomdoon.parser.fromproperties.testobjects.StringTestObject;
import de.voomdoon.parser.fromproperties.testobjects.collection.EnumListTestObject;
import de.voomdoon.parser.fromproperties.testobjects.collection.EnumListTestObjectWithSetter;
import de.voomdoon.parser.fromproperties.testobjects.collection.ObjectListTestObject;
import de.voomdoon.parser.fromproperties.testobjects.collection.StringListTestObject;
import de.voomdoon.parser.fromproperties.testobjects.collection.StringListTestObjectWithSetter;
import de.voomdoon.parser.fromproperties.testobjects.common.ClassAnyTestObject;
import de.voomdoon.parser.fromproperties.testobjects.common.ClassSpecificTestObject;
import de.voomdoon.parser.fromproperties.testobjects.common.EnumTestObject;
import de.voomdoon.parser.fromproperties.testobjects.inheritance.InterfaceTestObject;
import de.voomdoon.parser.fromproperties.testobjects.map.Enum_Enum_MapTestObjectWithSetter;
import de.voomdoon.parser.fromproperties.testobjects.map.Object_String_MapTestObject;
import de.voomdoon.parser.fromproperties.testobjects.map.String_Object_MapTestObject;
import de.voomdoon.parser.fromproperties.testobjects.map.String_String_MapTestObject;
import de.voomdoon.parser.fromproperties.testobjects.recursive.StringTestObjectTestObject;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
class FromPropertiesParserTest {

	/**
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	@Nested
	class ParseTest {

		/**
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		@Nested
		class AccessTest extends TestBase {

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_setter() throws Exception {
				logTestStart();

				PrimitivesPublicSetterTestObject object = new PrimitivesPublicSetterTestObject();

				parseProperties(object, "string=abc");

				assertThat(object.getString()).isEqualTo("abc");
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_setter_List() throws Exception {
				logTestStart();

				StringListTestObjectWithSetter object = new StringListTestObjectWithSetter();

				parseProperties(object, """
						list.0=abc
						list.1=ABC
						""");

				assertThat(object.getList()).containsExactly("abc", "ABC");
			}
		}

		/**
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		@Nested
		class CollectionTest extends TestBase {

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_List_enum() throws Exception {
				logTestStart();

				EnumListTestObject object = new EnumListTestObject();

				parseProperties(object, """
						list.0=DEBUG
						list.1=INFO
						""");

				assertThat(object.list).containsExactly(LogLevel.DEBUG, LogLevel.INFO);
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_List_enum_setter() throws Exception {
				logTestStart();

				EnumListTestObjectWithSetter object = new EnumListTestObjectWithSetter();

				parseProperties(object, """
						list.0=DEBUG
						list.1=INFO
						""");

				assertThat(object.getList()).containsExactly(LogLevel.DEBUG, LogLevel.INFO);
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_List_String_multiple_inline() throws Exception {
				logTestStart();

				StringListTestObject object = new StringListTestObject();

				parseProperties(object, "list=abc,ABC");

				assertThat(object.list).containsExactly("abc", "ABC");
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_List_String_multiple_multiLine_indexAsInteger() throws Exception {
				logTestStart();

				StringListTestObject object = new StringListTestObject();

				parseProperties(object, """
						list.0=abc
						list.1=ABC
						""");

				assertThat(object.list).containsExactly("abc", "ABC");
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_List_String_multiple_multiLine_indexAsText() throws Exception {
				logTestStart();

				StringListTestObject object = new StringListTestObject();

				parseProperties(object, """
						list.aa=abc
						list.bb=ABC
						""");

				assertThat(object.list).containsExactly("abc", "ABC");
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_List_String_single() throws Exception {
				logTestStart();

				StringListTestObject object = new StringListTestObject();

				parseProperties(object, """
						list.0=abc
						""");

				assertThat(object.list).containsExactly("abc");
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_order_indexNumbersFirst() throws Exception {
				logTestStart();

				StringListTestObject object = new StringListTestObject();

				parseProperties(object, """
						list.0=0
						list.1=1
						list.a=2
						list.b=3
						""");

				assertThat(object.list).containsExactly("0", "1", "2", "3");
			}
		}

		/**
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		@Nested
		class CommonTest extends TestBase {

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_Class_any() throws Exception {
				logTestStart();

				ClassAnyTestObject object = new ClassAnyTestObject();

				parseProperties(object, "clazz=" + String.class.getName());

				assertThat(object.clazz).isEqualTo(String.class);
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_Class_specific() throws Exception {
				logTestStart();

				ClassSpecificTestObject object = new ClassSpecificTestObject();

				parseProperties(object, "clazz=" + RuntimeException.class.getName());

				assertThat(object.clazz).isEqualTo(RuntimeException.class);
			}

			/**
			 * @since DOCME add inception version number
			 */
			@Disabled // TODO implement error handling
			@Test
			void test_Class_specific_error() throws Exception {
				logTestStart();

				ClassSpecificTestObject object = new ClassSpecificTestObject();

				String properties = "clazz=" + String.class.getName();

				IllegalArgumentException actual = assertThrows(IllegalArgumentException.class,
						() -> parseProperties(object, properties));

				assertThat(actual).hasMessageContainingAll(Error.class.getName(), String.class.getName());
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_enum() throws Exception {
				logTestStart();

				EnumTestObject object = new EnumTestObject();

				parseProperties(object, "logLevel=DEBUG");

				assertThat(object.logLevel).isEqualTo(LogLevel.DEBUG);
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_enum_error() throws Exception {
				logTestStart();

				EnumTestObject object = new EnumTestObject();

				ParseException actual = assertThrows(ParseException.class,
						() -> parseProperties(object, "logLevel=garbage"));

				logger.debug("expected error: " + actual.getMessage());

				assertThat(actual).hasMessageContainingAll("logLevel", "garbage");
			}
		}

		/**
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		@Nested
		class InheritanceTest extends TestBase {

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_inteface_ipmplementationWithoutPackageNameAtSamePackage() throws Exception {
				logTestStart();

				InterfaceTestObject object = new InterfaceTestObject();

				parseProperties(object, """
						object.class=TestInterfaceImpl
						object.string1=abc
						""");

				assertThat(object.object.getString()).isEqualTo("abc");
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_inteface_ipmplementationWithPackageName() throws Exception {
				logTestStart();

				InterfaceTestObject object = new InterfaceTestObject();

				parseProperties(object, """
						object.class=de.voomdoon.parser.fromproperties.testobjects.inheritance.TestInterfaceImpl
						object.string1=abc
						""");

				assertThat(object.object.getString()).isEqualTo("abc");
			}
		}

		/**
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		@Nested
		class MapTest extends TestBase {

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_Enum_Enum_setter_inline() throws Exception {
				logTestStart();

				Enum_Enum_MapTestObjectWithSetter object = new Enum_Enum_MapTestObjectWithSetter();

				parseProperties(object, """
						map.DEBUG=INFO
						""");

				assertThat(object.getMap()).containsEntry(LogLevel.DEBUG, LogLevel.INFO);
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_Enum_Enum_setter_keyValue() throws Exception {
				logTestStart();

				Enum_Enum_MapTestObjectWithSetter object = new Enum_Enum_MapTestObjectWithSetter();

				parseProperties(object, """
						map.0.key=DEBUG
						map.0.value=INFO
						""");

				assertThat(object.getMap()).containsEntry(LogLevel.DEBUG, LogLevel.INFO);
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_String_String_inline() throws Exception {
				logTestStart();

				String_String_MapTestObject object = new String_String_MapTestObject();

				parseProperties(object, """
						map.abc=123
						""");

				assertThat(object.map).containsEntry("abc", "123");
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_String_String_keyValue() throws Exception {
				logTestStart();

				String_String_MapTestObject object = new String_String_MapTestObject();

				parseProperties(object, """
						map.0.key=abc
						map.0.value=123
						""");

				assertThat(object.map).containsEntry("abc", "123");
			}
		}

		/**
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		@Nested
		class PrimitiveTest extends TestBase {

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_boolean() throws Exception {
				logTestStart();

				PrimitivesPublicFieldTestObject object = new PrimitivesPublicFieldTestObject();

				parseProperties(object, "bool=true");

				assertThat(object.bool).isTrue();
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_double() throws Exception {
				logTestStart();

				PrimitivesPublicFieldTestObject object = new PrimitivesPublicFieldTestObject();

				parseProperties(object, "d=123.456");

				assertThat(object.d).isEqualTo(123.456);
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_integer() throws Exception {
				logTestStart();

				PrimitivesPublicFieldTestObject object = new PrimitivesPublicFieldTestObject();

				parseProperties(object, "integer=123");

				assertThat(object.integer).isEqualTo(123);
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_integer_error() throws Exception {
				logTestStart();

				PrimitivesPublicFieldTestObject object = new PrimitivesPublicFieldTestObject();

				NumberFormatException actual = assertThrows(NumberFormatException.class,
						() -> parseProperties(object, "integer=abc"));

				logger.debug("expected error: " + actual.getMessage());

				assertThat(actual).hasMessageContainingAll("integer", "abc");
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_string() throws Exception {
				logTestStart();

				PrimitivesPublicFieldTestObject object = new PrimitivesPublicFieldTestObject();

				parseProperties(object, "string=abc");

				assertThat(object.string).isEqualTo("abc");
			}
		}

		/**
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		@Nested
		class RecursiveTest extends TestBase {

			/**
			 * DOCME add JavaDoc for method test
			 */
			@Test
			void test() throws Exception {
				logTestStart();

				StringTestObjectTestObject object = new StringTestObjectTestObject();

				parseProperties(object, "object.string=abc");

				assertThat(object.object.string).isEqualTo("abc");
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_Collection_List() throws Exception {
				logTestStart();

				ObjectListTestObject object = new ObjectListTestObject();

				parseProperties(object, """
						list.0.string=abc
						list.1.string=ABC
						""");

				assertThat(object.list)
						.hasToString(List.of(new StringTestObject("abc"), new StringTestObject("ABC")).toString());
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_Map_key() throws Exception {
				logTestStart();

				Object_String_MapTestObject object = new Object_String_MapTestObject();

				parseProperties(object, """
						map.0.key.string=abc
						map.0.value=123
						""");

				assertThat(object.map).containsEntry(new StringTestObject("abc"), "123");
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_Map_value() throws Exception {
				logTestStart();

				String_Object_MapTestObject object = new String_Object_MapTestObject();

				parseProperties(object, """
						map.0.key=abc
						map.0.value.string=123
						""");

				assertThat(object.map).containsEntry("abc", new StringTestObject("123"));
			}
		}
	}

	/**
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	private abstract class TestBase extends de.voomdoon.testing.tests.TestBase {

		/**
		 * @since 0.1.0
		 */
		private FromPropertiesParser parser = new FromPropertiesParser().setDefaultLogLevel(LogLevel.TRACE);

		/**
		 * DOCME add JavaDoc for method parseProperties
		 * 
		 * @param object
		 * @param properties
		 * @throws ParseException
		 * @since 0.1.0
		 */
		protected void parseProperties(Object object, String properties) throws ParseException {
			Properties p = new Properties();

			try {
				p.load(new StringReader(properties));
			} catch (IOException e) {
				// TODO implement error handling
				throw new RuntimeException("Error at 'parseProperties': " + e.getMessage(), e);
			}

			parser.parse(object, p);

			logger.debug("parsed: " + object);
		}
	}
}
