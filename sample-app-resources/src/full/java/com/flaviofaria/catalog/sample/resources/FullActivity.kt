package com.flaviofaria.catalog.sample.resources

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flaviofaria.catalog.runtime.resources.Plurals

class FullActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Plurals.somePlural(1, 2, 3)
  }
}
