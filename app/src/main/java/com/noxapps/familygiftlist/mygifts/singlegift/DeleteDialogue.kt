package com.noxapps.familygiftlist.mygifts.singlegift

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun DeleteDialogue(dialogueState: MutableState<Boolean>, onConfirm: ()-> Unit){
    BackHandler {
        dialogueState.value = !dialogueState.value
    }
    Dialog(
        onDismissRequest = { dialogueState.value = !dialogueState.value },
    ) {
        //Box(
        //  modifier = Modifier

        //){
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(MaterialTheme.colorScheme.surface)
                .border(Dp.Hairline, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.primary)
                    //.padding(10.dp)
                ) {
                    Text(
                        modifier = Modifier.Companion
                            .padding(8.dp)
                            .fillMaxWidth()
                            .align(Alignment.CenterVertically),
                        text = "Confirmation",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        //textAlign = TextAlign.Center
                    )
                }
            }
            Text(
                modifier = Modifier
                    .padding(8.dp),
                text = "Are you sure you want to delete this gift? It will be removed from all lists including ones shared in groups." +
                        "\n\nThis can not be undone.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
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
                            onConfirm()
                        }
                    //.padding(10.dp)
                ) {
                    Text(
                        modifier = Modifier.Companion
                            .padding(4.dp)
                            .fillMaxWidth()
                            .align(Alignment.CenterVertically),
                        text = "Delete Gift",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        // }
    }
}