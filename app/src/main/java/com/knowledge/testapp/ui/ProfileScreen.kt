package com.knowledge.testapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.ui.unit.dp
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.knowledge.testapp.R

@Composable
fun ProfileScreen(
    onShowWinningPaths: () -> Unit,
    onShowLosingPaths: () -> Unit,
    onClearPaths: () -> Unit,
    onDeleteAccount: () -> Unit,
    onLogout: () -> Unit,
    onGoToMainMenu: () -> Unit,
    onGoToRankings: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(end = 8.dp),
                    onClick = onShowWinningPaths
                ) {
                    Text(
                        text = stringResource(R.string.winning_paths)
                    )
                }

                Spacer(Modifier.width(16.dp))

                Button(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(start = 8.dp),
                    onClick = onShowLosingPaths
                ) {
                    Text(
                        text = stringResource(R.string.losing_paths)
                    )
                }
            }

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onClearPaths
            ) {
                Text(text = stringResource(R.string.clear_your_local_data))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onDeleteAccount
            ) {
                Text(text = stringResource(R.string.delete_account))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onLogout
            ) {
                Text(text = stringResource(R.string.logout), color = Color.White)
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier.padding(start = 16.dp),
                    onClick = onGoToMainMenu
                ) {
                    Text(text = stringResource(R.string.main_menu), color = Color.White)
                }

                Button(
                    modifier = Modifier.padding(start = 16.dp),
                    onClick = onGoToRankings
                ) {
                    Text(text = stringResource(R.string.rankings), color = Color.White)
                }
            }
        }
    }
}