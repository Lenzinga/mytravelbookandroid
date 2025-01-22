package com.example.mytravelbook.ui

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mytravelbook.R
import com.example.mytravelbook.data.Entry
import com.example.mytravelbook.viewmodel.EntryViewModel
import com.example.mytravelbook.viewmodel.TripViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TripDetailsScreen(
    tripId: Int,
    tripViewModel: TripViewModel,
    entryViewModel: EntryViewModel,
    onEntryClicked: (Int) -> Unit,
    onBack: () -> Unit
) {
    val entries by entryViewModel.entriesForSelectedTrip.collectAsState()

    // For loading the Trip name
    var tripName by remember { mutableStateOf("") }

    // Fetch the trip from DB once
    LaunchedEffect(tripId) {
        val trip = tripViewModel.getTripById(tripId)
        tripName = trip?.name ?: ""
    }

    // Controls for adding a new entry
    var showAddEntryDialog by remember { mutableStateOf(false) }

    // For the "delete entry" dialog
    var entryToDelete by remember { mutableStateOf<Entry?>(null) }

    // If we might need READ_EXTERNAL_STORAGE for picking images (Android < 33)
    val readStoragePermissionState: PermissionState? = if (Build.VERSION.SDK_INT < 33) {
        rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    } else {
        null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tripName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrowback),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddEntryDialog = true }) {
                Text("+")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(entries) { entry ->
                EntryListItem(
                    entry = entry,
                    onClick = { onEntryClicked(entry.id) },
                    onLongClick = { entryToDelete = entry }
                )
            }
        }
    }

    // Dialog to add a new entry (with images from the gallery).
    if (showAddEntryDialog) {
        NewEntryDialog(
            tripId = tripId,
            entryViewModel = entryViewModel,
            readStoragePermissionState = readStoragePermissionState,
            onDismiss = { showAddEntryDialog = false }
        )
    }

    // Dialog to confirm entry deletion
    entryToDelete?.let { entry ->
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = { Text(stringResource(id = R.string.confirm_deletion)) },
            text = {
                Text(
                    text = stringResource(
                        id = R.string.delete_trip,
                        entry.title
                    )
                )
            },
            confirmButton = {
                Button(onClick = {
                    entryViewModel.deleteEntry(entry)
                    entryToDelete = null
                }) {
                    Text(stringResource(id = R.string.yes_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }
}

/**
 * Dialog for creating a new Entry with optional images.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NewEntryDialog(
    tripId: Int,
    entryViewModel: EntryViewModel,
    readStoragePermissionState: PermissionState?,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var newTitle by remember { mutableStateOf("") }
    var newText by remember { mutableStateOf("") }
    var newLocation by remember { mutableStateOf("") }

    // Holds the user's currently selected URIs for the new entry
    var selectedUris by remember { mutableStateOf(emptyList<android.net.Uri>()) }

    // Use OpenMultipleDocuments to support multiple selection & persist URIs
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris != null) {
            // Overwrite any previous selection
            android.util.Log.d("NewEntryDialog", "User selected ${uris.size} URIs.")
            selectedUris = emptyList()

            // Take persistable permissions for each newly picked URI
            uris.forEach { uri ->
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {
                    android.util.Log.e(
                        "NewEntryDialog",
                        "takePersistableUriPermission failed for $uri",
                        e
                    )
                }
            }
            selectedUris = uris
        }
    }

    val requestGalleryImages: () -> Unit = {
        if (readStoragePermissionState != null) {
            if (readStoragePermissionState.status.isGranted) {
                galleryLauncher.launch(arrayOf("image/*"))
            } else {
                android.util.Log.d("NewEntryDialog", "Requesting READ_EXTERNAL_STORAGE permission...")
                readStoragePermissionState.launchPermissionRequest()
            }
        } else {
            // Android 33+ -> no explicit READ permission needed
            galleryLauncher.launch(arrayOf("image/*"))
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.new_entry)) },
        text = {
            Column {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text(stringResource(id = R.string.title)) },
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newText,
                    onValueChange = { newText = it },
                    label = { Text(stringResource(id = R.string.text)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newLocation,
                    onValueChange = { newLocation = it },
                    label = { Text(stringResource(id = R.string.location)) },
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = { requestGalleryImages() }) {
                    Text(stringResource(id = R.string.pick_images))
                }

                if (selectedUris.isNotEmpty()) {
                    Text(text = stringResource(id = R.string.selected_images))
                    selectedUris.forEach { uri ->
                        Text(uri.toString())
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                scope.launch {
                    try {
                        // Make sure user actually entered both title & text
                        if (newTitle.isNotBlank() && newText.isNotBlank()) {
                            android.util.Log.d("NewEntryDialog", "Inserting new Entry: $newTitle")

                            // 1) Insert the new Entry in DB
                            val newEntryId = entryViewModel.insertEntryAndReturnId(
                                tripId = tripId,
                                title = newTitle,
                                text = newText,
                                location = newLocation.ifEmpty { null }
                            )
                            android.util.Log.d(
                                "NewEntryDialog",
                                "New Entry ID = $newEntryId. Inserting images..."
                            )

                            // 2) Insert associated images in DB
                            selectedUris.forEach { uri ->
                                android.util.Log.d("NewEntryDialog", "Inserting image for $uri")
                                entryViewModel.addImage(
                                    entryId = newEntryId.toInt(),
                                    imageUri = uri.toString()
                                )
                            }

                            android.util.Log.d("NewEntryDialog", "Finished inserting images.")
                        } else {
                            android.util.Log.d("NewEntryDialog", "Title or text is blank. Skipping insert.")
                        }
                    } catch (e: Exception) {
                        android.util.Log.e(
                            "NewEntryDialog",
                            "Error inserting entry or images: ${e.message}",
                            e
                        )
                    }

                    // **IMPORTANT**: Dismiss the dialog **after** the insertion
                    onDismiss()
                }
            }) {
                Text(stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EntryListItem(
    entry: Entry,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Surface(
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        // "published" text removed as per request
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = entry.title, style = MaterialTheme.typography.titleMedium)
        }
    }
}
