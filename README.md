# Catalog

**_Disclaimer: this is a work in progress._**

Catalog is a Gradle plugin that generates user-friendly extensions to resolve Android resources.

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

## How it works

Catalog generates `Context` and `Fragment` extensions using [context receivers](https://blog.jetbrains.com/kotlin/2022/02/kotlin-1-6-20-m1-released/#prototype-of-context-receivers-for-kotlin-jvm).
Since this language feature is still a prototype, make sure you opt-in for `-Xcontext-receivers`.

<img width="546" alt="image" src="https://user-images.githubusercontent.com/1800351/192676257-5235ee2f-430a-4e87-ad1b-67a349051a8d.png">
