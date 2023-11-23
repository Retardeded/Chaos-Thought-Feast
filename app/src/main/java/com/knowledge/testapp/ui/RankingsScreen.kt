package com.knowledge.testapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.ui.unit.dp
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.knowledge.testapp.R

@Composable
fun RankingsScreen(
    onFindYourLikingsClick: () -> Unit,
    onLikingSpectrumJourneyClick: () -> Unit,
    onAnyfinCanHappenClick: () -> Unit,
    onFindYourLikingsRecordsClick: () -> Unit,
    onLikingSpectrumJourneyRecordsClick: () -> Unit,
    onAnyfinCanHappenRecordsClick: () -> Unit,
    onGoToMainMenuClick: () -> Unit,
    onGoToProfileClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.statsbackground),
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
                text = stringResource(R.string.top_users_in_each_mode),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp)
            )

            Divider(color = Color.White, thickness = 1.dp)

            // User-Specific World Records Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = onFindYourLikingsClick) {
                    Text(text = stringResource(R.string.find_your_likings))
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onLikingSpectrumJourneyClick) {
                    Text(text = stringResource(R.string.liking_spectrum_journey))
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onAnyfinCanHappenClick) {
                    Text(text = stringResource(R.string.anyfin_can_happen))
                }
            }

            Divider(color = Color.White, thickness = 1.dp)

            Text(
                text = stringResource(R.string.your_personal_bests),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp)
            )

            Divider(color = Color.White, thickness = 1.dp)

            // User-Specific World Records Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = onFindYourLikingsRecordsClick) {
                    Text(text = stringResource(R.string.find_your_likings_records))
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onLikingSpectrumJourneyRecordsClick) {
                    Text(text = stringResource(R.string.liking_spectrum_journey_records))
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onAnyfinCanHappenRecordsClick) {
                    Text(text = stringResource(R.string.anyfin_can_happen_records))
                }
            }

            Divider(color = Color.White, thickness = 1.dp)

            // Spacer to push bottom elements to the bottom
            Spacer(Modifier.weight(1f))

            // Main Menu and Profile Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onGoToMainMenuClick,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(text = stringResource(R.string.main_menu), color = Color.White)
                }

                Button(onClick = onGoToProfileClick) {
                    Text(text = stringResource(R.string.profile), color = Color.White)
                }
            }
        }
    }
}