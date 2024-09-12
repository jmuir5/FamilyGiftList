package com.noxapps.wfrpcompanion.Home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.noxapps.wfrpcompanion.ui.theme.WFRPCompanionTheme

@Composable
fun HomePage(
    viewModel: HomeViewModel = HomeViewModel(),
    navController: NavController
){
    Greeting()
}
@Composable
fun Greeting() {
    Column (modifier = Modifier
        .padding(Dp(4f))
    ){
        Text(
            text = "Welcome to the WFRP companion app",
        )
        Text(
            text = """
                todo:
                character sheets
                character creator?
                character sheet exporter?
                
                dice rollers
                """.trimIndent()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WFRPCompanionTheme {
        Greeting()
    }
}