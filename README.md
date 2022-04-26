# PlugWork
PlugWork is intended to be a super lightweight form of dependency injection.

Currently does not support `@Plug` with parameters, or construction of any classes (i.e. `@WireStation`, or `@PowerPlug`) without no-args constructors.

Note, all methods marked with `@Plug` and all fields marked with `@Socket` must be public.

Activated by calling the static method PlugManager.wire(String prefix) where prefix is the path to the package you would like to start scanning from. String should be formatted with dots, such as `org.example.project`.

There are 6 main annotations that need to be used when using this with a project.

- `@Source`: 
  - Class Level
  - Marks a class as the starting point of your application
  - Must be used with `@PowerPlug` annotation
- `@Start`:
  - Method Level
  - This method is called after wiring is done
  - The start point of your application
  - Must be inside of class marked as `@Source`
- `@WireStation`:
  - Class Level
  - Serves the purpose of a config class
  - May contain multiple methods marked with `@Plug` annotation
  - Must have No-Args constructor
- `@Plug`:
  - Method Level
  - Can have name specified to differentiate between different instances of the same class
  - Default name is name of return type
  - Marks a method to be used to create an object that can be plugged into an `@Socket` field
  - Must be a parameterless method
- `@PowerPlug`:
  - Class Level
  - Can have name specified to differentiate between different instances of the same class
  - Default name is name of class
  - Can have fields marked with `@Socket` annotations
  - Can be used to satisfy `@Socket` dependencies
- `@Socket`:
  - Field Level
  - Can have name specified to differentiate between different instances of the same class
  - Default name is name of field type
  - When used there must be either a `@Plug` or `@PowerPlug` that can be placed here
  - Must be placed in classes with `@PowerPlug` annotation

In order to use, you must clone this repo down, perform `mvn clean install` within the root of this project, and add this dependency to your pom:
````
<dependency>
    <groupId>my.plug</groupId>
    <artifactId>plugWork</artifactId>
    <version>1.0-MVP</version>
</dependency>
````
