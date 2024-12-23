package com.samzuhalsetiawan.vortexmusicplayer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.samzuhalsetiawan.vortexmusicplayer.presentation.Screen
import com.samzuhalsetiawan.vortexmusicplayer.presentation.mainscreen.MainScreen
import com.samzuhalsetiawan.vortexmusicplayer.presentation.mainscreen.MainScreenViewModel
import com.samzuhalsetiawan.vortexmusicplayer.presentation.ui.theme.VortexMusicPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var isServiceStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VortexMusicPlayerTheme {

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Main
                ) {
                    composable<Screen.Main> {
                        val viewModel = hiltViewModel<MainScreenViewModel>()

                        MainScreen(
                            viewModel = viewModel,
                        )
                    }
                }

            }
        }
    }

}
