package com.heartcare.agni.ui.historyandtests.priordx

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.heartcare.agni.ui.common.ExpandableCard
import com.heartcare.agni.ui.historyandtests.HistoryTakingAndTestsViewModel

@Composable
fun PriorDxView(
    viewModel: HistoryTakingAndTestsViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            viewModel.priorDxList.forEach { priorDx ->
                ExpandableCard()
            }
        }
    }
}