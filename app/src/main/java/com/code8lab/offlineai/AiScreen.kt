package com.code8lab.offlineai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AiScreen(vm: AiViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Offline AI", style = MaterialTheme.typography.headlineMedium)
        Text(state.status, style = MaterialTheme.typography.labelLarge)

        OutlinedTextField(
            value = state.prompt,
            onValueChange = vm::onPromptChange,
            label = { Text("Ask anything") },
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = vm::send,
            enabled = state.ready && !state.busy,
        ) { Text(if (state.busy) "Thinking..." else "Send") }

        if (state.answer.isNotEmpty()) {
            Card(Modifier.fillMaxWidth()) {
                Text(state.answer, Modifier.padding(16.dp))
            }
        }
    }
}
