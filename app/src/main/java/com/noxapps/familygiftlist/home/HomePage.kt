package com.noxapps.familygiftlist.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.GiftListGiftCrossReference
import com.noxapps.familygiftlist.navigation.Paths
import com.noxapps.familygiftlist.data.User
import com.noxapps.familygiftlist.data.sampleData
import com.noxapps.familygiftlist.navigation.loginCheck
import com.noxapps.familygiftlist.ui.theme.FamilyGiftListTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomePage(
    auth: FirebaseAuth,
    currentUser: User?,
    navController: NavHostController,
    db:AppDatabase,
    viewModel: HomeViewModel = HomeViewModel()
){
    loginCheck(navController, auth)
    Greeting(currentUser, db,  navController)
}


@Composable
fun Greeting(
    currentUser: User?,
    db:AppDatabase,
    navController: NavHostController
) {
    val coroutineScope = rememberCoroutineScope()
    /*val fakeGifts = (1..5).map{
            Gift(
            0,
            0,
            "Example Gift",
            "This is an example of a gift",
            "www.google.com",
            5000,
            false,
            LocalDate.of(2024, 9, 10),
            "James Muir",
            false,
            LocalDate.of(2024, 9, 10),
            "James Muir",
            ""
        )
    }
    val previewGift = Gift(
        0,
        0,
        "Mirrored Mushroom Sculpture",
        "Disco fever forever with a magic mushroom-shaped Sculpture in shiny silver",
        "https://gigiandtom.com.au/products/mirrored-mushroom-sculpture-silver",
        2459,
        false,
        LocalDate.of(2024, 9, 10),
        "James Muir",
        true,
        LocalDate.of(2024, 9, 10),
        "James Muir",
        ""
    )

    val listOGifts = fakeGifts+previewGift+fakeGifts+fakeGifts+fakeGifts

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    LazyColumn(state = listState) {
        items(listOGifts.size){
            ReceivedGiftCard(
                gift = listOGifts[it],
                index = it,
                lazyListState = listState,
                coroutineScope = coroutineScope
            )
        }
    }*/
    //ReceivedGiftCard(previewGift)

    Column (modifier = Modifier
        .fillMaxWidth()
    ){
        Text(
            text = "Home Page",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "logged in as: ${currentUser?.firstName} ${currentUser?.lastName}",
            style = MaterialTheme.typography.headlineSmall
        )
        Button(
            onClick = {
                Firebase.auth.signOut()
                loginCheck(auth = Firebase.auth, navController = navController)
            }
        ) {
            Text("sign out")
        }
        Button(
            onClick = {
                navController.navigate(Paths.MyLists.Path)
            }
        ) {
            Text("myList")
        }
        Button(
            onClick = {
                navController.navigate(Paths.MyGifts.Path)
            }
        ) {
            Text("myGifts")
        }
        Button(
            onClick = {
                    //db.clearAllTables()
            }
        ) {
            Text("clean database")
        }
        var relationships by remember {mutableStateOf<List<GiftListGiftCrossReference>>(emptyList())}
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(coroutineScope) {
            relationships = db.referenceDao().getAll()
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
        ) {
            item(){
                Text("Relationships (${relationships.size}) :")
            }
            itemsIndexed(relationships){ index, relationship ->
                Text("relationship #$index, listId = ${relationship.listId}, GiftId = ${relationship.giftId}")
            }
        }

        Text(
            text = "Display Large",
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = "Display Medium",
            style = MaterialTheme.typography.displayMedium
        )
        Text(
            text = "Display Small",
            style = MaterialTheme.typography.displaySmall
        )
        Text(
            text = "Headline Large",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Headline Medium",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Headline Small",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Title Large",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Title Medium",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Title Small",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = "Body Large",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Body Medium",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Body Small",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Label Large",
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = "Label Medium",
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = "Label Small",
            style = MaterialTheme.typography.labelSmall
        )




    }
    /*
    Text(
            text = "Welcome to the WFRP companion app",
        )
    Text(
        text = """
            todo:

            local database
            accounts
            remote database

            define schema for gift list entry:
                - name
                - description
                - link
                - image?
                - price
                - reserved (someone has intent to buy)
                - purchased (with prof of purchase?)

            list class:
                - add item
                - remove item

            pages:
                -my list
                - shared family lists
                - home page with links to my lists, joind family lists, settings?

            join family:

            accounts:

            """.trimIndent()

    )*/


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FamilyGiftListTheme{

        /*Greeting(
            currentUser = sampleData.sampleUser,
            navController = rememberNavController()
        )*/
    }
}