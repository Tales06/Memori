/**
 * A composable function that displays a loading indicator with padding.
 *
 * This function uses [ContainedLoadingIndicator] from Material3 with an 8.dp padding.
 * It can be used to indicate loading states in your UI.
 *
 * @sample LoadingIndicator
 *
 * @see ContainedLoadingIndicator
 * @see Modifier.padding
 */
package com.example.memori.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@ExperimentalMaterial3ExpressiveApi
@Preview
fun LoadingIndicator() {
    ContainedLoadingIndicator(
        modifier = Modifier.padding(8.dp),


    )
}