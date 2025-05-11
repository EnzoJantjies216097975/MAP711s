package com.map711s.namibiahockey.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.debounce

@Composable
fun <T> rememberSnapshotList(
    items: List<T>,
    debounceMillis: Long = 300
): List<T> {
    val snapshotList = remember { mutableStateListOf<T>() }

    LaunchedEffect(items) {
        // Update the list after a debounce to avoid rapid updates
        snapshotFlow { items }
            .debounce(debounceMillis)
            .collect { newItems ->
                snapshotList.clear()
                snapshotList.addAll(newItems)
            }
    }

    return snapshotList
}