package com.flaviofaria.catalog

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.flaviofaria.catalog.sample.Plurals
import com.flaviofaria.catalog.sample.StringArrays
import com.flaviofaria.catalog.sample.Strings

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(TextView(this).apply {
      text = Strings.stringWithNonPositionedArgs(1, 2)
      text = Plurals.somePlural(2, 1, 2)
    })
    Strings.appName()
    StringArrays.someStringArray()
    //stringResource(id = )
  }
}
