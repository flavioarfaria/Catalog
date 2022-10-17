package com.flaviofaria.catalog.sample.resources

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.flaviofaria.catalog.library.main.libraryName
import com.flaviofaria.catalog.library.main.libraryStringArray
import com.flaviofaria.catalog.runtime.Plurals
import com.flaviofaria.catalog.runtime.StringArrays
import com.flaviofaria.catalog.runtime.Strings
import com.flaviofaria.catalog.sample.resources.demo.appName
import com.flaviofaria.catalog.sample.resources.main.someOtherStringResource
import com.flaviofaria.catalog.sample.resources.main.somePlural
import com.flaviofaria.catalog.sample.resources.main.someStringArray

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(TextView(this).apply {
      Strings.appName()
      StringArrays.someStringArray()
      Strings.libraryName()
      StringArrays.libraryStringArray()
      Plurals.somePlural(3, 1, 2)
      text = Strings.someOtherStringResource()
    })
  }
}
