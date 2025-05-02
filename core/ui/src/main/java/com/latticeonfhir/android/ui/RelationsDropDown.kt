package com.latticeonfhir.android.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.latticeonfhir.utils.converters.responseconverter.RelationshipList

@Composable
fun RelationsDropDown(
    gender: String,
    relation: String,
    expanded: Boolean,
    updateRelation: (Boolean, String) -> Unit
) {
    Column(
        modifier = Modifier.testTag("RELATIONS_DROPDOWN")
    ) {
        val relationsList =
            RelationshipList.getRelationshipList(gender)
        TextField(
            value = relation,
            onValueChange = {},
            trailingIcon = {
                IconButton(onClick = {
                    updateRelation(false, "")
                }) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = ""
                    )
                }
            },
            readOnly = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            placeholder = {
                Text(
                    text = "e.g. ${relationsList[0]}",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            interactionSource = remember {
                MutableInteractionSource()
            }.also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            updateRelation(false, "")
                        }
                    }
                }
            },
        )
        DropdownMenu(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.4f),
            expanded = expanded,
            onDismissRequest = {
                updateRelation(false, "")
            },
        ) {
            relationsList.forEach { label ->
                DropdownMenuItem(
                    onClick = {
                        updateRelation(true, label)
                    },
                    text = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }
    }
}