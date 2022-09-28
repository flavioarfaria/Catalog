# Catalog

**_Disclaimer: this is a work in progress._**

Catalog is a Gradle plugin that generates type-safe, user-friendly extensions to resolve Android resources.

```xml
<string name="welcome_message">Hello, %1$s! You have %2$d unread messages.</string>
```

### Without Catalog:

Android resource resolution is overly verbose. It also uses snake case for resource identifiers,
which diverts away from Java/Kotlin naming conventions. In addition, parametrized strings and
plurals are not type safe, so you can easily get a crash if arguments passed don't match
the expected parameters defined in a string.

Here's how we resolve string resources without Catalog:

<img width="644" src="https://user-images.githubusercontent.com/1800351/192675500-9e4fff60-f2f3-4f26-8473-e4a7364530ab.png">

### With Catalog:

<img width="655" src="https://user-images.githubusercontent.com/1800351/192675528-d3463e82-197c-4f39-bfda-69fe43eceaa1.png">

You can also use Catalog to access the resource id directly:

<img width="691" alt="image" src="https://user-images.githubusercontent.com/1800351/192676118-423ead2a-3856-48b7-8637-866f64ca8ce1.png">

Catalog also works with plurals and string arrays. In the future, other resource types will also be supported.

Resource comments are also carried over to extension properties and methods:

```xml
<!-- This string resource is used in the launcher screen. -->
<string name="app_name">Catalog</string>
```

<img width="509" src="https://user-images.githubusercontent.com/1800351/192677607-06a8d538-8786-4419-98df-21ad0cd4acd5.png">

## How it works

Catalog generates `Context` and `Fragment` extensions using [context receivers](https://blog.jetbrains.com/kotlin/2022/02/kotlin-1-6-20-m1-released/#prototype-of-context-receivers-for-kotlin-jvm).
Since this language feature is still a prototype, make sure you opt-in for `-Xcontext-receivers`.

<img width="545" alt="Screen Shot 2022-09-27 at 11 14 14 PM" src="https://user-images.githubusercontent.com/1800351/192679242-be1b2d67-4b65-4e0a-a78a-14145f28dd47.png">

