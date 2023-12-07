package com.knowledge.testapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.knowledge.testapp.R
import com.knowledge.testapp.data.PathRecord

@Composable
fun ProfilePathsDialog(
    winningPaths: Boolean,
    username: String,
    pathsData: List<PathRecord>,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        PathsProfileScreen(
            username = username,
            paths = pathsData,
            isWinning = winningPaths,
            onCloseClick = onDismissRequest
        )
    }
}

@Composable
fun PathsProfileScreen(
    username: String,
    paths: List<PathRecord>,
    isWinning: Boolean,
    onCloseClick: () -> Unit
) {
    val backgroundResource = if (isWinning) R.drawable.foundcorrectpathwin3 else R.drawable.deadendpathlose
    val titleText = if (isWinning) R.string.winning_paths else R.string.losing_paths

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = backgroundResource),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = username,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = stringResource(titleText),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                textAlign = TextAlign.Center,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                elevation = 5.dp,
                shape = RoundedCornerShape(8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    items(paths) { path ->
                        PathItem(path)
                    }
                }
            }
        }

        Button(
            onClick = onCloseClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 4.dp, end = 4.dp)
                .size(24.dp),
            contentPadding = PaddingValues(all = 0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun PathItem(pathRecord: PathRecord) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = pathRecord.startConcept,
                fontSize = 13.sp,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier.wrapContentWidth(Alignment.Start)
            )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "To",
                modifier = Modifier
                    .size(24.dp)
                    .padding(horizontal = 4.dp)
            )
            Text(
                text = pathRecord.goalConcept,
                fontSize = 13.sp,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier.wrapContentWidth(Alignment.End)
            )
        }
        Text(
            text = pathRecord.path.joinToString(" -> "),
            fontSize = 12.sp,
        )
    }
}