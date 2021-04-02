# kontent
Provides serialization support for Cursors and ContentValues in Android apps

## Getting started
Not published to any public maven repo yet, checkout this project and run the following:
```bash
./gradlew publishToMavenLocal
```

Then add the following to your android project's "build.gradle(.kts)" file:
```kotlin
plugins {
    kotlin("plugin.serialization") version "1.4.30"
}

dependencies {
    implementation("net.daverix.kontent:kontent-serializer:0.1-SNAPSHOT")
}

repositories {
    mavenLocal()
}
```

## Usage
Let's say we have a simple class like this that we have annotated with the kotlinx.serialization 
annotation @Serializable:
```kotlin
@Serializable
data class Person(val name: String)
```

With Kontent you can then use this to serialize and deserialize Cursor and ContentValues instances.

### Cursor
Read a single row from a cursor:
```kotlin
val person: Person = Kontent.deserializeFromCursor(cursor)
```

If you have collection, we can iterate through the cursor by just requesting a list instead:
```kotlin
val people: List<Person> = Kontent.deserializeFromCursor(cursor)
```

When implementing a content provider, this can be used to serialize a single instance to a 
MatrixCursor with one value:
```kotlin
val person = Person("David")
val cursor = Kontent.serializeToCursor(person)
```

... or a list which turns into a MatrixCursor with multiple rows:
```kotlin
val people = listOf(Person("David"), Person("Christofer"))
val cursor = Kontent.serializeToCursor(people)
```

### ContentValues
You can serialize an object to ContentValues by using the following code:
```kotlin
val person = Person("David")
val values: ContentValues = Kontent.serializeToContentValues(person)
```

Then when implementing the ContentProvider, you can retrieve the object with this:
```kotlin
val person: Person = Kontent.deserializeFromContentValues(values)
```
