package com.noxapps.familygiftlist.login

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.familygiftlist.Paths
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class LoginViewModel(
    val auth: FirebaseAuth,
    val db:AppDatabase,
    val navController: NavController
):ViewModel() {
    fun login(
        email:String,
        password:String,
        enableState: MutableState<Boolean>,
        context: Context
    ){
        enableState.value = false
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Login", "signInWithEmail:success")
                    val user = auth.currentUser
                    navController.navigate(Paths.Home.Path){
                        popUpTo(Paths.Home.Path) {
                            inclusive=true
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Login", "signInWithEmail:failure", task.exception)

                    Toast.makeText(
                        context,
                        "Authentication failed: ${task.exception}",
                        Toast.LENGTH_SHORT,
                    ).show()
                    enableState.value = true
                    //updateUI(null)
                }
            }
    }

    fun register(
        firstName:String,
        lastName:String,
        email:String,
        birthday: LocalDate,
        password:String,
        enableState: MutableState<Boolean>,
        context: Context,
        coroutineScope: CoroutineScope
    ){
        enableState.value = false
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("register", "createUserWithEmail:success")
                    val user = auth.currentUser
                    coroutineScope.launch{
                        user?.let{db.userDao().insertOne(User(it.uid, email, firstName, lastName, birthday))}
                    }
                    navController.navigate(Paths.Home.Path) {
                        popUpTo(Paths.Home.Path) {
                            inclusive = true
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("register", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        context,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    enableState.value = true
                }
            }

    }
}