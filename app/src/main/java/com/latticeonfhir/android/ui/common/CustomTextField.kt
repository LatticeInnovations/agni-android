package com.latticeonfhir.android.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction

@Composable
fun CustomTextField(
    value: String,
    label: String,
    weight: Float,
    maxLength: Int,
    isError: Boolean,
    error: String,
    updateValue: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length <= maxLength)
                updateValue(it)
        },
        modifier = Modifier
            .fillMaxWidth(weight)
            .testTag(label),
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = if(label == "Email") ImeAction.Done else ImeAction.Next
        ),
        isError = isError,
        supportingText = {
            if (isError) Text(text = error, style = MaterialTheme.typography.bodySmall)
        }
    )
}