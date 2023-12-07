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
import com.knowledge.testapp.data.PathRecord

@Composable
fun UserRecordsDialog(
    backgroundResource: Int,
    title: String,
    gameMode: Int,
    onDismissRequest: () -> Unit,
    userRecords: List<PathRecord>
) {
    Dialog(onDismissRequest = onDismissRequest) {
        UserRecordsScreen(
            backgroundResource = backgroundResource,
            title = title,
            gameMode = gameMode,
            userRecords = userRecords,
            onCloseClick = onDismissRequest
        )
    }
}

@Composable
fun UserRecordsScreen(
    backgroundResource: Int,
    title: String,
    gameMode: Int,
    userRecords: List<PathRecord>,
    onCloseClick: () -> Unit
) {
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
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(gameMode),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(userRecords) { record ->
                    PathRecordItem(pathRecord = record) // Renamed for clarity
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
fun PathRecordItem(pathRecord: PathRecord) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Adjusted padding
        shape = RoundedCornerShape(8.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {
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
            Spacer(modifier = Modifier.padding(bottom = 8.dp))
            Text(
                text = pathRecord.path.joinToString(" -> "),
                textAlign = TextAlign.Start,
                fontSize = 12.sp
            )
        }

    }
}