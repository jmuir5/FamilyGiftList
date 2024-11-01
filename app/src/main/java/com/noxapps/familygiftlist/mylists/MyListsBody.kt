package com.noxapps.familygiftlist.mylists

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.BottomDrawerState
import androidx.compose.material.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.noxapps.familygiftlist.R
import com.noxapps.familygiftlist.data.ListWithGifts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MyListsBody(
    coroutineScope: CoroutineScope,
    drawerState: BottomDrawerState,
    headerSize: MutableState<Int>,
    pageSize:MutableState<Int>,
    listOfLists: List<ListWithGifts>,
    expandedListCard: MutableState<Int>,
    navController: NavHostController
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                pageSize.value = coordinates.size.height
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    headerSize.value = coordinates.size.height
                }
                .background(MaterialTheme.colorScheme.primary)
                .padding(10.dp)
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.CenterVertically),
                text = "My Lists",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.add_24px),
                    contentDescription = "create new list",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                )
            }
        }
        LazyColumn() {
            if (listOfLists.isEmpty()) {
                item { //todo no lists dialogue
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }
                    ) {
                        Text(text = "Create New List +")
                    }
                }
            } else {
                itemsIndexed(listOfLists) { index, list ->
                    ListCard(
                        list,
                        index,
                        expandedListCard,
                        navController
                    )
                }
            }


        }
    }
}