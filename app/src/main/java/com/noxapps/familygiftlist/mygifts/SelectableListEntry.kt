package com.noxapps.familygiftlist.mygifts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.noxapps.familygiftlist.R
import com.noxapps.familygiftlist.data.GiftList
import com.noxapps.familygiftlist.navigation.Paths

@Composable
fun SelectableListEntry(
    giftList: GiftList,
    state: MutableState<Boolean>,
    navController: NavHostController
){
    val toggleBackgroundColor =
        if(state.value){
            MaterialTheme.colorScheme.primary
        }else{
            MaterialTheme.colorScheme.secondaryContainer
        }

    val toggleTextColor =
        if(state.value){
            MaterialTheme.colorScheme.onPrimary
        }else{
            MaterialTheme.colorScheme.onPrimaryContainer
        }

    Row(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(4.dp, 2.dp)
            .background(toggleBackgroundColor)
            .height(IntrinsicSize.Min)
            .clickable { state.value = !state.value }
            .padding(4.dp),
    ) {
        Checkbox(
            modifier = Modifier.Companion
                .align(Alignment.Companion.CenterVertically),
            checked = state.value,
            onCheckedChange = {
                state.value = it
            }
        )
        Text(
            modifier = Modifier.Companion
                .align(Alignment.Companion.CenterVertically),
            text = giftList.listName,
            color = toggleTextColor,
            maxLines = 1,
            overflow = TextOverflow.Companion.Ellipsis
        )
        Spacer(
            modifier = Modifier.Companion
                .weight(1f)
        )
        IconButton(
            modifier = Modifier.Companion
                .align(Alignment.Companion.CenterVertically),
            onClick = {
                navController.navigate("${Paths.SingleList.Path}/${giftList.listId}")
            }) {
            Image(
                painter = painterResource(id = R.drawable.open_in_new_24px),
                contentDescription = "Open Gift",
                colorFilter = ColorFilter.Companion.tint(toggleTextColor)
            )
        }

    }
}

@Composable
fun UnselectableListEntry(
    giftList: GiftList,
    navController: NavHostController){
    Row(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(4.dp, 2.dp)
            .background(MaterialTheme.colorScheme.primary)
            .height(IntrinsicSize.Min)
            .padding(4.dp),
    ) {
        Text(
            modifier = Modifier.Companion
                .align(Alignment.Companion.CenterVertically),
            text = giftList.listName,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Companion.Ellipsis
        )
        Spacer(
            modifier = Modifier.Companion
                .weight(1f)
        )
        IconButton(
            modifier = Modifier.Companion
                .align(Alignment.Companion.CenterVertically),
            onClick = {
                navController.navigate("${Paths.SingleList.Path}/${giftList.listId}")
            }) {
            Image(
                painter = painterResource(id = R.drawable.open_in_new_24px),
                contentDescription = "Open Gift",
                colorFilter = ColorFilter.Companion.tint(MaterialTheme.colorScheme.onPrimary)
            )
        }

    }
}