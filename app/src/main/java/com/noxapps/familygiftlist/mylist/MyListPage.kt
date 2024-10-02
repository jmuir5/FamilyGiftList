package com.noxapps.familygiftlist.mylist

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerState
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.DrawerState
import androidx.compose.material.IconButton
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.familygiftlist.R
import com.noxapps.familygiftlist.auth.autofill
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.loginCheck
import com.noxapps.familygiftlist.ui.theme.FamilyGiftListTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun MyListPage(
    context: Context,
    db:AppDatabase,
    auth: FirebaseAuth,
    navController: NavHostController,
    viewModel: MyListViewModel = MyListViewModel(context,db)
) {
    loginCheck(navController, auth)
    val createDrawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)
    val showCreateDrawer by remember{ derivedStateOf { createDrawerState.isOpen }}
    val coroutineScope = rememberCoroutineScope()

    BottomDrawer(
        drawerState = createDrawerState,
        //drawerShape = RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp),
        drawerBackgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        gesturesEnabled = false,
        drawerContent = {
            if(showCreateDrawer)CreateListDialogue(state = createDrawerState, scope = coroutineScope)
        }
    ){
        LazyColumn(){
            items(0){
                //list headers
            }
            item(){
                Button(
                    onClick = {
                        coroutineScope.launch {
                            createDrawerState.open()
                        }

                    }
                ) {
                    Text(text = "Create New List +")
                }
            }
        }

    }

    //AnimatedVisibility(visible = createDrawerState.value) { //create list dialogue drawer

    //}
    //CreateListDialogue(state = createDrawerState, scope = coroutineScope)
    /*
    My lists:
    create new list
    list of lists/ list index
    list page:
        gifts/ gift cards
        add gift



     */

}

@Composable
fun ColumnScope.CreateListDialogue(state: BottomDrawerState, scope:CoroutineScope){
    var listName by remember{ mutableStateOf("")}
    var enabled by remember{mutableStateOf(true)}
    val textIconColors = MaterialTheme.colorScheme.onPrimaryContainer
    val textFieldColors = TextFieldDefaults.colors(
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
    )

    SideEffect {
        if(state.isOpen){
            scope.launch {state.expand()}
        }
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
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
        ){
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
            .fillMaxHeight(0.7f)
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
            enabled = enabled,
            shape = RectangleShape,
            label = {Text("List Name")},

            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.add_24px),
                    contentDescription = "List Name",
                    colorFilter = textIconColors.let { ColorFilter.tint(it) }
                )
            },

            )
        Text("Add gifts to this list:")
        LazyColumn(
            modifier = Modifier
                .border(2.dp, MaterialTheme.colorScheme.primary)
                .weight(1f)
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            item {
                Text(text = "gifts go here") //todo
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RectangleShape,
            onClick = { /*TODO*/ }) {
            Text("Create List")
            Image(
                painter = painterResource(id = R.drawable.add_24px),
                contentDescription = "List Name",
                //colorFilter = textIconColors.let { ColorFilter.tint(it) }
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun createListPreview() {
    FamilyGiftListTheme {
        Box(
            modifier = Modifier
                .background(Color.Blue)
                .fillMaxSize()
        ){
            var state = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)
            //CreateListDialogue(state = state)
        }

    }
}
