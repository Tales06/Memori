package com.example.memori
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlin.random.Random
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

val kaomoji: List<String> = listOf(
    "╥﹏╥)",
    "(ಥ﹏ಥ)",
    "(ಥ_ಥ)",
    "(´；ω；｀)",
    "(｡•́︿•̀｡)",
    "(ಥ‿ಥ)",
    "(╯︵╰,)",
    "(ﾉД`)",
    "(つ﹏⊂)",
    "(இ﹏இ)",
    "(｀Д´)",
    "(¬_¬)",
    "(눈_눈)",
    "(ノಠ益ಠ)ノ",
    "(ง'̀-'́)ง",
    "(¬▂¬)",
    "(ಠ_ಠ)",
    "(ಠ益ಠ)",
    "(⊙_⊙)",
    "(ﾟoﾟ)",
    "(o_O)",
    "(°ロ°)",
    "(o.o)",
    "(O.O)",
    "(ʘᗩʘ’)",
    "(￣□￣)",
    "Σ(°△°|||)",
    "(⊙_⊙;)",
    "(－_－) zzZ",
    "(￣o￣) zzZ",
    "(∪｡∪)｡｡｡zzz",
    "(︶︹︺)",
    "(っ˘ω˘ς )",
    "(－ω－) zzZ",
    "(´〜｀*) zzz",
    "(≚ᄌ≚)ƶƶ",
    "(∪｡∪) zzZ",
    "(￣ρ￣)..zzZZ"
)

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    navController: NavController,
    noteViewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(
            repository = NotesRepository(
                NoteDatabase.getDatabase(context = LocalContext.current).noteDao()
            )
        )
    )
) {
    val notesState by noteViewModel.allNotes.collectAsStateWithLifecycle(initialValue = emptyList())
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val listState = rememberLazyListState()
    var isSearchBarVisible by remember { mutableStateOf(true) }

    // Osserva il comportamento dello scroll
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        isSearchBarVisible = listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 10
    }

    val randomKaomoji by remember { mutableStateOf(kaomoji.random()) }
    val searchBarHeight by animateDpAsState(targetValue = if (isSearchBarVisible) 70.dp else 0.dp)

    Surface(
        contentColor = MaterialTheme.colorScheme.background
    ) {
        Column {
            Spacer(modifier = Modifier.height(searchBarHeight))

            Box(modifier = Modifier.fillMaxSize()) {
                if (notesState.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = randomKaomoji,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = TextStyle(fontSize = 45.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.align(Alignment.Center)

                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "No notes yet",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(top = 90.dp)
                        )
                    }
                } else {
                    NoteCard(notes = notesState, navController = navController)
                }
            }
        }

        SearchBarComponent(
            navController = navController,
            noteViewModel = noteViewModel,
            modifier = Modifier
                .height(searchBarHeight)
                .padding(8.dp)
        )

        ButtonNote(navController)
    }
}




