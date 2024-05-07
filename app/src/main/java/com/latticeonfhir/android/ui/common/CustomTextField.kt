package com.latticeonfhir.android.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.latticeonfhir.android.R

@Composable
fun CustomTextField(
    value: String,
    label: String,
    weight: Float,
    maxLength: Int,
    isError: Boolean,
    error: String,
    keyboardType: KeyboardType,
    keyboardCapitalization: KeyboardCapitalization,
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
            imeAction = if (label == stringResource(id = R.string.email)) ImeAction.Done else ImeAction.Next,
            keyboardType = keyboardType,
            capitalization = keyboardCapitalization
        ),
        isError = isError,
        supportingText = {
            if (isError) Text(text = error, style = MaterialTheme.typography.bodySmall)
        }
    )
}