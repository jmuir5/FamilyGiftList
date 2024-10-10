package com.noxapps.familygiftlist.mylists

import android.util.Log
import androidx.compose.material.BottomDrawerState
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.noxapps.familygiftlist.navigation.Paths
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.GiftList
import com.noxapps.familygiftlist.data.GiftListGiftCrossReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MyListsViewModel(val db:AppDatabase, val auth: FirebaseAuth): ViewModel() {
    private val firebaseDB = Firebase.database.reference


    fun saveList(
        enabledState: MutableState<Boolean>,
        drawerState: BottomDrawerState,
        listObject: GiftList,
        initialGifts:List<Int>,
        navController:NavHostController,
        coroutineScope: CoroutineScope
    ){
        Log.d("MyList", "function Started")
        enabledState.value = false
        try {
            //local
            Log.d("MyList", "local started")
            coroutineScope.launch {
                val newId = db.giftListDao().insert(listObject)
                Log.d("MyList", "newID = $newId")
                Log.d("MyList", "newID.toInt() = ${newId.toInt()}")


                initialGifts.forEach { giftId ->
                    db.referenceDao().insertAll(
                        GiftListGiftCrossReference(
                            listId = newId.toInt(),
                            giftId = giftId
                        )
                    )
                }
                Log.d("MyList", "local finished, server started")
                //to server
                val newList = GiftList(listObject, newId.toInt())
                firebaseDB
                    .child("${auth.currentUser?.uid}")
                    .child("Lists")
                    .child("${newList.listId}")
                    .setValue(newList)
                initialGifts.forEach { giftId ->
                    firebaseDB
                        .child("${auth.currentUser?.uid}")
                        .child("Relationships")
                        .child("${newList.listId}")
                        .child("$giftId")
                        .setValue(true)
                }
                enabledState.value = true
                coroutineScope.launch {
                    drawerState.close()
                }
                Log.d("MyList", "function finished, new id = $newId")
                MainScope().launch {
                    navController.navigate("${Paths.SingleList.Path}/${newId}")
                }
            }

        }
        catch(e:Exception){
            Log.d("MyList", "function failed")
            Log.e("CreateList error", e.toString())
        }


    }



}