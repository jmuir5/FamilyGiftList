package com.noxapps.familygiftlist.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.database.values
import com.google.firebase.ktx.Firebase
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.GiftList
import com.noxapps.familygiftlist.data.User
import com.noxapps.familygiftlist.navigation.Paths
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class HomeViewModel(
    val auth: FirebaseAuth,
    val db: AppDatabase,
    val user: User?,
    val navController: NavController
):ViewModel(){
    private val firebaseDB = Firebase.database.reference

    fun pullData(coroutineScope: CoroutineScope){
        if(user!=null) {
            coroutineScope.launch {
                val lists = firebaseDB
                    .child(user.userId)
                    .child("Lists")
                    .values<GiftList>()
                    .toList()
                MainScope().launch {
                    lists.forEach {
                        Log.d("pulled lists", it.toString())
                    }
                }
            }



        }
        //user?.userId?.let {

                //.get()
                //.addOnCompleteListener() { pu ->
                    //Log.d("pulled data test", pu.result.toString())
                    //val pulledUser = pu.result.getValue(User::class.java)
                    /*Log.d("pulled data test", pulledUser.toString())
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

                     */



    }

}