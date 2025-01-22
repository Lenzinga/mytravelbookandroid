package com.example.mytravelbook.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mytravelbook.R
import com.example.mytravelbook.data.Entry
import com.example.mytravelbook.data.Image
import com.example.mytravelbook.viewmodel.EntryViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailsScreen(
    entryId: Int,
    entryViewModel: EntryViewModel,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Load the entry if valid ID
    var currentEntry by remember { mutableStateOf<Entry?>(null) }
    LaunchedEffect(entryId) {
        if (entryId > 0) {
            currentEntry = entryViewModel.getEntryById(entryId)
        }
    }

    val errorMessage by entryViewModel.publishError.collectAsState()

    // State for editable fields
    var title by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    // Once we have the entry, populate text fields
    LaunchedEffect(currentEntry) {
        currentEntry?.let { entry ->
            title = entry.title
            text = entry.text
            location = entry.location ?: ""
        }
    }

    // Format the timestamp
    val formattedTimestamp = remember(currentEntry) {
        currentEntry?.let { entry ->
            val sdf = SimpleDateFormat("dd:MM:yy HH:mm", Locale.getDefault())
            sdf.format(Date(entry.timestamp))
        } ?: ""
    }

    // Observe images from DB
    val dbImagesFlow = remember(entryId) { entryViewModel.getImagesForEntry(entryId) }
    val dbImages by dbImagesFlow.collectAsState(emptyList())

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris != null && currentEntry != null) {
            // Persist read permissions & add them to DB immediately
            uris.forEach { uri ->
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                scope.launch {
                    // Insert directly into DB
                    entryViewModel.addImage(
                        entryId = currentEntry!!.id,
                        imageUri = uri.toString()
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.entry_details)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrowback),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            // Show "Publish" FAB only if entry is not published
            if (currentEntry?.isPublished == false) {
                ExtendedFloatingActionButton(
                    onClick = {
                        scope.launch {
                            currentEntry?.let { entry ->
                                // Update DB with text fields
                                val updatedEntry = entry.copy(
                                    title = title,
                                    text = text,
                                    location = location.ifEmpty { null }
                                )
                                entryViewModel.updateEntry(updatedEntry)

                                // Then publish
                                entryViewModel.publishEntry(context, updatedEntry)
                            }
                        }
                    }
                ) {
                    Text(stringResource(id = R.string.publish))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Timestamp
            if (formattedTimestamp.isNotEmpty()) {
                Text(stringResource(id = R.string.timestamp, formattedTimestamp))
                Spacer(modifier = Modifier.height(8.dp))
            }

            // TITLE
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(id = R.string.title)) },
                singleLine = true,
                maxLines = 1,
                enabled = currentEntry?.isPublished == false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // TEXT
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(stringResource(id = R.string.text)) },
                enabled = currentEntry?.isPublished == false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // LOCATION
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(stringResource(id = R.string.location)) },
                singleLine = true,
                maxLines = 1,
                enabled = currentEntry?.isPublished == false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // SAVE BUTTON (only if not published)
            if (currentEntry?.isPublished == false) {
                Button(
                    onClick = {
                        scope.launch {
                            currentEntry?.let { entry ->
                                // Update text fields in DB
                                val updated = entry.copy(
                                    title = title,
                                    text = text,
                                    location = location.ifEmpty { null }
                                )
                                entryViewModel.updateEntry(updated)
                            }
                            // Return to previous screen
                            onBack()
                        }
                    }
                ) {
                    Text(stringResource(id = R.string.save))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Button to pick images from the gallery
                Button(onClick = {
                    // Launch the image picker
                    galleryLauncher.launch(arrayOf("image/*"))
                }) {
                    Text(stringResource(id = R.string.pick_images_from_gallery))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show existing DB images (with long-press to delete)
            Text(text = stringResource(id = R.string.images_in_db))
            if (dbImages.isEmpty()) {
                Text(stringResource(id = R.string.no_images))
            } else {
                DeletableImageRow(
                    dbImages = dbImages,
                    onDeleteImage = { image ->
                        scope.launch {
                            entryViewModel.deleteImage(image)
                        }
                    }
                )
            }

            // Show any publish error
            errorMessage?.let { msg ->
                AlertDialog(
                    onDismissRequest = { entryViewModel.clearPublishError() },
                    title = { Text(stringResource(id = R.string.publish_error)) },
                    text = { Text(msg) },
                    confirmButton = {
                        Button(onClick = { entryViewModel.clearPublishError() }) {
                            Text(stringResource(id = R.string.ok))
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DeletableImageRow(
    dbImages: List<Image>,
    onDeleteImage: (Image) -> Unit
) {
    // Local state to show/hide alert for a single image
    var imageToDelete by remember { mutableStateOf<Image?>(null) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        items(dbImages) { image ->
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxHeight()
                    .combinedClickable(
                        onClick = { /* Optional preview? */ },
                        onLongClick = {
                            imageToDelete = image
                        }
                    )
            ) {
                AsyncImage(
                    model = image.imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }

    // Confirm deletion dialog
    imageToDelete?.let { img ->
        AlertDialog(
            onDismissRequest = { imageToDelete = null },
            title = { Text(stringResource(id = R.string.confirm_deletion)) },
            text = {
                Text(text = stringResource(id = R.string.delete_image_question))
            },
            confirmButton = {
                Button(onClick = {
                    onDeleteImage(img)
                    imageToDelete = null
                }) {
                    Text(stringResource(id = R.string.yes_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { imageToDelete = null }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }
}
