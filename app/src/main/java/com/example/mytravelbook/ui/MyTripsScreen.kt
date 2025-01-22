package com.example.mytravelbook.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.combinedClickable
import com.example.mytravelbook.data.Trip
import com.example.mytravelbook.viewmodel.TripViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import com.example.mytravelbook.R
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyTripsScreen(
    tripViewModel: TripViewModel,
    onTripSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val trips by tripViewModel.trips.collectAsState()

    // State for showing a dialog to add a new trip
    var showDialog by remember { mutableStateOf(false) }
    var newTripName by remember { mutableStateOf("") }

    // For the "delete confirmation" dialog
    var tripToDelete by remember { mutableStateOf<Trip?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        // List of trips
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(trips) { trip ->
                TripListItem(
                    trip = trip,
                    onClick = { onTripSelected(trip.id) },
                    onLongClick = { tripToDelete = trip }
                )
            }
        }

        // FAB for adding a new trip
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("+")
        }
    }

    // Dialog to create a new trip
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(id = R.string.add_new_trip)) },
            text = {
                OutlinedTextField(
                    value = newTripName,
                    onValueChange = { newTripName = it },
                    label = { Text(stringResource(id = R.string.trip_name)) },
                    singleLine = true,
                    maxLines = 1
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTripName.isNotBlank()) {
                            tripViewModel.addTrip(newTripName)
                        }
                        newTripName = ""
                        showDialog = false
                    }
                ) {
                    Text(stringResource(id = R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    tripToDelete?.let { trip ->
        AlertDialog(
            onDismissRequest = { tripToDelete = null },
            title = { Text(stringResource(id = R.string.confirm_deletion)) },
            text = { Text(stringResource(id = R.string.confirm_entry_deletion, trip.name)) },
            confirmButton = {
                Button(onClick = {
                    tripViewModel.deleteTripWithEntries(trip)
                    tripToDelete = null
                }) {
                    Text(stringResource(id = R.string.yes_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { tripToDelete = null }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TripListItem(
    trip: Trip,
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
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = trip.name, style = MaterialTheme.typography.titleLarge)
        }
    }
}
