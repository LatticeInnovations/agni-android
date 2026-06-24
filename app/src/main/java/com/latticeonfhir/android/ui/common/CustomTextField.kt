package com.latticeonfhir.android.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
    singleLine: Boolean = true,
    modifier: Modifier = Modifier,
    updateValue: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length <= maxLength)
                updateValue(it)
        },
        modifier = modifier
            .fillMaxWidth(weight)
            .testTag(label),
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(
            imeAction = if (label == stringResource(id = R.string.email)) ImeAction.Done else ImeAction.Next,
            keyboardType = keyboardType,
            capitalization = keyboardCapitalization
        ),
        isError = isError,
        supportingText = if (isError && error.isNotBlank()) {
            {
                Text(text = error, style = MaterialTheme.typography.bodySmall)
            }
        } else null
    )
}

@Composable
fun CustomTextFieldWithLength(
    modifier: Modifier = Modifier,
    value: String,
    label: String? = null,
    placeholder: String? = null,
    weight: Float,
    maxLength: Int,
    isError: Boolean,
    error: String? = null,
    keyboardType: KeyboardType,
    keyboardCapitalization: KeyboardCapitalization,
    singleLine: Boolean = true,
    updateValue: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length <= maxLength)
                updateValue(it)
        },
        modifier = modifier
            .fillMaxWidth(weight),
        label = if (label == null) null else {
            {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        placeholder = if (placeholder == null) null else {
            {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            capitalization = keyboardCapitalization
        ),
        isError = isError,
        supportingText = {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isError && error != null) Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${value.length}/$maxLength",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}