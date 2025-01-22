package com.example.mytravelbook.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mytravelbook.viewmodel.EntryViewModel
import com.example.mytravelbook.viewmodel.TripViewModel

// Routes for navigation
private const val ROUTE_WELCOME = "welcome"
private const val ROUTE_HOME = "home"
private const val ROUTE_TRIP_DETAILS = "tripDetails"
private const val ROUTE_ENTRY_DETAILS = "entryDetails"

@Composable
fun MyTravelBookNavHost(
    isFirstLaunch: Boolean,
    tripViewModel: TripViewModel,
    entryViewModel: EntryViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val startDestination = remember(isFirstLaunch) {
        if (isFirstLaunch) ROUTE_WELCOME else ROUTE_HOME
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = ROUTE_WELCOME) {
            // NEW: Pass isFirstLaunch down to the WelcomeScreen
            WelcomeScreen(
                isFirstLaunch = isFirstLaunch,
                onGetStartedClicked = {
                    // Navigate to Home Screen
                    navController.navigate(ROUTE_HOME) {
                        // Pop the welcome screen so user can’t go back to it
                        popUpTo(ROUTE_WELCOME) { inclusive = true }
                    }
                }
            )
        }

        composable(route = ROUTE_HOME) {
            HomeScreen(
                tripViewModel = tripViewModel,
                entryViewModel = entryViewModel,
                onNavigateToTripDetails = { tripId ->
                    navController.navigate("$ROUTE_TRIP_DETAILS/$tripId")
                },
                onNavigateToEntryDetails = { entryId ->
                    navController.navigate("$ROUTE_ENTRY_DETAILS/$entryId")
                }
            )
        }

        // Trip Details Screen
        composable(
            route = "$ROUTE_TRIP_DETAILS/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: -1
            entryViewModel.setSelectedTrip(tripId)

            TripDetailsScreen(
                tripId = tripId,
                tripViewModel = tripViewModel,
                entryViewModel = entryViewModel,
                onEntryClicked = { entryId ->
                    navController.navigate("$ROUTE_ENTRY_DETAILS/$entryId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Entry Details Screen
        composable(
            route = "$ROUTE_ENTRY_DETAILS/{entryId}",
            arguments = listOf(navArgument("entryId") { type = NavType.IntType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getInt("entryId") ?: -1
            EntryDetailsScreen(
                entryId = entryId,
                entryViewModel = entryViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
