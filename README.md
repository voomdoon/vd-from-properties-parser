# vd-from-properties-parser

## examples

### Collection

* inline
    ```
    collection=a,b,c
    ```
* using integer index

    ```
    collection.0=a
    collection.1=b
    collection.3=c
    ```
* using any index
    ```
    collection.a=a
    collection.b=b
    collection.z=c
    ```
* with recursion
    ```
    collection.0.string=a
    collection.1.string=b
    ```
  <details>
    <summary>view classes</summary>
  
    ### MyObject
    ```js
    public class MyObject {
        public Collection<MySubObject> collection;
    }
    ```

    ### MySubObject
    ```js
    public class MySubObject {
        public String string;
    }
    ```
  </details>


### Map

* inline
    ```
    map.abc=123
    ```
* using any index and explicit key and value
    ```
    map.0.key=abc
    map.0.value=123
    ```
* with recursion
    ```
    map.0.key.string=a
    map.0.value.string=1
    ```
  <details>
    <summary>view classes</summary>
  
    ### MyObject
    ```js
    public class MyObject {
        public Map<MySubObject, MySubObject> map;
    }
    ```

    ### MySubObject
    ```js
    public class MySubObject {
        public String string;
    }
    ```
  </details>

### inheritance

* specify implementing Class for an interface
    ```
    object.class=MyImplementation
    object.string=abc
    ```
  <details>
    <summary>view classes</summary>
  
    ### MyInterface
    ```js
    public interface MyInterface {}
    ```
    
    ### MyImplementation
    ```js
    public class MyImplementation implements MyInterface {}
    ```
  </details>
