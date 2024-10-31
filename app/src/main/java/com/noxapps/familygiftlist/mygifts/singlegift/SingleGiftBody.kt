package com.noxapps.familygiftlist.mygifts.singlegift

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.BottomDrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.noxapps.familygiftlist.data.GiftWithLists
import com.noxapps.familygiftlist.metafetcher.MetaFetcher
import com.noxapps.familygiftlist.mygifts.UnselectableListEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SingleGiftBody(
    pageSize: MutableState<Int>,
    headerSize: MutableState<Int>,
    thisGift: GiftWithLists,
    metadata: MetaFetcher?,
    deleteDialogueState: MutableState<Boolean>,
    editDrawerState: BottomDrawerState,
    coroutineScope: CoroutineScope,
    navController: NavHostController,
    viewModel: SingleGiftViewModel
){
    Column(
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                pageSize.value = coordinates.size.height
            }
    ) {
        Row(
            modifier = Modifier.Companion
                .onGloballyPositioned { coordinates ->
                    headerSize.value = coordinates.size.height
                }
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(10.dp)
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.Companion
                    .padding(4.dp)
                    .align(Alignment.CenterVertically),
                text = thisGift.gift.name,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        if (metadata != null) {
            MetadataCard(metadata)

        } else {
            MetadataPlaceholderCard()
        }
        Column(
            modifier = Modifier
                .padding(4.dp)
                .weight(1f)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = thisGift.gift.description,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.End,
                text = "\$%.2f".format(thisGift.gift.price.toFloat().div(100)),
                style = MaterialTheme.typography.bodyLarge
            )

            Text("this gift is on the following lists:")
            LazyColumn(
                modifier = Modifier.Companion
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                itemsIndexed(thisGift.lists) { index, gift ->
                    UnselectableListEntry(
                        giftList = gift,
                        navController = navController
                    )
                }
            }

        }
        Row(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.error)
                    .clickable {
                        deleteDialogueState.value = true
                    }
                    .padding(10.dp)
            ) {
                Text(
                    modifier = Modifier.Companion
                        .padding(4.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically),
                    text = "Delete",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onError,
                    textAlign = TextAlign.Center

                )
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        coroutineScope.launch {
                            editDrawerState.open()
                        }
                    }
                    .padding(10.dp)
            ) {
                Text(
                    modifier = Modifier.Companion
                        .padding(4.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically),
                    text = "Edit",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }

    }
    if(deleteDialogueState.value){
        DeleteDialogue(deleteDialogueState) {
            viewModel.deleteGift(
                giftObject = thisGift,
                navController = navController,
                coroutineScope = coroutineScope
            )
        }
    }
}