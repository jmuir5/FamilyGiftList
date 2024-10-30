package com.noxapps.familygiftlist.mygifts

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavHostController
import com.noxapps.familygiftlist.R
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.Gift
import com.noxapps.familygiftlist.data.GiftList
import com.noxapps.familygiftlist.data.GiftWithLists
import com.noxapps.familygiftlist.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun ColumnScope.CreateGiftDialogue(
    state: BottomDrawerState,
    scope: CoroutineScope,
    //headerSize: MutableState<IntSize>,
    drawerSize: Int,
    user: User,
    db: AppDatabase,
    navController: NavHostController,
    viewModel: MyGiftsViewModel
){
    var giftName by remember { mutableStateOf("") }
    var giftDesc by remember { mutableStateOf("") }
    var giftLink by remember { mutableStateOf("") }
    var giftPrice by remember { mutableIntStateOf(0) }

    val enabled = remember { mutableStateOf(true) }
    val textIconColors = MaterialTheme.colorScheme.onPrimaryContainer
    val textFieldColors = TextFieldDefaults.colors(
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
    )

    var emptyCheck by remember { mutableStateOf(false) }
    val giftNameError by remember {
        derivedStateOf { giftName.isEmpty() && emptyCheck }
    }
    var listOfLists by remember { mutableStateOf<List<GiftList>>(emptyList()) }
    val selectedLists = remember { mutableStateListOf<MutableState<Boolean>>() }
    val coroutineScope = rememberCoroutineScope()
    val screenHeight = LocalConfiguration.current.screenHeightDp

    LaunchedEffect(coroutineScope) {
        listOfLists = db.userDao()
            .getOneWithListsById(user.userId).lists

        (0..listOfLists.size).map { selectedLists.add(mutableStateOf(false)) }
    }
    LaunchedEffect(state.currentValue) {
        if(!state.isClosed)state.expand()
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .height(with(LocalDensity.current){drawerSize.toDp() })
    ) {
        Row(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(12.dp, 2.dp)
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.Companion
                    .padding(4.dp)
                    .align(Alignment.Companion.CenterVertically),
                text = "Create new gift",
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
                    colorFilter = ColorFilter.Companion.tint(MaterialTheme.colorScheme.onPrimary)
                )
            }
        }

        Column(
            modifier = Modifier.Companion
                .background(MaterialTheme.colorScheme.background)
                //.height((screenHeight - (headerSize.value.height - 8)).dp)//(screenHeight-((headerSize.value.height+drawerHeaderSize.height)/2)).dp
                .padding(4.dp)
                .weight(1f)


        ) {

            TextField(
                modifier = Modifier.Companion
                    .fillMaxWidth(),
                value = giftName,
                onValueChange = {
                    if (it.length <= 32) {
                        giftName = it
                    }
                },
                colors = textFieldColors,
                enabled = enabled.value,
                shape = RectangleShape,
                label = {
                    if (giftNameError)
                        Text("Name can not be empty")
                    else
                        Text("Gift Name")
                },
                singleLine = true,

                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.add_24px),
                        contentDescription = "List Name",
                        colorFilter = textIconColors.let { ColorFilter.Companion.tint(it) }
                    )
                }
            )
            TextField(
                modifier = Modifier.Companion
                    .fillMaxWidth(),
                value = giftDesc,
                onValueChange = {
                    giftDesc = it

                },
                colors = textFieldColors,
                enabled = enabled.value,
                shape = RectangleShape,
                label = { Text("Description") },
                singleLine = true,

                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.add_24px),
                        contentDescription = "Gift Description",
                        colorFilter = textIconColors.let { ColorFilter.Companion.tint(it) }
                    )
                }
            )
            TextField(
                modifier = Modifier.Companion
                    .fillMaxWidth(),
                value = giftLink,
                onValueChange = {
                    giftLink = it
                },
                colors = textFieldColors,
                enabled = enabled.value,
                shape = RectangleShape,
                label = { Text("Link") },
                singleLine = true,

                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.link_24px),
                        contentDescription = "Gift Link",
                        colorFilter = textIconColors.let { ColorFilter.Companion.tint(it) }
                    )
                }
            )
            TextField(
                modifier = Modifier.Companion
                    .fillMaxWidth(),
                value = "\$%.2f".format(giftPrice.toFloat().div(100)),
                onValueChange = {
                    val cleanedString = it.replace("$", "").replace(".", "")
                    if (cleanedString.isEmpty() || cleanedString.isDigitsOnly()) {
                        giftPrice = try {
                            cleanedString.toInt()
                        } catch (e: Exception) {
                            Int.MAX_VALUE

                        }
                    }
                },
                singleLine = true,
                colors = textFieldColors,
                enabled = enabled.value,
                shape = RectangleShape,
                label = { Text("Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Companion.NumberPassword),
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.attach_money_24px),
                        contentDescription = "Gift Price",
                        colorFilter = textIconColors.let { ColorFilter.Companion.tint(it) }
                    )
                }
            )
            Text("Add this gift to lists:")
            LazyColumn(
                modifier = Modifier.Companion
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                itemsIndexed(listOfLists) { index, gift ->
                    SelectableListEntry(
                        giftList = gift,
                        state = selectedLists[index],
                        navController = navController
                    )
                }
            }

        }
        Button(
            modifier = Modifier.Companion
                .fillMaxWidth(),
            shape = RectangleShape,
            onClick = {
                emptyCheck = true
                if (!giftNameError) {
                    val initialLists = mutableListOf<Int>()
                    (0..<selectedLists.size).map {
                        if (selectedLists[it].value) initialLists.add(
                            listOfLists[it].listId
                        )
                    }

                    viewModel.saveGift(
                        enabledState = enabled,
                        drawerState = state,
                        giftObject = Gift(
                            0,
                            user.userId,
                            giftName,
                            giftDesc,
                            giftLink.normaliseLink(),
                            giftPrice,
                            false,
                            LocalDate.now().toString(),
                            "",
                            false,
                            LocalDate.now().toString(),
                            "",
                            ""
                        ),
                        initialLists = initialLists,//inital gifts
                        navController = navController,
                        coroutineScope = coroutineScope
                    )
                }

            }
        ) {
            Text("Create Gift")
            Image(
                painter = painterResource(id = R.drawable.add_24px),
                contentDescription = "Gift Name",
                //colorFilter = textIconColors.let { ColorFilter.tint(it) }
            )
        }
    }
}


@Composable
fun ColumnScope.EditGiftDialogue(
    state: BottomDrawerState,
    reloader: MutableState<Boolean>,
    scope: CoroutineScope,
    drawerSize: Int,
    user: User,
    db: AppDatabase,
    //giftList
    initialGift: GiftWithLists,
    navController: NavHostController,
    viewModel: SingleGiftViewModel
){
    var giftName by remember { mutableStateOf(initialGift.gift.name) }
    var giftDesc by remember { mutableStateOf(initialGift.gift.description) }
    var giftLink by remember { mutableStateOf(initialGift.gift.link) }
    var giftPrice by remember { mutableIntStateOf(initialGift.gift.price) }

    val enabled = remember { mutableStateOf(true) }
    val textIconColors = MaterialTheme.colorScheme.onPrimaryContainer
    val textFieldColors = TextFieldDefaults.colors(
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
    )
    val priceFormattedString = "\$%.2f".format(giftPrice.toFloat().div(100))
    var priceTextFieldValueState = TextFieldValue(
        text = priceFormattedString,
        selection = TextRange(priceFormattedString.length)
    )



    var emptyCheck by remember { mutableStateOf(false) }
    val giftNameError by remember {
        derivedStateOf { giftName.isEmpty() && emptyCheck }
    }
    var listOfLists by remember { mutableStateOf<List<GiftList>>(emptyList()) }
    val selectedLists = remember { mutableStateListOf<MutableState<Boolean>>() }
    val coroutineScope = rememberCoroutineScope()
    val screenHeight = LocalConfiguration.current.screenHeightDp

    LaunchedEffect(coroutineScope) {
        listOfLists = db.userDao()
            .getOneWithListsById(user.userId).lists
        (0..listOfLists.size-1).map { selectedLists.add(mutableStateOf(initialGift.lists.contains(listOfLists[it]))) }
    }
    LaunchedEffect(state.currentValue) {
        if(!state.isClosed)state.expand()
    }



    Column(
        modifier = Modifier.Companion
            .background(MaterialTheme.colorScheme.background)
            .height(with(LocalDensity.current){drawerSize.toDp() })//(screenHeight - (headerSize.value.height - 8)).dp)//(screenHeight-((headerSize.value.height+drawerHeaderSize.height)/2)).dp
    ) {
        Row(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(12.dp, 2.dp)
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.Companion
                    .padding(4.dp)
                    .align(Alignment.Companion.CenterVertically),
                text = "Edit gift",
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
                    colorFilter = ColorFilter.Companion.tint(MaterialTheme.colorScheme.onPrimary)
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(4.dp)
                .weight(1f)
        ) {

            TextField(
                modifier = Modifier.Companion
                    .fillMaxWidth(),
                value = giftName,
                onValueChange = {
                    if (it.length <= 32) {
                        giftName = it
                    }
                },
                colors = textFieldColors,
                enabled = enabled.value,
                shape = RectangleShape,
                label = {
                    if (giftNameError)
                        Text("Name can not be empty")
                    else
                        Text("Gift Name")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),

                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.add_24px),
                        contentDescription = "List Name",
                        colorFilter = textIconColors.let { ColorFilter.Companion.tint(it) }
                    )
                }
            )
            TextField(
                modifier = Modifier.Companion
                    .fillMaxWidth(),
                value = giftDesc,
                onValueChange = {
                    giftDesc = it

                },
                colors = textFieldColors,
                enabled = enabled.value,
                shape = RectangleShape,
                label = { Text("Description") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),

                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.add_24px),
                        contentDescription = "Gift Description",
                        colorFilter = textIconColors.let { ColorFilter.Companion.tint(it) }
                    )
                }
            )
            TextField(
                modifier = Modifier.Companion
                    .fillMaxWidth(),
                value = giftLink,
                onValueChange = {
                    giftLink = it
                },
                colors = textFieldColors,
                enabled = enabled.value,
                shape = RectangleShape,
                label = { Text("Link") },
                singleLine = true,

                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.link_24px),
                        contentDescription = "Gift Link",
                        colorFilter = textIconColors.let { ColorFilter.Companion.tint(it) }
                    )
                }
            )
            TextField(
                modifier = Modifier.Companion
                    .fillMaxWidth(),
                value = priceTextFieldValueState,
                onValueChange = {
                    val cleanedString = it.text.replace("$", "").replace(".", "")
                    if (cleanedString.isEmpty() || cleanedString.isDigitsOnly()) {
                        giftPrice = try {
                            cleanedString.toInt()
                        } catch (e: Exception) {
                            Log.e("pricefield error", e.toString())
                            Int.MAX_VALUE
                        }
                        //priceTextFieldValueState= "\$%.2f".format(giftPrice.toFloat().div(100))
                    }
                },
                singleLine = true,
                colors = textFieldColors,
                enabled = enabled.value,
                shape = RectangleShape,
                label = { Text("Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Companion.NumberPassword),
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.attach_money_24px),
                        contentDescription = "Gift Price",
                        colorFilter = textIconColors.let { ColorFilter.Companion.tint(it) }
                    )
                }
            )
            Text("Add this gift to lists:")
            LazyColumn(
                modifier = Modifier.Companion
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                itemsIndexed(listOfLists) { index, gift ->
                    SelectableListEntry(
                        giftList = gift,
                        state = selectedLists[index],
                        navController = navController
                    )
                }
            }
        }
        Row(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
            ,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.error)
                    .clickable{
                        scope.launch {
                            state.close()
                        }
                    }
                    //.padding(10.dp)
            ){
                Text(
                    modifier = Modifier.Companion
                        .padding(4.dp)
                        .fillMaxWidth()
                        .align(Alignment.Companion.CenterVertically),
                    text = "Discard",
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
                        emptyCheck = true
                        if (!giftNameError) {
                            val oldRelationships = mutableListOf<Int>()
                            val initialLists = mutableListOf<Int>()
                            val removedRelationships = mutableListOf<Int>()
                            val newRelationships = mutableListOf<Int>()
                            (0..<selectedLists.size).map {
                                if (selectedLists[it].value) initialLists.add(
                                    listOfLists[it].listId
                                )
                            }
                            (0..<initialGift.lists.size).map {
                                oldRelationships.add(initialGift.lists[it].listId)
                                if (!initialLists.contains(initialGift.lists[it].listId))
                                    removedRelationships.add(initialGift.lists[it].listId)
                            }
                            (0..<initialLists.size).map {
                                if (!oldRelationships.contains(initialLists[it]))
                                    newRelationships.add(initialLists[it])
                            }



                            viewModel.saveGift(
                                enabledState = enabled,
                                drawerState = state,
                                reloader = reloader,
                                giftObject = Gift(
                                    initialGift.gift.giftId,
                                    user.userId,
                                    giftName,
                                    giftDesc,
                                    giftLink.normaliseLink(),
                                    giftPrice,
                                    initialGift.gift.reserved,
                                    initialGift.gift._reservedDate,
                                    initialGift.gift.reservedBy,
                                    initialGift.gift.purchased,
                                    initialGift.gift._purchasedDate,
                                    initialGift.gift.purchasedBy,
                                    initialGift.gift.purchaseProof
                                ),
                                newRelationships = newRelationships,//inital gifts
                                removedRelationships = removedRelationships,
                                navController = navController,
                                coroutineScope = coroutineScope
                            )
                        }
                    }
                    //.padding(10.dp)
            ){
                Text(
                    modifier = Modifier.Companion
                        .padding(4.dp)
                        .fillMaxWidth()
                        .align(Alignment.Companion.CenterVertically),
                    text = "Save",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}

fun String.normaliseLink():String{
    return if(this.startsWith("https://")||this.startsWith("Https://")){
        this
    }else if(this.startsWith("http://")){
        this.replace("http://", "https://")
    }else
        "https://$this"
}

