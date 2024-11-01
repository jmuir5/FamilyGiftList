package com.noxapps.familygiftlist.mylists.singlelist

import android.util.Log
import androidx.compose.material.BottomDrawerState
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.GiftList
import com.noxapps.familygiftlist.data.GiftListGiftCrossReference
import com.noxapps.familygiftlist.data.GiftWithLists
import com.noxapps.familygiftlist.data.ListWithGifts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SingleListViewModel(
    val id:Int,
    val db: AppDatabase,
    val auth: FirebaseAuth,
    val navController: NavHostController
): ViewModel() {

    private val firebaseDB = Firebase.database.reference

    fun saveList(
        enabledState: MutableState<Boolean>,
        reloader: MutableState<Boolean>,
        drawerState: BottomDrawerState,
        listObject: GiftList,
        newRelationships:List<Int>,
        removedRelationships:List<Int>,
        coroutineScope: CoroutineScope
    ){
        enabledState.value = false
        try {
            Log.d("update list", listObject.toString())
            //local
            coroutineScope.launch {
                db.giftListDao().update(listObject)
                newRelationships.forEach { giftId ->
                    db.referenceDao().insert(
                        GiftListGiftCrossReference(
                            listId = listObject.listId,
                            giftId = giftId
                        )
                    )

                }
                removedRelationships.forEach { giftId ->
                    db.referenceDao().delete(
                        GiftListGiftCrossReference(
                            listId = listObject.listId,
                            giftId = giftId
                        )
                    )

                }
                //to server
                firebaseDB
                    .child("${auth.currentUser?.uid}")
                    .child("Lists")
                    .child("${listObject.listId}")
                    .setValue(listObject)
                removedRelationships.forEach{ giftId->
                    firebaseDB
                        .child("${auth.currentUser?.uid}")
                        .child("Relationships")
                        .child("${listObject.listId}")
                        .child("$giftId")
                        .setValue(false)
                }
                newRelationships.forEach{ giftId->
                    firebaseDB
                        .child("${auth.currentUser?.uid}")
                        .child("Relationships")
                        .child("${listObject.listId}")
                        .child("$giftId")
                        .setValue(true)
                }
                enabledState.value = true
                drawerState.close()
                MainScope().launch {
                    reloader.value = !reloader.value
                    enabledState.value=true
                }
            }

        }
        catch(e:Exception){
            Log.d("MyList", "function failed")
            Log.e("CreateList error", e.toString())
        }

    }

    fun deleteList(
        listObject: ListWithGifts,
        navController:NavHostController,
        coroutineScope: CoroutineScope
    ) {
        auth.uid?.let {
            firebaseDB.child(it)
                .child("Lists")
                .child(listObject.giftList.listId.toString())
                .removeValue()
                .addOnSuccessListener {
                    Log.d("gift Deletion", "gift deleted")
                }
                .addOnFailureListener {
                    Log.d("gift Deletion", "gift deleted")
                }
            listObject.gifts.forEach { gift ->
                firebaseDB.child(it).child("Relationships")
                    .child("${listObject.giftList.listId}")
                    .child(gift.giftId.toString())
                    .setValue(false)
                    .addOnSuccessListener {
                        Log.d(
                            "gift Deletion",
                            "relationship ${gift.giftId} - ${listObject.giftList.listId} deleted"
                        )
                    }
                    .addOnFailureListener {
                        Log.d("gift Deletion", "gift deleted")
                    }
            }
            coroutineScope.launch {
                db.giftListDao().delete(listObject.giftList)
                val references = db.referenceDao().getByListId(listObject.giftList.listId)
                references.forEach { it ->
                    db.referenceDao().delete(it)
                }
                MainScope().launch {
                    navController.popBackStack()
                }
            }

        }
    }

}