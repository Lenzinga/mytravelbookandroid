package com.example.mytravelbook.online

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineEntriesScreen(
    onlineEntriesViewModel: OnlineEntriesViewModel,
    onEntryClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Optionally load once (if you also call it from HomeScreen, you can remove this)
    LaunchedEffect(Unit) {
        onlineEntriesViewModel.loadAllOnlineEntries()
    }

    val onlineEntries by onlineEntriesViewModel.onlineEntries.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Online Entries") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (onlineEntries.isEmpty()) {
                Text("Loading or no online entries found.")
            } else {
                LazyColumn {
                    items(onlineEntries) { remoteEntry ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onEntryClicked(remoteEntry.id) }
                                .padding(8.dp),
                            tonalElevation = 1.dp
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text = remoteEntry.title.ifBlank { "No Title" })
                            }
                        }
                    }
                }
            }
        }
    }
}
