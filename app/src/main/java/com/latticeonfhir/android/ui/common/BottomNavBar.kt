package com.latticeonfhir.core.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.latticeonfhir.core.R

@Composable
fun BottomNavBar(
    selectedIndex: Int,
    updateIndex: (Int) -> Unit
) {
    NavigationBar(
        modifier = Modifier.testTag("BOTTOM_NAV_BAR")
    ) {
        val icons =
            listOf(
                R.drawable.patient_list,
                R.drawable.list_alt,
                R.drawable.person_icon
            )
        listOf("My Patients", "Queue", "Profile").forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(id = icons[index]),
                        contentDescription = item,
                        modifier = Modifier
                            .size(30.dp, 28.dp)
                            .padding(vertical = 3.dp)
                    )
                },
                label = {
                    Text(
                        item,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                selected = selectedIndex == index,
                onClick = { updateIndex(index) },
                modifier = Modifier.testTag("$item tab")
            )
        }
    }
}