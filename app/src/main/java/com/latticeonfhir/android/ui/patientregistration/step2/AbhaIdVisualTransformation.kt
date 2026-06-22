package com.latticeonfhir.android.ui.patientregistration.step2

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation


class AbhaIdVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {

        val formatted = buildString {
            text.text.forEachIndexed { index, char ->
                append(char)

                if (
                    (index == 1 || index == 5 || index == 9) &&
                    index != text.lastIndex
                ) {
                    append("-")
                }
            }
        }

        val offsetMapping = object : OffsetMapping {

            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 2 -> offset
                    offset <= 6 -> offset + 1
                    offset <= 10 -> offset + 2
                    offset <= 14 -> offset + 3
                    else -> 17
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 2 -> offset
                    offset <= 7 -> offset - 1
                    offset <= 12 -> offset - 2
                    offset <= 17 -> offset - 3
                    else -> 14
                }
            }
        }

        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = offsetMapping
        )
    }
}