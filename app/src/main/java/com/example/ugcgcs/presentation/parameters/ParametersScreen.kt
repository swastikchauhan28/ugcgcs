package com.example.ugcgcs.presentation.parameters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.ugcgcs.data.mock.MockGcsStore
import com.example.ugcgcs.presentation.components.PageHeading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ParametersViewModel @Inject constructor(private val store: MockGcsStore) : ViewModel() {
    val parameters = store.parameters.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), store.parameters.value)
    fun set(name: String, value: String) = store.updateParameter(name, value)
}

@Composable
fun ParametersScreen(viewModel: ParametersViewModel = hiltViewModel()) {
    val parameters by viewModel.parameters.collectAsStateWithLifecycle()
    LazyColumn(Modifier.fillMaxSize().background(Color(0xFF0A1015))) {
        item { PageHeading("Parameters", "Editable mock parameter values • no PARAM_SET is transmitted") }
        items(parameters, key = { it.name }) { parameter ->
            Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 5.dp).background(Color(0xFF101A22)).padding(12.dp)) {
                Row { Text(parameter.name, color = Color(0xFFE7F0F5), fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f)) }
                Text(parameter.description, color = Color(0xFF91A5B4), fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp))
                OutlinedTextField(value = parameter.value, onValueChange = { viewModel.set(parameter.name, it) }, singleLine = true, label = { Text("Mock value") }, modifier = Modifier.fillMaxWidth().padding(top = 7.dp))
            }
        }
    }
}
