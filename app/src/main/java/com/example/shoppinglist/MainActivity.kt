package com.example.shoppinglist

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.shoppinglist.presentation.ShoppingScreen
import com.example.shoppinglist.presentation.ShoppingViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ShoppingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val currentLang by viewModel.language.collectAsState()

            // Настройка локали
            val locale = Locale(currentLang)
            Locale.setDefault(locale)
            val config = Configuration(LocalContext.current.resources.configuration).apply {
                setLocale(locale)
            }
            val localizedContext = LocalContext.current.createConfigurationContext(config)

            val colorScheme = if (isDarkMode) darkColorScheme() else lightColorScheme()

            CompositionLocalProvider(LocalContext provides localizedContext) {
                MaterialTheme(colorScheme = colorScheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ShoppingScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
