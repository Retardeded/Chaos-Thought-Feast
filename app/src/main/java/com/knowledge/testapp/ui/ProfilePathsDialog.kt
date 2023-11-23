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
import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
        WinningPathsProfileScreen(
            username = username,
            paths = pathsData,
            isWinning = winningPaths,
            onCloseClick = onDismissRequest
        )
    }
}

@Composable
fun WinningPathsProfileScreen(
    username: String,
    paths: List<PathRecord>,
    isWinning: Boolean,
    onCloseClick: () -> Unit
) {
    val backgroundResource = if (isWinning) R.drawable.foundcorrectpathwin3 else R.drawable.deadendpathlose
    val titleText = if (isWinning) R.string.winning_paths.toString() else R.string.losing_paths.toString()

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
                text = titleText,
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
                .padding(top = 16.dp, end = 16.dp)
        ) {
            Text("Close")
        }
    }
}

@Composable
fun PathItem(path: PathRecord) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = path.startingConcept,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )
        Text(
            text = path.goalConcept,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )
        Text(
            text = path.path.joinToString(" -> "),
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}