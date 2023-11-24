package com.knowledge.testapp.ui

import androidx.compose.foundation.Image
import com.knowledge.testapp.R
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
fun MainMenuScreen(
    onFindLikingsClicked: () -> Unit,
    onLikingSpectrumJourneyClicked: () -> Unit,
    onAnyfinCanHappenClicked: () -> Unit,
    onProfileClicked: () -> Unit,
    onRankingsClicked: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.forestpathofknowledge),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.mindless_thought_cake),
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 30.dp)
                    .fillMaxWidth(),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Button(onClick = onFindLikingsClicked) {
                Text(text = stringResource(R.string.find_your_likings), color = colorResource(id = R.color.white))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onLikingSpectrumJourneyClicked) {
                Text(text = stringResource(R.string.liking_spectrum_journey), color = colorResource(id = R.color.white))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onAnyfinCanHappenClicked) {
                Text(text = stringResource(R.string.anyfin_can_happen), color = colorResource(id = R.color.white))
            }
            Spacer(modifier = Modifier.height(128.dp))
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 32.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = onProfileClicked) {
                Text(text = stringResource(R.string.profile), color = colorResource(id = R.color.white))
            }
            Spacer(modifier = Modifier.width(32.dp))
            Button(onClick = onRankingsClicked) {
                Text(text = stringResource(R.string.rankings), color = colorResource(id = R.color.white))
            }
        }
    }
}