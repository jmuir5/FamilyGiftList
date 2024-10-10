package com.noxapps.familygiftlist.mylists

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
import com.noxapps.familygiftlist.R
import com.noxapps.familygiftlist.data.Gift

@Composable
fun SelectableGiftEntry(gift: Gift, state: MutableState<Boolean>){
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp, 2.dp)
            .background(toggleBackgroundColor)
            .height(IntrinsicSize.Min)
            .clickable { state.value = !state.value }
            .padding(4.dp),
    ) {
        Checkbox(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            checked = state.value,
            onCheckedChange = {
                state.value = it
            }
        )
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            text = gift.name,
            color = toggleTextColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
        )
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            onClick = { /*TODO open gift page*/ }) {
            Image(
                painter = painterResource(id = R.drawable.open_in_new_24px),
                contentDescription = "Open Gift",
                colorFilter = ColorFilter.tint(toggleTextColor)
            )
        }

    }
}