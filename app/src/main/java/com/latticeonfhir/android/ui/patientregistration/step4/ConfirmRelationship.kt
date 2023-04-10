package com.latticeonfhir.android.ui.patientregistration.step4

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.ui.theme.Neutral40
import androidx.lifecycle.viewmodel.compose.*

@Composable
fun ConfirmRelationship(
    navController: NavController,
    viewModel: ConfirmRelationshipViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Add member",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Page 4/4",
                style = MaterialTheme.typography.bodySmall,
                color = Neutral40
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
        ) {
            MemberCard(viewModel)
            Spacer(modifier = Modifier.height(24.dp))
            MemberCard(viewModel)
            Spacer(modifier = Modifier.height(24.dp))
        }
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Text(text = "Connect Patient")
        }

        if (viewModel.openDeleteDialog) {
            DeleteDialog(viewModel)
        }

        if (viewModel.openEditDialog) {
            EditDialog(viewModel)
        }
    }
}

@Composable
fun MemberCard(viewModel: ConfirmRelationshipViewModel) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 5.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = "Vikram Pandey is the father of Alok Pandey",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(onClick = { viewModel.openDeleteDialog = true }) {
                Icon(
                    painterResource(id = R.drawable.delete_icon),
                    contentDescription = "delete member",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(onClick = { viewModel.openEditDialog = true }) {
                Icon(
                    painterResource(id = R.drawable.edit_icon),
                    contentDescription = "edit member",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun DeleteDialog(viewModel: ConfirmRelationshipViewModel) {
    AlertDialog(
        onDismissRequest = {
            viewModel.openDeleteDialog = false
        },
        title = {
            Text(
                text = "Discard relation ?",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.testTag("delete dialog title")
            )
        },
        text = {
            Column() {
                Text(
                    "Are you sure you want to remove this relation?",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("delete dialog description 1")
                )
                Text(
                    "Vikram Pandey is the father of Alok Pandey",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .testTag("delete dialog description 2")
                        .padding(start = 10.dp, top = 10.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.openDeleteDialog = false
                }) {
                Text(
                    "Confirm",
                    modifier = Modifier.testTag("delete dialog confirm btn")
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.openDeleteDialog = false
                }) {
                Text(
                    "Cancel",
                    modifier = Modifier.testTag("delete dialog cancel btn")
                )
            }
        }
    )
}

@Composable
fun EditDialog(viewModel: ConfirmRelationshipViewModel) {
    var expanded by remember{
        mutableStateOf(false)
    }
    AlertDialog(
        onDismissRequest = {
            viewModel.openEditDialog = false
        },
        title = {
            Text(
                text = "Edit relation",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.testTag("edit dialog title")
            )
        },
        text = {
            Column {
                Text(
                    "Alok Pandey",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("edit dialog description 1")
                )
                Spacer(modifier = Modifier.height(23.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "is the",
                        style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(10.dp))
                    TextField(
                        value = viewModel.editRelation,
                        onValueChange = {
                            viewModel.editRelation = it
                        },
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "")
                            }
                        },
                        readOnly = true,
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(23.dp))
                Text(text = "of Vikram Pandey.",
                    style = MaterialTheme.typography.bodyLarge)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.openEditDialog = false
                }) {
                Text(
                    "Save",
                    modifier = Modifier.testTag("edit dialog save btn")
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.openEditDialog = false
                }) {
                Text(
                    "Cancel",
                    modifier = Modifier.testTag("edit dialog cancel btn")
                )
            }
        }
    )
}