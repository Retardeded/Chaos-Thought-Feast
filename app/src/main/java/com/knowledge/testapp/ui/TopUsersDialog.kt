package com.knowledge.testapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.knowledge.testapp.R
import com.knowledge.testapp.data.User


@Composable
fun TopUsersDialog(
    backgroundResource: Int,
    title: String,
    gameMode: Int,
    topUsers: List<User>,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        TopUsersScreen(
            backgroundResource = backgroundResource,
            title = title,
            gameMode = gameMode,
            topUsers = topUsers,
            onCloseClick = onDismissRequest
        )
    }
}

@Composable
fun TopUsersScreen(
    backgroundResource: Int,
    title: String,
    gameMode: Int,
    onCloseClick: () -> Unit,
    topUsers: List<User>,
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
                .align(Alignment.Center)
        ) {
            Text(
                text = stringResource(gameMode),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                textAlign = TextAlign.Center,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                itemsIndexed(topUsers) { index, user ->
                    UserItem(user = user, position = index)
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
fun UserItem(user: User, position: Int) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        elevation = 5.dp,
        shape = RoundedCornerShape(8.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Text(
                text = "${position + 1}",
                modifier = Modifier.weight(1f),
                fontSize = 16.sp
            )
            Text(
                text = user.username,
                modifier = Modifier.weight(3f),
                fontSize = 16.sp
            )
            Text(
                text = stringResource(R.string.user_score, user.currentScore),
                modifier = Modifier.weight(2f),
                fontSize = 16.sp
            )
        }
    }
}