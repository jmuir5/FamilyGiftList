package com.noxapps.familygiftlist.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.values
import com.google.firebase.ktx.Firebase
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.FirebaseDBInteractor
import com.noxapps.familygiftlist.data.Gift
import com.noxapps.familygiftlist.data.GiftList
import com.noxapps.familygiftlist.data.GiftListGiftCrossReference
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
        updateLists(coroutineScope)
        updateGifts(coroutineScope)
        updateRelationships(coroutineScope)
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
    private fun updateLists(coroutineScope: CoroutineScope){
        if(user!=null) {
            FirebaseDBInteractor.getAllLists(user.userId){ _, lists->
                lists.forEach{
                    coroutineScope.launch {
                        db.giftListDao().upsert(it)
                    }
                }
            }
        }
    }
    private fun updateGifts(coroutineScope: CoroutineScope){
        if(user!=null) {
            FirebaseDBInteractor.getAllGifts(user.userId){ _, gifts->
                gifts.forEach{
                    coroutineScope.launch {
                        db.giftDao().upsert(it)
                    }
                }

            }
        }
    }
    private fun updateRelationships(coroutineScope: CoroutineScope){
        if(user!=null) {
            firebaseDB
                .child(user.userId)
                .child("Relationships")
                .get()
                .addOnCompleteListener() { snapshot ->
                    for (list in snapshot.result.children) {
                        val listId = list.key?.toInt()?:0
                        for(entry in list.children){
                            val result = entry.getValue(Boolean::class.java)
                            result?.let {
                                if (it) {
                                    val giftId = entry.key?.toInt()?:0
                                    coroutineScope.launch {
                                        db.referenceDao().upsert(GiftListGiftCrossReference(listId, giftId))
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

}