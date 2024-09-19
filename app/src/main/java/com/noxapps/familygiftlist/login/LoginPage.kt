package com.noxapps.familygiftlist.login

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginPage(auth: FirebaseAuth, navHostController: NavHostController, viewModel: LoginViewModel = LoginViewModel()){
    Column(){
        Text("This is the login page")
    }
}