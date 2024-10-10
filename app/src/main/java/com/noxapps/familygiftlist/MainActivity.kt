package com.noxapps.familygiftlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.noxapps.familygiftlist.ui.theme.FamilyGiftListTheme
import com.noxapps.familygiftlist.navigation.NavMain

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FamilyGiftListTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                ) { innerPadding ->
                    val navController = rememberNavController()

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        NavMain(navController = navController, auth = auth)
                    }
                }
            }
        }
    }
}


/* todo checklist
 - implement gemini nano to analyse web pages

 */