package com.flaviofaria.catalog

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.flaviofaria.catalog.sample.R

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(TextView(this).apply {
      text = Strings.appName()
    })
    /*R.string.app_name_demo
    R.string.app_name_debug*/
  }

  object Strings

  context(Context)
          private fun Strings.appName(): String {
    return getString(R.string.app_name)
  }

}
