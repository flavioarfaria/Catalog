package com.flaviofaria.catalog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
import com.flaviofaria.catalog.library.libraryName
import com.flaviofaria.catalog.library.libraryStringArray
import com.flaviofaria.catalog.runtime.Plurals
import com.flaviofaria.catalog.runtime.StringArrays
import com.flaviofaria.catalog.runtime.Strings
import com.flaviofaria.catalog.sample.*
import com.flaviofaria.catalog.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = ActivityMainBinding.inflate(layoutInflater, null, false)
    setContentView(binding.root)
    binding.regularTextView.text = Strings.someOtherStringResource()
    binding.composeView.apply {
      setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)
      setContent {
        Text(text = Strings.composable().toString())
      }
    }
    Strings.appName()
    StringArrays.someStringArray()
    Strings.libraryName()
    StringArrays.libraryStringArray()
    Plurals.somePlural(3, 1, 2)
  }
}
