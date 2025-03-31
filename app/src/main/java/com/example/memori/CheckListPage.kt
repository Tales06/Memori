package com.example.memori

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CheckListPage (
    isVisibleCheckList: Boolean,
    setIsVisibleCheckList: (Boolean) -> Unit,
    viewModel: CheckListItemViewModel = viewModel(
        factory = CheckListItemViewModelFactory(
            repository = CheckListNoteRepository(
                NoteDatabase.getDatabase(LocalContext.current).checkListDao()
            )
        )
    )
) {

}