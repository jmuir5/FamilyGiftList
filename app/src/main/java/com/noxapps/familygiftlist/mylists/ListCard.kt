package com.noxapps.familygiftlist.mylists

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.noxapps.familygiftlist.R
import com.noxapps.familygiftlist.data.ListWithGifts
import com.noxapps.familygiftlist.navigation.Paths

@Composable
fun ListCard(list: ListWithGifts, index:Int, indexOfExpanded: MutableState<Int>, navHostController: NavHostController) {
    val thisExpanded by remember { derivedStateOf { indexOfExpanded.value == index } }
    val toggleBackgroundColor =
        if(thisExpanded){
            MaterialTheme.colorScheme.secondary
        }else{
            MaterialTheme.colorScheme.secondaryContainer
        }

    val toggleTextColor =
        if(thisExpanded){
            MaterialTheme.colorScheme.onSecondary
        }else{
            MaterialTheme.colorScheme.onSecondaryContainer
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp, 2.dp)
            .background(toggleBackgroundColor)
            .height(IntrinsicSize.Min)
            .clickable {
                if (thisExpanded) {
                    navHostController.navigate("${Paths.SingleList.Path}/${list.giftList.listId}")//todo navigate to gift list
                } else {
                    indexOfExpanded.value = index
                }
            }
            .padding(4.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                text = list.giftList.listName,
                color = toggleTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "${list.gifts.size} items",
                color = toggleTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyMedium

            )
            AnimatedVisibility(thisExpanded) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = list.giftList.description,
                    color = toggleTextColor,
                    textAlign = TextAlign.Center,
                    //style = MaterialTheme.typography.
                )
            }

        }
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            onClick = {
                if (thisExpanded) {
                    navHostController.navigate("${Paths.SingleList.Path}/${list.giftList.listId}")
                } else {
                    indexOfExpanded.value = index
                }
            }) {
            Crossfade(targetState = thisExpanded, label = "expand/go to list") { thisExpanded ->
                if (thisExpanded) {
                    Image(
                        painter = painterResource(id = R.drawable.chevron_right_24px),
                        contentDescription = "Open Gift",
                        colorFilter = ColorFilter.tint(toggleTextColor)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.keyboard_arrow_down_24px),
                        contentDescription = "Open Gift",
                        colorFilter = ColorFilter.tint(toggleTextColor)
                    )
                }
            }
        }

    }

}