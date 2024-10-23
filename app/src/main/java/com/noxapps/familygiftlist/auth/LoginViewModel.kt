package com.noxapps.familygiftlist.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.noxapps.familygiftlist.navigation.Paths
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.GiftList
import com.noxapps.familygiftlist.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class LoginViewModel(
    val auth: FirebaseAuth,
    val db:AppDatabase,
    val navController: NavController
):ViewModel() {
    private val firebaseDB = Firebase.database.reference


    fun login(
        email:String,
        password:String,
        enableState: MutableState<Boolean>,
        context: Context,
        coroutineScope: CoroutineScope
    ){
        enableState.value = false
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Login", "signInWithEmail:success")
                    val user = auth.currentUser

                    Log.d("login debug", "loading data")

                    coroutineScope.launch {
                        try {
                            user?.let {
                                val loadedUser = db.userDao().getOneById(user.uid)
                                Log.d("login debug", "loaded user $loadedUser")
                                Log.d("login debug", "loaded user ${loadedUser.userId}")



                                if ( loadedUser.userId != "") {
                                    MainScope().launch {
                                        Log.d("login debug", "load successfull, going to home")

                                        navController.navigate(Paths.Home.Path) {
                                            popUpTo(Paths.Home.Path) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                } else {
                                    throw Exception("load failed")
                                }
                                /*Log.d("login debug", "loaded user = ${db.userDao().getOneById(user.uid)}")
                            MainScope().launch {
                                Log.d("login debug", "load successfull, going to home")

                                navController.navigate(Paths.Home.Path){
                                    popUpTo(Paths.Home.Path) {
                                        inclusive=true
                                    }
                                }
                            }*/
                            } ?: throw Exception("load failed")
                        } catch (e:Exception){
                            Log.d("login debug", "load failed, pulling data")

                            firebaseDB
                                .child(user!!.uid)
                                .child("User")
                                .get()
                                .addOnCompleteListener(){pu ->
                                    Log.d("pulled data test", pu.result.toString())
                                    val pulledUser = pu.result.getValue(User::class.java)
                                    Log.d("pulled data test", pulledUser.toString())
                                    coroutineScope.launch {
                                        pulledUser?.let {db.userDao().insertOne(it) }
                                        MainScope().launch {
                                            navController.navigate(Paths.Home.Path){
                                                popUpTo(Paths.Home.Path) {
                                                    inclusive=true
                                                }
                                            }
                                        }
                                    }

                                }

                                .addOnFailureListener(){

                                }
                        }
                    }

                    /*firebaseDB
                        .child(user!!.uid)
                        .child("User")
                        .get()
                        .addOnCompleteListener(){
                            val lists = it.result.getValue(User::class.java)
                            Log.d("pulled data test", lists.toString())
                        }
                        .addOnFailureListener(){

                        }*/

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
                    user?.let {
                        val newUser =  User(it.uid, email, firstName, lastName, birthday.toString())
                        coroutineScope.launch{
                            db.userDao().insertOne(newUser)
                            firebaseDB
                                .child(user.uid)
                                .child("User")
                                .setValue(newUser)
                            MainScope().launch {
                                navController.navigate(Paths.Home.Path) {
                                    popUpTo(Paths.Home.Path) {
                                        inclusive = true
                                    }
                                }
                            }
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