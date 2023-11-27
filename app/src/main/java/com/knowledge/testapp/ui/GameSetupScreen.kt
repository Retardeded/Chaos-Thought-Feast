package com.knowledge.testapp.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import com.knowledge.testapp.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knowledge.testapp.data.GameMode
import com.knowledge.testapp.viewmodels.RandomArticleViewModel

@Composable
fun GameSetupScreen(
    gameMode: GameMode,
    onStartButtonClick: (GameMode, String, String, String, String, Boolean, Boolean) -> Unit,
    onGoToMainMenuActivity: () -> Unit,
    onGoToProfileActivity: () -> Unit,
    handleFetchRandomArticle: (Boolean, String, String, (Boolean) -> Unit) -> Unit,
    startTitle: MutableState<String>,
    goalTitle: MutableState<String>,
    checkTitleCorrectness: (String, (Boolean) -> Unit) -> Unit,
    selectedCategory: MutableState<String>,
    typedKeyword: MutableState<String>,
    fetchArticleDescription: (String, (String) -> Unit) -> Unit,
    randomArticleViewModel: RandomArticleViewModel,
) {
    val showDropdown = remember { mutableStateOf(false) }
    val iconButtonPosition = remember { mutableStateOf(Offset(200f, 200f)) }
    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    val isStartTitleCorrect = remember { mutableStateOf(true) }
    val isGoalTitleCorrect = remember { mutableStateOf(true) }

    var backgroundResource:Int
    var titleResource:Int

    val showDescriptionDialog = remember { mutableStateOf(false) }
    val descriptionDialogTitle = remember { mutableStateOf("") }
    val descriptionDialogContent = remember { mutableStateOf("") }

    val categoriesData by randomArticleViewModel.categories.observeAsState(initial = emptyMap())

    fun handleLongPress(title: String) {
        fetchArticleDescription(title) { description ->
            descriptionDialogTitle.value = title
            descriptionDialogContent.value = description
            showDescriptionDialog.value = true
        }
    }

    fun updateTitleCorrectness(title: String, isStart: Boolean) {
        checkTitleCorrectness(title) { isCorrect ->
            if (isStart) {
                isStartTitleCorrect.value = isCorrect
            } else {
                isGoalTitleCorrect.value = isCorrect
            }
        }
    }

    LaunchedEffect(categoriesData) {
        categoriesData.keys.forEach { category ->
            expandedStates[category] = false
        }
    }

    when (gameMode) {
        GameMode.FIND_YOUR_LIKINGS -> {
            backgroundResource = R.drawable.findyourlikings
            titleResource = R.string.find_your_likings
        }
        GameMode.LIKING_SPECTRUM_JOURNEY -> {
            backgroundResource = R.drawable.likingspecturmjourney
            titleResource = R.string.liking_spectrum_journey
        }
        GameMode.ANYFIN_CAN_HAPPEN -> {
            backgroundResource = R.drawable.anyfin_can_happen
            titleResource = R.string.anyfin_can_happen
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = backgroundResource),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(titleResource),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp)
            )

            // Example CardView
            Card(
                modifier = Modifier.padding(horizontal = 20.dp),
                elevation = 5.dp,
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (gameMode == GameMode.FIND_YOUR_LIKINGS) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            IconButton(
                                onClick = {
                                    handleLongPress(startTitle.value)
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(end = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Article Description",
                                    tint = Color.Unspecified
                                )
                            }

                            OutlinedTextField(
                                value = startTitle.value,
                                onValueChange = {
                                    startTitle.value = it
                                    updateTitleCorrectness(it, true)
                                },
                                textStyle = if (isStartTitleCorrect.value) TextStyle.Default else TextStyle(textDecoration = TextDecoration.LineThrough),
                                label = { Text(stringResource(R.string.enter_starting_concept)) },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            )

                            IconButton(
                                onClick = {
                                    handleFetchRandomArticle(true, selectedCategory.value, typedKeyword.value,
                                        { isCorrect ->
                                        isStartTitleCorrect.value = isCorrect
                                    })
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_randomicon),
                                    contentDescription = "Random Start Title",
                                    tint = Color.Unspecified
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (gameMode == GameMode.FIND_YOUR_LIKINGS || gameMode == GameMode.LIKING_SPECTRUM_JOURNEY) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = {
                                    handleLongPress(goalTitle.value)
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(end = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Article Description",
                                    tint = Color.Unspecified
                                )
                            }

                            OutlinedTextField(
                                value = goalTitle.value,
                                onValueChange = {
                                    goalTitle.value = it
                                    updateTitleCorrectness(it, false)
                                },
                                textStyle = if (isGoalTitleCorrect.value) TextStyle.Default else TextStyle(textDecoration = TextDecoration.LineThrough),
                                label = { Text(stringResource(R.string.enter_concept_you_want_reach)) },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onLongPress = {
                                                handleLongPress(goalTitle.value)
                                            }
                                        )
                                    }
                            )

                            IconButton(
                                onClick = {
                                    handleFetchRandomArticle(false, selectedCategory.value, typedKeyword.value
                                        , { isCorrect ->
                                        isGoalTitleCorrect.value = isCorrect
                                    })
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_randomicon),
                                    contentDescription = "Random Goal Title",
                                    tint = Color.Unspecified
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Button inside the Card
                    Button(
                        onClick = {
                            onStartButtonClick(
                                gameMode,
                                startTitle.value,
                                goalTitle.value,
                                typedKeyword.value,
                                selectedCategory.value,
                                isStartTitleCorrect.value,
                                isGoalTitleCorrect.value
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(text = "Start", fontSize = 18.sp)
                    }
                }
            }

            if (showDescriptionDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDescriptionDialog.value = false },
                    title = { Text(descriptionDialogTitle.value) },
                    text = { Text(descriptionDialogContent.value) },
                    confirmButton = {
                        Button(onClick = { showDescriptionDialog.value = false }) {
                            Text("OK")
                        }
                    }
                )
            }

            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                elevation = 5.dp,
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.specify_the_category),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF363A43),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = selectedCategory.value,
                            onValueChange = { /* Do nothing to make it read-only */ },
                            label = { Text(stringResource(R.string.select_specified_category)) },
                            readOnly = true,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )

                        IconButton(
                            onClick = {
                                showDropdown.value = !showDropdown.value
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_plus),
                                contentDescription = "Expand",
                                tint = Color.Unspecified
                            )
                        }

                        DropdownMenu(
                            expanded = showDropdown.value,
                            onDismissRequest = { showDropdown.value = false },
                            offset = DpOffset(x = iconButtonPosition.value.x.dp, y = iconButtonPosition.value.y.dp),
                            modifier = Modifier.width(IntrinsicSize.Max)
                        ) {
                            Column {
                                val categoryEntries = categoriesData.entries.toList()
                                categoryEntries.forEachIndexed { index, (category, subcategories) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { expandedStates[category] = !expandedStates.getOrDefault(category, false) }
                                            .padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end=16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ArrowDropDown,
                                            contentDescription = "Expand or Collapse",
                                            modifier = Modifier
                                                .padding(end = 8.dp)
                                                .rotate(if (expandedStates.getOrDefault(category, false)) 180f else 0f),
                                            tint = Color.Gray
                                        )

                                        Text(
                                            text = category,
                                            modifier = Modifier.weight(1.5f),
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.W500
                                        )
                                    }
                                    if (index < categoriesData.keys.size - 1) {
                                        Divider()
                                    }

                                    if (expandedStates.getOrDefault(category, false)) {
                                        subcategories.forEach { subcategory ->
                                            Text(
                                                text = subcategory,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        selectedCategory.value = subcategory
                                                        showDropdown.value = false
                                                    }
                                                    .padding(start = 40.dp, top = 8.dp, bottom = 8.dp, end = 8.dp), // Adjust 'start' padding to align with categories
                                                fontSize = 19.sp,
                                                fontWeight = FontWeight.W400
                                            )
                                            Divider()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                elevation = 5.dp,
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.enter_a_filter_keyword),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF363A43),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = typedKeyword.value,
                        onValueChange = { typedKeyword.value = it },
                        label = { Text(stringResource(R.string.enter_a_keyword)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            color = Color(0xFF363A43),
                            fontSize = 18.sp
                        )
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = onGoToMainMenuActivity) {
                    Text(text = stringResource(R.string.main_menu))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = onGoToProfileActivity) {
                    Text(text = stringResource(R.string.profile))
                }
            }
        }
    }
}