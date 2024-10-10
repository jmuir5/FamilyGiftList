package com.noxapps.familygiftlist.mylists

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.noxapps.familygiftlist.R
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.Gift
import com.noxapps.familygiftlist.data.GiftList
import com.noxapps.familygiftlist.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ColumnScope.CreateListDialogue(
    state: BottomDrawerState,
    scope: CoroutineScope,
    headerSize:MutableState<IntSize>,
    user: User,
    db: AppDatabase,
    navController: NavHostController,
    viewModel: MyListsViewModel
){
    var listName by remember { mutableStateOf("") }
    var listDesc by remember { mutableStateOf("") }

    val enabled = remember { mutableStateOf(true) }
    val textIconColors = MaterialTheme.colorScheme.onPrimaryContainer
    val textFieldColors = TextFieldDefaults.colors(
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
    )

    var emptyCheck by remember { mutableStateOf(false) }
    val listNameError by remember {
        derivedStateOf { listName.isEmpty() && emptyCheck }
    }

    var listOfGifts by remember { mutableStateOf<List<Gift>>(emptyList()) }
    val selectedGifts = remember { mutableStateListOf<MutableState<Boolean>>() }
    val coroutineScope = rememberCoroutineScope()
    val screenHeight = LocalConfiguration.current.screenHeightDp


    LaunchedEffect(coroutineScope) {
        listOfGifts = db.giftDao().getAll()
        (0..listOfGifts.size).map {selectedGifts.add(mutableStateOf(false)) }

    }
    LaunchedEffect(state.isOpen) {
        state.expand()
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(12.dp, 2.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.CenterVertically),
            text = "Create new list",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
        IconButton(
            onClick = {
                scope.launch {
                    state.close()
                }
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.close_24px),
                contentDescription = "close",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
            )
        }
    }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .height((screenHeight - (headerSize.value.height -8)).dp)
            .padding(4.dp)

    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = listName,
            onValueChange = {
                if (it.length <= 32) {
                    listName = it
                }
            },
            colors = textFieldColors,
            enabled = enabled.value,
            shape = RectangleShape,
            isError = listNameError,
            label = {
                if (listNameError)
                    Text("Name can not be empty")
                else
                    Text("List Name")
            },

            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.add_24px),
                    contentDescription = "List Name",
                    colorFilter = textIconColors.let { ColorFilter.tint(it) }
                )
            }
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = listDesc,
            onValueChange = {
                listDesc = it

            },
            colors = textFieldColors,
            enabled = enabled.value,
            shape = RectangleShape,
            label = { Text("Description") },

            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.add_24px),
                    contentDescription = "List Name",
                    colorFilter = textIconColors.let { ColorFilter.tint(it) }
                )
            }
        )
        Text("Add gifts to this list:")
        LazyColumn(
            modifier = Modifier
                .border(2.dp, MaterialTheme.colorScheme.primary)
                .weight(1f)
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            itemsIndexed(listOfGifts) { index, gift ->
                SelectableGiftEntry(gift = gift, state = selectedGifts[index])
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RectangleShape,
            onClick = {
                Log.d("MyList", "Button Clicked")
                emptyCheck = true
                if (!listNameError) {
                    Log.d("MyList", "error check passed")
                    viewModel.saveList(
                        enabledState = enabled,
                        drawerState = state,
                        listObject = GiftList(
                            0,
                            user.id,
                            "${user.firstName} ${user.lastName}",
                            listName,
                            listDesc
                        ),
                        initialGifts = listOf<Int>(),//inital gifts
                        navController = navController,
                        coroutineScope = coroutineScope
                    )
                }

            }
        ) {
            Text("Create List")
            Image(
                painter = painterResource(id = R.drawable.add_24px),
                contentDescription = "List Name",
                //colorFilter = textIconColors.let { ColorFilter.tint(it) }
            )
        }
    }
}