package com.heartcare.agni.ui.prescription.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.heartcare.agni.R
import com.heartcare.agni.ui.prescription.PrescriptionViewModel
import com.heartcare.agni.utils.regex.OnlyAlphabetRegex.onlyAlphabets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPrescription(viewModel: PrescriptionViewModel) {
    val focusRequester = remember { FocusRequester() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp))
            .testTag("SEARCH_LAYOUT")
            .clickable { },
        verticalArrangement = Arrangement.Top
    ) {
        val containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
        TextField(
            value = viewModel.searchQuery,
            onValueChange = {
                if (it.matches(onlyAlphabets)) viewModel.searchQuery = it
            },
            leadingIcon = {
                IconButton(onClick = {
                    viewModel.searchQuery = ""
                    viewModel.isSearching = false
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "BACK_ICON")
                }
            },
            trailingIcon = {
                if (viewModel.searchQuery.isNotBlank()) {
                    IconButton(onClick = {
                        viewModel.searchQuery = ""
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "CLEAR_ICON")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onGloballyPositioned {
                    focusRequester.requestFocus()
                }
                .testTag("SEARCH_TEXT_FIELD"),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                disabledContainerColor = containerColor,
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (viewModel.searchQuery.isNotBlank()) {
                        viewModel.isSearching = false
                        viewModel.isSearchResult = true
                        viewModel.insertRecentSearch(viewModel.searchQuery.trim()) {}
                        viewModel.getActiveIngredientSearchList(viewModel.searchQuery.trim()) {
                            viewModel.activeIngredientSearchList = it
                        }
                        viewModel.searchQuery = ""
                    } else {
                        viewModel.isSearching = false
                        viewModel.searchQuery = ""
                    }
                }
            ),
            singleLine = true
        )
        LazyColumn(modifier = Modifier.testTag("PREVIOUS_SEARCHES")) {
            items(viewModel.previousSearchList) { listItem ->
                PreviousSearches(
                    listItem, viewModel
                )
            }
        }
    }
}

@Composable
fun PreviousSearches(listItem: String, viewModel: PrescriptionViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                viewModel.searchQuery = listItem
                viewModel.getActiveIngredientSearchList(viewModel.searchQuery.trim()) {
                    viewModel.activeIngredientSearchList = it
                }
                viewModel.isSearching = false
                viewModel.isSearchResult = true
                viewModel.searchQuery = ""
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(id = R.drawable.search_history),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = listItem,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
