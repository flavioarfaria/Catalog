package com.flaviofaria.catalog.sample.resources

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flaviofaria.catalog.runtime.Strings
import com.flaviofaria.catalog.sample.resources.demo.appName

class DemoActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Strings.appName()
  }
}
