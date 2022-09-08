package com.flaviofaria.catalog

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.flaviofaria.catalog.library.libraryName
import com.flaviofaria.catalog.library.libraryStringArray
import com.flaviofaria.catalog.runtime.StringArrays
import com.flaviofaria.catalog.runtime.Strings
import com.flaviofaria.catalog.sample.appName
import com.flaviofaria.catalog.sample.someOtherStringResource
import com.flaviofaria.catalog.sample.someStringArray

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(TextView(this).apply {
      //text = Strings.stringWithNonPositionedArgs(1, 2)
      text = Strings.someOtherStringResource()
      //com.flaviofaria.catalog.library.Strings.libraryName()
      //text = Plurals.somePlural(2, 1, 2)
    })
    Strings.appName()
    StringArrays.someStringArray()
    Strings.libraryName()
    StringArrays.libraryStringArray()
    //stringResource(id = )
  }
}
