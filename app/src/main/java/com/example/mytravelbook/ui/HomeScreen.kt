package com.example.mytravelbook.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytravelbook.R
import com.example.mytravelbook.viewmodel.EntryViewModel
import com.example.mytravelbook.viewmodel.TripViewModel
import com.example.mytravelbook.online.OnlineEntriesViewModel
import com.example.mytravelbook.online.OnlineEntriesScreen
import com.example.mytravelbook.online.OnlineEntryDetailScreen
import androidx.compose.ui.res.stringResource

enum class HomeTabs {
    MY_TRIPS,
    ONLINE_ENTRIES;

    @Composable
    fun getTitle(): String {
        return when (this) {
            MY_TRIPS -> stringResource(id = R.string.my_trips)
            ONLINE_ENTRIES -> stringResource(id = R.string.online_entries)
        }
    }
}


@Composable
fun HomeScreen(
    tripViewModel: TripViewModel,
    entryViewModel: EntryViewModel,
    onNavigateToTripDetails: (Int) -> Unit,
    onNavigateToEntryDetails: (Int) -> Unit
) {
    // Keep your existing offline code as-is for MY_TRIPS usage...
    val onlineEntriesViewModel: OnlineEntriesViewModel = viewModel()

    // We'll track which tab is selected
    var selectedTab by remember { mutableStateOf(HomeTabs.MY_TRIPS) }
    // We'll track the currently selected remote entry ID for read-only detail
    var selectedOnlineDiaryId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = (selectedTab == HomeTabs.MY_TRIPS),
                    onClick = { selectedTab = HomeTabs.MY_TRIPS },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_addphoto),
                            contentDescription = stringResource(id = R.string.my_trips)
                        )
                    },
                    label = { Text(HomeTabs.MY_TRIPS.getTitle()) }
                )

                NavigationBarItem(
                    selected = (selectedTab == HomeTabs.ONLINE_ENTRIES),
                    onClick = {
                        selectedTab = HomeTabs.ONLINE_ENTRIES
                        onlineEntriesViewModel.loadAllOnlineEntries()
                        selectedOnlineDiaryId = null
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cloud),
                            contentDescription = stringResource(id = R.string.online_entries)
                        )
                    },
                    label = { Text(HomeTabs.ONLINE_ENTRIES.getTitle()) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                HomeTabs.MY_TRIPS -> {
                    // Original offline code
                    MyTripsScreen(
                        tripViewModel = tripViewModel,
                        onTripSelected = onNavigateToTripDetails
                    )
                }
                HomeTabs.ONLINE_ENTRIES -> {
                    // If user has NOT yet clicked an online entry, show the list.
                    if (selectedOnlineDiaryId == null) {
                        OnlineEntriesScreen(
                            onlineEntriesViewModel = onlineEntriesViewModel,
                            onEntryClicked = { diaryId ->
                                // When user clicks an item, show detail
                                selectedOnlineDiaryId = diaryId
                            }
                        )
                    } else {
                        // Show read-only detail for the clicked remote entry
                        OnlineEntryDetailScreen(
                            diaryId = selectedOnlineDiaryId!!,
                            viewModel = onlineEntriesViewModel,
                            onBack = {
                                // Return to the list
                                selectedOnlineDiaryId = null
                            }
                        )
                    }
                }
            }
        }
    }
}
