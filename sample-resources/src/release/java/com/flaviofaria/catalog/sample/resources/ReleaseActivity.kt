package com.flaviofaria.catalog.sample.resources

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flaviofaria.catalog.runtime.Strings

class ReleaseActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Strings.releaseString()
  }
}
