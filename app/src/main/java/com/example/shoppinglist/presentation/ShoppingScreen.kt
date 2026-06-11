package com.example.shoppinglist.presentation

import com.emil.shoppinglist.R
import android.content.Intent
import android.os.Build
import com.example.shoppinglist.ShoppingForegroundService
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.shoppinglist.domain.ShoppingItem
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(viewModel: ShoppingViewModel) {
    val shoppingList by viewModel.shoppingList.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val currentLang by viewModel.language.collectAsState()
    var editingItem by remember { mutableStateOf<ShoppingItem?>(null) }

    var showAddDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Динамически переключаем конфигурацию контекста приложения при смене языка в DataStore
    LaunchedEffect(currentLang) {
        val locale = Locale(currentLang)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    val (toBuy, bought) = shoppingList.partition { !it.isBought }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = {
                        viewModel.changeLanguage(if (currentLang == "ru") "en" else "ru")
                    }) {
                        Icon(Icons.Default.Language, contentDescription = "Language")
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.toggleTheme(it) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            )
        },
        floatingActionButton = {
            // Закон Фиттса: Крупная кнопка добавления внизу в зоне доступности большого пальца
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Закон Фиттса: Крупная, высокая кнопка (56dp) для старта фонового сервиса
            Button(
                onClick = {
                    val intent = Intent(context, ShoppingForegroundService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.ShoppingBag, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.btn_go_shopping), style = MaterialTheme.typography.titleMedium)
            }

            if (shoppingList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.empty_list), style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    // Закон Миллера: Чётко разделяем элементы на смысловые группы (не более 7 элементов в фокусе)
                    if (toBuy.isNotEmpty()) {
                        item { CategoryHeader(stringResource(R.string.title_buy)) }
                        items(toBuy, key = { it.id }) { item ->
                            ShoppingListItem(
                                item = item,
                                onClick = { editingItem = item },
                                onDelete = { viewModel.deleteProduct(item) }
                            )
                        }
                    }
                    if (bought.isNotEmpty()) {
                        item { CategoryHeader(stringResource(R.string.title_bought)) }
                        items(bought, key = { it.id }) { item ->
                            ShoppingListItem(
                                item = item,
                                onClick = { editingItem = item },
                                onDelete = { viewModel.deleteProduct(item) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Закон Хика: Избавляемся от перегрузки экрана лишними деталями, убирая ввод в диалог
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var quantity by remember { mutableStateOf("1") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(stringResource(R.string.btn_add)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(R.string.hint_product_name)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text(stringResource(R.string.hint_quantity)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.addProduct(name, quantity.toIntOrNull() ?: 1)
                    showAddDialog = false
                }) {
                    Text("OK")
                }
            }
        )
    }
    editingItem?.let { item ->
        EditItemDialog(
            item = item,
            onDismiss = { editingItem = null },
            onSave = { newName, newQty ->
                viewModel.updateProduct(item, newName, newQty)
                editingItem = null
            },
            onToggleStatus = {
                viewModel.toggleBoughtStatus(item)
                editingItem = null
            }
        )
    }
}

@Composable
fun CategoryHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShoppingListItem(item: ShoppingItem, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onDelete
                )
                .padding(16.dp), // Большая кликабельная область по Фиттсу (> 48dp)
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${item.name} (${item.quantity})",
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (item.isBought) TextDecoration.LineThrough else TextDecoration.None
                )
            )
            if (item.isBought) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun EditItemDialog(
    item: ShoppingItem,
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit,
    onToggleStatus: () -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    var quantity by remember { mutableStateOf(item.quantity.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать / Edit") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название / Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Количество / Qty") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(name, quantity.toIntOrNull() ?: 1)
            }) { Text("Сохранить") }
        },
        dismissButton = {
            Button(onClick = onToggleStatus) {
                Text(if (item.isBought) "Вернуть" else "Вычеркнуть")
            }
        }
    )
}


