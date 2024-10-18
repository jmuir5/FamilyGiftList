package com.noxapps.familygiftlist.mygifts

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
import com.noxapps.familygiftlist.data.Gift
import com.noxapps.familygiftlist.data.GiftList
import com.noxapps.familygiftlist.data.GiftListGiftCrossReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MyGiftsViewModel(val db:AppDatabase, val auth: FirebaseAuth): ViewModel() {
    private val firebaseDB = Firebase.database.reference

    fun saveGift(
        enabledState: MutableState<Boolean>,
        drawerState: BottomDrawerState,
        giftObject: Gift,
        initialLists:List<Int>,
        navController:NavHostController,
        coroutineScope: CoroutineScope
    ){
        enabledState.value = false
        try {
            //local
            coroutineScope.launch {
                val newId = db.giftDao().insert(giftObject)
                Log.d("MyList", "newID = $newId")
                Log.d("MyList", "newID.toInt() = ${newId.toInt()}")


                initialLists.forEach { listId ->
                    db.referenceDao().insert(
                        GiftListGiftCrossReference(
                            listId = listId,
                            giftId = newId.toInt()
                        )
                    )
                }
                //to server
                val newGift = Gift(giftObject, newId.toInt())
                firebaseDB
                    .child("${auth.currentUser?.uid}")
                    .child("Gifts")
                    .child("${newGift.giftId}")
                    .setValue(newGift)
                initialLists.forEach { listId ->
                    firebaseDB
                        .child("${auth.currentUser?.uid}")
                        .child("Relationships")
                        .child("$listId")
                        .child("${newGift.giftId}")
                        .setValue(true)
                }
                enabledState.value = true
                coroutineScope.launch {
                    drawerState.close()
                }
                MainScope().launch {
                    navController.navigate("${Paths.SingleGift.Path}/${newId}")
                }
            }

        }
        catch(e:Exception){
            Log.d("MyList", "function failed")
            Log.e("CreateList error", e.toString())
        }

    }



}