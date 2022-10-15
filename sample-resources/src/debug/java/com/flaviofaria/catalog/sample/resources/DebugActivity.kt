package com.flaviofaria.catalog.sample.resources

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flaviofaria.catalog.runtime.Strings
import com.flaviofaria.catalog.sample.resources.debug.debugString

class DebugActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Strings.debugString()
  }
}
