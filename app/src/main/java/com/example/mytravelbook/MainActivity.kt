package com.example.mytravelbook

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytravelbook.data.AppDatabase
import com.example.mytravelbook.data.AppState
import com.example.mytravelbook.ui.MyTravelBookNavHost
import com.example.mytravelbook.ui.theme.MyTravelBookTheme
import com.example.mytravelbook.viewmodel.AppViewModelFactory
import com.example.mytravelbook.viewmodel.EntryViewModel
import com.example.mytravelbook.viewmodel.TripViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1) Retrieve the SharedPreferences
        val prefs = getSharedPreferences("my_travel_book_prefs", MODE_PRIVATE)

        // 2) Check if this is the first launch (default true if key not found)
        val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)

        // 3) If it is the first launch, store false so next time it won't show
        if (isFirstLaunch) {
            prefs.edit().putBoolean("isFirstLaunch", false).apply()
        }

        // NEW: Log the state
        Log.d("MainActivity", "isFirstLaunch=$isFirstLaunch")

        // NEW: Also store in our Room database
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            val appState = AppState(id = 0, isFirstLaunch = isFirstLaunch)
            db.appStateDao().insertOrUpdateAppState(appState)
        }

        setContent {
            MyTravelBookTheme {
                val viewModelFactory = AppViewModelFactory(this)
                val tripViewModel: TripViewModel = viewModel(factory = viewModelFactory)
                val entryViewModel: EntryViewModel = viewModel(factory = viewModelFactory)

                // Pass along the isFirstLaunch to the NavHost
                MyTravelBookNavHost(
                    isFirstLaunch = isFirstLaunch,
                    tripViewModel = tripViewModel,
                    entryViewModel = entryViewModel
                )
            }
        }
    }
}
