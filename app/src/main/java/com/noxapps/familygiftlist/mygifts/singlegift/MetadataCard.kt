package com.noxapps.familygiftlist.mygifts.singlegift

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.noxapps.familygiftlist.Indicator
import com.noxapps.familygiftlist.R
import com.noxapps.familygiftlist.metafetcher.MetaFetcher

@Composable
fun MetadataCard(metadata: MetaFetcher) {
    val title = metadata.getTitle()
    val description = metadata.getDescription()
    val imageString = metadata.getImageString()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .heightIn(200.dp)
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerShape(15.dp))
                .align(Alignment.Center)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(15.dp))
            //verticalAlignment = Alignment.CenterVertically
        ) {
            Column() {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)

                    ) {
                        RemoteGiftImage(
                            link = imageString,
                            desc = description,
                            readyState = (true),
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .border(Dp.Hairline, MaterialTheme.colorScheme.outlineVariant)
                        .background(Color.White)
                        .padding(4.dp)

                ) {
                    Column(
                        modifier = Modifier
                            .weight(9f)
                    ) {
                        Text(
                            text = title,
                            maxLines = 1,
                            style = MaterialTheme.typography.titleMedium,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = description,
                            maxLines = 3,
                            style = MaterialTheme.typography.bodySmall,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        modifier = Modifier
                            .weight(1f),
                        onClick = {
                            //todo go to metadata.target on click
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.open_in_new_24px),
                            contentDescription = "open in new"
                        )
                    }
                }
            }

        }

    }
}

@Composable
fun MetadataPlaceholderCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(200.dp)
                .clip(RoundedCornerShape(15.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(15.dp))
                .align(Alignment.Center)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            //verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Indicator()
            }
        }
    }
}