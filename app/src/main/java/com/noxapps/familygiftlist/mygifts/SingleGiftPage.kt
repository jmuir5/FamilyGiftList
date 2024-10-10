package com.noxapps.familygiftlist.mygifts

import androidx.compose.foundation.layout.Column
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.sampleData
import com.noxapps.familygiftlist.mylists.SingleListViewModel

@Composable
fun SingleGiftPage(
    id: Int,
    db: AppDatabase,
    navController: NavHostController,
    singleGiftViewModel: SingleGiftViewModel = SingleGiftViewModel(
        id = id,
        db = db,
        navController = navController
    )
) {
    val coroutineScope = rememberCoroutineScope()

    var thisGift by remember{ mutableStateOf(sampleData.nullGiftWithLists) }
    LaunchedEffect(coroutineScope) {
        thisGift = db.giftDao().getOneWithListsById(id)
    }


    TopAppBar(title = { thisGift.gift.name })

    Column {
        Text("This is a Gift page")
        Text("Gift id = ${thisGift.gift.giftId}")
        Text("Gift Name = ${thisGift.gift.name}")
        Text("description = ${thisGift.gift.description}")
        Text("Link = ${thisGift.gift.link}")
        Text("reserved = ${thisGift.gift.reserved}")
        Text("reserved Date = ${thisGift.gift.reservedDate}")
        Text("reserved by = ${thisGift.gift.reservedBy}")
        Text("purchased = ${thisGift.gift.purchased}")
        Text("purchased Date = ${thisGift.gift.purchaseDate}")
        Text("purchased by = ${thisGift.gift.purchasedBy}")
        Text("purchased proof = ${thisGift.gift.purchaseProof}")


    }
}