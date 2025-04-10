package com.latticeonfhir.core.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toMonth
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toOneYearFuture
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toOneYearPast
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toSlotDate
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toWeekDay
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toYear
import java.util.Date


@Composable
fun WeekDaysComposable(
    dateScrollState: LazyListState,
    selectedDate: Date,
    weekList: List<Date>,
    callBack: (Boolean?, Date?) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .padding(start = 20.dp, top = 15.dp, bottom = 15.dp)
                .wrapContentSize()
        ) {
            Row(
                modifier = Modifier
                    .testTag("DATE_DROPDOWN")
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        callBack(true, null)
                    }
            ) {
                Column {
                    Text(
                        text = selectedDate.toMonth(),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = selectedDate.toYear(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Icon(Icons.Default.ArrowDropDown, contentDescription = "DROP_DOWN_ICON")
            }
            VerticalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .width(1.dp)
                    .fillMaxHeight(0.07f),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            LazyRow(
                state = dateScrollState,
                modifier = Modifier.testTag("DAYS_TAB_ROW")
            ) {
                items(weekList) { date ->
                    SuggestionChip(
                        onClick = {
                            callBack(null, date)
                        },
                        label = {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = date.toWeekDay(),
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Text(
                                    text = date.toSlotDate(),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        },
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .testTag("DAYS_CHIP"),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = if (selectedDate == date) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surface,
                            labelColor = if (selectedDate == date) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.outline
                        ),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            enabled = date < Date().toOneYearFuture() && date > Date().toTodayStartDate()
                                .toOneYearPast(),
                            borderColor = Color.Transparent
                        ),
                        enabled = date < Date().toOneYearFuture() && date > Date().toTodayStartDate()
                            .toOneYearPast()
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(bottom = 10.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}