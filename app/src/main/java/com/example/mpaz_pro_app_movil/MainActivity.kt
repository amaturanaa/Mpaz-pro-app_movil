package com.example.mpaz_pro_app_movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mpaz_pro_app_movil.ui.theme.Mpazproapp_movilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mpazproapp_movilTheme {
                EstudianteScreen(onCerrarSesion = { })
            }
        }
    }
}