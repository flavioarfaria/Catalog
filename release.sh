#!/bin/bash
sed -i '' -e "s/id 'com.flaviofaria.catalog' version '$1'/id 'com.flaviofaria.catalog' version '$2'/g" README.md
sed -i '' -e "s/RUNTIME_VERSION = \"$1\"/RUNTIME_VERSION = \"$2\"/g" catalog-gradle-plugin/src/main/java/com/flaviofaria/catalog/gradle/CatalogPlugin.kt
sed -i '' -e "s/catalog = \"$1\"/catalog = \"$2\"/g" gradle/libs.versions.toml

