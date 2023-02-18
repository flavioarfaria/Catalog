# Catalog

_Just like View Binding, but for resources._

Catalog is a Gradle plugin that generates type-safe, user-friendly extensions to resolve Android resources.

Let's see how the following string resource gets resolved without and with Catalog:

```xml
<string name="good_morning_with_weather">Good morning, %1$s! It’s %2$d°C outside.</string>
```

### Without Catalog:

Android resource resolution is overly verbose. It also uses snake case for resource identifiers,
which diverts away from Java/Kotlin naming conventions. In addition, parametrized strings and
plurals are not type safe. Android Studio comes with a very loose lint check for argument types,
but it treats all arguments as optional and it won't flag if you miss one.

Here's how we resolve string resources without Catalog:

<img width="644" src="https://user-images.githubusercontent.com/1800351/201457373-cfae9625-1add-4e43-93ec-771c69efb406.png">

### With Catalog:

<img width="725" alt="image" src="https://user-images.githubusercontent.com/1800351/203698324-cfb73cc8-dca2-4eda-8287-64ce74fa2367.png">

You can also use Catalog to access the resource id directly:

<img width="691" alt="image" src="https://user-images.githubusercontent.com/1800351/201457666-44ecff64-bc0e-4d55-9020-b1982fcfd331.png">

Catalog also works with plurals:

```xml
<plurals name="unread_messages">
  <item quantity="one">You have %1$d unread message</item>
  <item quantity="other">You have %1$d unread messages</item>
</plurals>
```

<img width="691" alt="image" src="https://user-images.githubusercontent.com/1800351/201458751-48d31e9a-d683-4006-80a3-2c7e9734e65d.png">

string arrays:

```xml
<string-array name="seasons">
  <item>Spring</item>
  <item>Summer</item>
  <item>Fall</item>
  <item>Winter</item>
</string-array>
```

<img width="691" alt="image" src="https://user-images.githubusercontent.com/1800351/201458738-5c6d1b9b-af88-40fb-a3b9-730a140bfcdf.png">

and simple color resources:

```xml
<color name="red">#FFFF0000</color>
```

<img width="689" alt="image" src="https://user-images.githubusercontent.com/1800351/219828664-70e5080b-ffff-43d9-91e6-4f254cfda855.png">

In the future, other resource types like integer arrays, dimensions, etc. will also be supported.

### Comment support

Resource comments are also carried over to extension properties and methods:

```xml
<!-- This string resource is used in the launcher screen. -->
<string name="app_name">Catalog</string>
```

<img width="509" src="https://user-images.githubusercontent.com/1800351/192677607-06a8d538-8786-4419-98df-21ad0cd4acd5.png">

### Compose support

If you're using compose, `@Composable` extensions will also be generated. In order to avoid
signature clashes, Compose extensions are extension methods of
`com.flaviofaria.catalog.runtime.compose.[String, Plurals, StringArray]` whereas standard extensions
are extension methods of `com.flaviofaria.catalog.runtime.resources.[String, Plurals, StringArray]`,
so make sure you're importing the right class.

<img width="650" src="https://user-images.githubusercontent.com/1800351/201458064-9fff8e50-86fe-4d2c-8b7b-345ff7d87e81.png">

## How it works

Catalog generates `Context` and `Fragment` extensions using [context receivers](https://blog.jetbrains.com/kotlin/2022/02/kotlin-1-6-20-m1-released/#prototype-of-context-receivers-for-kotlin-jvm).

<img width="545" alt="Screen Shot 2022-09-27 at 11 14 14 PM" src="https://user-images.githubusercontent.com/1800351/192679242-be1b2d67-4b65-4e0a-a78a-14145f28dd47.png">

Since these extensions are `inline`, there will be no increase in your app method count or any
significant impact on runtime performance.

## Setup and configuration

To use Catalog, just apply the plugin to your module:

```groovy
plugins {
  id 'com.flaviofaria.catalog' version '0.1.0'
}
```

By default, Catalog generates non-Compose extensions only. Compose extensions will also be generated
if it detects Compose among your module dependencies. If your project is 100% written in Compose,
you can explicitly turn off non-Compose extensions by adding:

```groovy
catalog {
  generateResourcesExtensions = false
}
```

Similarly, you can also turn off Compose extensions:

```groovy
catalog {
  generateComposeExtensions = false
}
```
