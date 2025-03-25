# vd-from-properties-parser

A lightweight, reflection-based utility to populate Java objects from `Properties`.
Supports nested structures, collections, maps, interfaces (via `class` property), and uses `FromStringParsers` to support string-based conversions for individual properties.

---

## Features

- Populate object fields via `Properties`
- Supports:
  - Collections (inline or indexed)
  - Maps (inline or recursive)
  - Nested objects (recursively parsed)
  - Polymorphism (via `.class` property)
- Works out of the box — no annotations or frameworks required
- Extensible with custom parsers via SPI (see [`FromStringParsers`](https://github.com/your-org/from-string-parsers))

---

## Comparison

| Feature                            | `vd-from-properties-parser` | [Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#application-properties) | [Apache Commons Configuration](https://commons.apache.org/proper/commons-configuration/) | [Owner](https://github.com/matteobaccan/owner) |
|-------------------------------|-----------------------------|-------------|------------------------|-------|
| Works with plain `Properties` | ✅                           | ❌          | ✅                     | ✅    |
| Deep/nested object support    | ✅                           | ✅          | ❌                     | ❌    |
| Custom parsers via SPI        | ✅                           | ❌          | ❌                     | ❌    |
| Reflection-based (no annotations) | ✅                     | ❌          | ❌                     | ❌    |
| Lightweight (no framework)    | ✅                           | ❌          | ✅                     | ✅    |

---

## Examples

### Collection

**Inline:**
```properties
collection=a,b,c
```

**Using integer index:**
```properties
collection.0=a
collection.1=b
collection.3=c
```

**Using any index:**
```properties
collection.a=a
collection.b=b
collection.z=c
```

**With recursion:**
```properties
collection.0.string=a
collection.1.string=b
```

<details>
<summary>View classes</summary>

### MyObject
```java
public class MyObject {
    public Collection<MySubObject> collection;
}
```

### MySubObject
```java
public class MySubObject {
    public String string;
}
```

</details>

---

### Map

**Inline:**
```properties
map.abc=123
```

**Using any index with explicit key and value:**
```properties
map.0.key=abc
map.0.value=123
```

**With recursion:**
```properties
map.0.key.string=a
map.0.value.string=1
```

<details>
<summary>View classes</summary>

### MyObject
```java
public class MyObject {
    public Map<MySubObject, MySubObject> map;
}
```

### MySubObject
```java
public class MySubObject {
    public String string;
}
```

</details>

---

### Inheritance

**Specify implementing class for an interface:**
```properties
object.class=MyImplementation
object.string=abc
```

<details>
<summary>View classes</summary>

### MyInterface
```java
public interface MyInterface {}
```

### MyImplementation
```java
public class MyImplementation implements MyInterface {
    public String string;
}
```

</details>