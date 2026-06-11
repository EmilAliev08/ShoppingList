package com.example.shoppinglist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppinglist.data.local.SettingsManager
import com.example.shoppinglist.domain.ShoppingItem
import com.example.shoppinglist.domain.usecase.GetShoppingListUseCase
import com.example.shoppinglist.domain.usecase.ManageItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    getShoppingListUseCase: GetShoppingListUseCase,
    private val manageItemUseCase: ManageItemUseCase,
    private val settingsManager: SettingsManager
) : ViewModel() {

    // Преобразуем Flow из базы Room в StateFlow для Compose UI
    val shoppingList: StateFlow<List<ShoppingItem>> = getShoppingListUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Подтягиваем настройки темной темы и языка из DataStore Flow
    val isDarkMode: StateFlow<Boolean> = settingsManager.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val language: StateFlow<String> = settingsManager.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "ru")

    fun addProduct(name: String, quantity: Int) {
        viewModelScope.launch { manageItemUseCase.addProduct(name, quantity) }
    }

    fun deleteProduct(item: ShoppingItem) {
        viewModelScope.launch { manageItemUseCase.deleteProduct(item) }
    }

    fun toggleBoughtStatus(item: ShoppingItem) {
        viewModelScope.launch { manageItemUseCase.toggleBoughtStatus(item) }
    }

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch { settingsManager.toggleDarkMode(isDark) }
    }

    fun changeLanguage(lang: String) {
        viewModelScope.launch { settingsManager.setLanguage(lang) }
    }

    fun updateProduct(item: ShoppingItem, newName: String, newQuantity: Int) {
        viewModelScope.launch { manageItemUseCase.updateProduct(item, newName, newQuantity) }
    }
}
