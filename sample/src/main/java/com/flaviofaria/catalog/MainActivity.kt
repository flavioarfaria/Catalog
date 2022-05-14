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
  }

  object Strings {

    context(Context)
    fun appName(): String {
      return getString(R.string.app_name)
    }
  }

}
