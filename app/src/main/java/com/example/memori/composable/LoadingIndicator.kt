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