package com.knowledge.testapp.ui

import androidx.compose.foundation.*
import com.knowledge.testapp.R
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoseScreen(
    goalConcept: String,
    pathText: String,
    onGoToMainMenu: () -> Unit,
    onTryAgain: () -> Unit
) {
    val yourWikiPathString = stringResource(id = R.string.your_wiki_path)
    val primaryColor = colorResource(id = R.color.color_primary)
    val resultText = stringResource(id = R.string.have_not_found_concept, goalConcept)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.deadendpathlose),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Text at the very top center
        Text(
            text = stringResource(id = R.string.you_failed),
            color = Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        ) {
            Text(
                text = yourWikiPathString,
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Text(
                text = pathText,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Text(
                text = resultText,
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp)) // Space before buttons

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Button(
                    onClick = onTryAgain,
                    colors = ButtonDefaults.buttonColors(backgroundColor = primaryColor),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.another_try),
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }

                Button(
                    onClick = onGoToMainMenu,
                    colors = ButtonDefaults.buttonColors(backgroundColor = primaryColor),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.main_menu),
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}