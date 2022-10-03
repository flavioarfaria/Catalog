package com.flaviofaria.catalog.sample.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import com.flaviofaria.catalog.library.libraryName
import com.flaviofaria.catalog.library.libraryStringArray
import com.flaviofaria.catalog.runtime.Plurals
import com.flaviofaria.catalog.runtime.StringArrays
import com.flaviofaria.catalog.runtime.Strings

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      Strings.appName()
      StringArrays.someStringArray()
      Strings.libraryName()
      StringArrays.libraryStringArray()
      Plurals.somePlural(3, 1, 2)
      Text(text = Strings.composable().toString())
    }
  }
}
