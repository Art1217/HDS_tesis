package com.example.hds_tesisapp

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.hds_tesisapp.Nav.AppNavigation
import com.example.hds_tesisapp.ui.theme.HDS_tesisAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Forzamos landscape solo cuando entras al juego (opcional)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

        setContent {
            AppNavigation()
        }
    }
}

