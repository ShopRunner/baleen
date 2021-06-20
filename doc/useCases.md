# Vision
 
I'm given an XML/CSV/JSON... source that I don't control but if I can detect and provide feedback quickly when 
the data format changes for the worse, I can prevent the data format changes from becoming permanent.

Baleen should provide

  - Tell good data from bad data.
  - Fine grain control to ignore existing bad data that has been grandfathered in.
  - Allow for ratcheting data from bad formats to good formats.
  - Be unobtrusive to existing code.
  - Tests are great
    There are a lot of great libraries for testing code.  We should use those same concepts for testing 
    data.
  - Performance and streaming are important
    A data validation library should be able to handle large amounts of data quickly.
  - Invalid data is also important
    Warnings and Errors need to be treated as first class objects.
  - Data Traces
    Similar to a stack trace being used to debug a code path, a data trace can be used to debug a 
    path through data. 
  - Don't map data to Types too early.
    Type safe code is great but if the data has not been sanitized then it isn't really typed.  
  - Don't Repeat Yourself
      - If you have described the schema in baleen, you should be able to reuse that schema in other tools for
        - Published schemas JSON Schema, Avro, XSL...
    
      - If you have described the schema in another format, you can reuse it but also extend the validation with baleen
        - Kotlin data class, Avro, JSON Schema, XSL...
  - Don't be a serialization/deserialization library
    Baleen will need to do some deserialization in order to validate but developers should be free to pick a deserialization library independently.
    TODO: how do we verify that these other serialization libraries work after Baleen is validated 


## Use Cases

### 1. Validating existing data format

  1. Create baleen schema or auto-generate from sample data
  1. Run validate before ingesting data
  1. Send validation errors to some alarm
  1. Generate ignore list
  1. Early return if validation fails before rest of the pipeline.
 
### 2. Defining and validating new data format

  1. Generate a Kotlin data class
  1. Extend the data class with baleen tests
  1. Use the Kotlin data class with the serialization/deserialization library of your choice


## Other Ideas that are not Flushed Out
 - Web interface / UI
 - storage format for errors
 - storage format for errors to ignore
 - Should we have one schema for different formats
 - codegen, compile time, runtime
 - Platform Agnostic (Kotlin JVM, Kotlin Javascript, Kotlin Native)
 - Language Agnostic? (Python, Typescript)
 - Create model classes off of data shapes
   - Generating model classes in different languages
 