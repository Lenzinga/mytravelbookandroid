package com.example.mytravelbook.online

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mytravelbook.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineEntryDetailScreen(
    diaryId: String,
    viewModel: OnlineEntriesViewModel,
    onBack: () -> Unit
) {
    // Load the single entry from the API
    LaunchedEffect(diaryId) {
        viewModel.loadOnlineEntryById(diaryId)
    }

    val selectedEntry by viewModel.selectedOnlineEntry.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Online Entry Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrowback),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (selectedEntry == null) {
                Text("Loading or entry not found.")
            } else {
                val entry = selectedEntry!!
                Text(text = "Title: ${entry.title}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Text: ${entry.text}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Location: ${entry.locationName ?: "Unknown"}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Date: ${entry.dateTime}")
                Spacer(modifier = Modifier.height(16.dp))

                if (entry.images.isNotEmpty()) {
                    Text("Images:")
                    entry.images.forEach { remoteImage ->
                        AsyncImage(
                            model = remoteImage.url,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(vertical = 8.dp)
                        )
                    }
                } else {
                    Text("No images.")
                }
            }
        }
    }
}
