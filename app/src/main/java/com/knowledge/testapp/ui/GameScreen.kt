package com.knowledge.testapp.ui

import androidx.compose.foundation.*
import com.knowledge.testapp.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.knowledge.testapp.data.Language
import com.knowledge.testapp.utils.ConstantValues
import com.knowledge.testapp.utils.LocalLanguage
import com.knowledge.testapp.viewmodels.WikiParseViewModel
import com.knowledge.testapp.utils.ModifyingStrings

@Composable
fun GameOptionsList(
    selectedOption: String,
    options: List<String>,
    onOptionClick: (String) -> Unit,
    wikiParseViewModel: WikiParseViewModel,
    modifier: Modifier = Modifier,
    listState: LazyListState
) {
    LazyColumn(
        state = listState,
        modifier = modifier.padding(bottom = 35.dp)
    ) {
        items(options) { option ->
            GameOptionItem(
                option = option,
                isSelected = option == selectedOption,
                onOptionClick = { onOptionClick(option) },
                wikiParseViewModel = wikiParseViewModel
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameOptionItem(
    option: String,
    isSelected: Boolean,
    onOptionClick: () -> Unit,
    wikiParseViewModel: WikiParseViewModel
) {
    val backgroundColor = if (isSelected) Color.LightGray else Color.Transparent
    val textColor = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
    val showDescription = remember { mutableStateOf(false) }
    val description = remember { mutableStateOf("") }

    val language = LocalLanguage.current

    LaunchedEffect(showDescription.value) {
        if (showDescription.value) {
            description.value = wikiParseViewModel.fetchIntroText(
                ModifyingStrings.generateArticleDescriptionUrl(language, option)
            )
        }
    }

    LaunchedEffect(key1 = option) {
        showDescription.value = false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    showDescription.value = false
                    onOptionClick()
                },
                onLongClick = {showDescription.value = !showDescription.value}
            )
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = option,
            color = textColor,
            style = MaterialTheme.typography.subtitle1
        )

        if (showDescription.value) {
            Text(
                text = description.value,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun GameScreen(
    currentTitle: MutableState<String>,
    goalTitle: MutableState<String>,
    wikiParseViewModel: WikiParseViewModel,
    pathList: MutableState<List<String>>,
    totalSteps: MutableState<Int>,
    maxSteps: Int = 100,
    win: MutableState<Boolean>,
    onEndQuest: (Boolean, Int, List<String>) -> Unit
) {
    val options by wikiParseViewModel.options.observeAsState(emptyList())
    val listState = rememberLazyListState()

    var showDialog by remember { mutableStateOf(false) }
    var startFetchDescription by remember { mutableStateOf(false) }
    val description = remember { mutableStateOf("") }

    val progress = totalSteps.value.toFloat() / maxSteps

    val language = LocalLanguage.current

    LaunchedEffect(startFetchDescription) {
        if (startFetchDescription) {
            description.value = wikiParseViewModel.fetchIntroText(
                ModifyingStrings.generateArticleDescriptionUrl(language, goalTitle.value)
            )
            showDialog = true
            startFetchDescription = false
        }
    }

    LaunchedEffect(currentTitle) {
        wikiParseViewModel.fetchTitles(ModifyingStrings.generateArticleUrl(language, currentTitle.value))
    }

    LaunchedEffect(options) {
        listState.scrollToItem(0)
    }

    fun goBack() {
        if (pathList.value.size > 1) {
            val newCurrentTitle = pathList.value[pathList.value.size - 2]
            currentTitle.value = newCurrentTitle
            pathList.value = pathList.value.dropLast(1)
            totalSteps.value = totalSteps.value - 1

            wikiParseViewModel.fetchTitles(
                ModifyingStrings.generateArticleUrl(language, newCurrentTitle)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = currentTitle.value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            textAlign = TextAlign.Center,
            color = Color(0xFF363A43),
            fontSize = 16.sp
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.concept_to_found) + " " + goalTitle.value,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                textAlign = TextAlign.Center,
                color = Color(0xFF363143),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            IconButton(
                onClick = { startFetchDescription = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Article Description"
                )
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(goalTitle.value) },
                text = { Text(description.value) },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 5.dp, bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .weight(1f)
                    .height(5.dp)
            )

            Text(
                text = "${totalSteps.value}/$maxSteps",
                color = Color(0xFF7A8089),
                modifier = Modifier.padding(5.dp),
                fontSize = 14.sp
            )
        }

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            val (list, bottomBar) = createRefs() // Create references for the composables

            GameOptionsList(
                selectedOption = currentTitle.value,
                options = options,
                onOptionClick = { option ->
                    pathList.value = pathList.value + option
                    totalSteps.value = totalSteps.value + 1
                    currentTitle.value = option

                    if (option == goalTitle.value.replace("_", " ")) {
                        win.value = true
                        onEndQuest(win.value, totalSteps.value, pathList.value)
                    } else {
                        wikiParseViewModel.fetchTitles(ModifyingStrings.generateArticleUrl(
                            language, option))
                    }
                },
                wikiParseViewModel = wikiParseViewModel,
                listState = listState,
                modifier = Modifier
                    .constrainAs(list) {
                        top.linkTo(parent.top)
                        bottom.linkTo(bottomBar.top)
                    }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
                    .constrainAs(bottomBar) {
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Button(
                    onClick = { onEndQuest(win.value, totalSteps.value, pathList.value) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                ) {
                    Text(
                        text = stringResource(R.string.end_quest),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { goBack() },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                ) {
                    Text(
                        text = stringResource(R.string.go_back),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}