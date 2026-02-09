package demo.nexa.clinical_transcription_demo.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import demo.nexa.clinical_transcription_demo.ui.component.NoteCard
import demo.nexa.clinical_transcription_demo.ui.component.SecondaryChip
import demo.nexa.clinical_transcription_demo.ui.state.NoteUiState
import demo.nexa.clinical_transcription_demo.ui.theme.PlauColors
import demo.nexa.clinical_transcription_demo.ui.theme.PlauDimens
import demo.nexa.clinical_transcription_demo.ui.theme.PlauGradients

@Composable
fun NotesListScreen(
    notes: List<NoteUiState>,
    onNoteClick: (NoteUiState) -> Unit,
    onRecordClick: () -> Unit,
    onImportClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenModelCenterClick: (() -> Unit)? = null,
    onOpenBountyBenchClick: (() -> Unit)? = null,
    onTestAsrClick: (() -> Unit)? = null,
    onTestLlmClick: (() -> Unit)? = null
) {
    val navigationBarsPadding = WindowInsets.navigationBars.asPaddingValues()
    val statusBarsPadding = WindowInsets.statusBars.asPaddingValues()
    val listState = rememberLazyListState()
    var query by rememberSaveable { mutableStateOf("") }
    
    var previousNotesCount by rememberSaveable { mutableIntStateOf(notes.size) }
    
    LaunchedEffect(notes.size) {
        if (notes.size > previousNotesCount) {
            listState.animateScrollToItem(0)
        }
        previousNotesCount = notes.size
    }

    val filteredNotes = remember(notes, query) {
        if (query.isBlank()) notes
        else {
            val q = query.trim().lowercase()
            notes.filter { n ->
                n.title.lowercase().contains(q) ||
                    n.date.lowercase().contains(q) ||
                    n.duration.lowercase().contains(q)
            }
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PlauColors.BackgroundAqua)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = PlauDimens.spacingMedium,
                        end = PlauDimens.spacingMedium,
                        top = statusBarsPadding.calculateTopPadding() + PlauDimens.spacingSmall,
                        bottom = PlauDimens.spacingSmall
                    ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "All Recording Notes",
                    fontSize = PlauDimens.textSizeTitle,
                    fontWeight = FontWeight.Medium,
                    color = PlauColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Keep actions on a separate row so narrow devices don't clip/overflow.
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    onOpenModelCenterClick?.let { onClick ->
                        SecondaryChip(text = "Models", onClick = onClick)
                    }
                    onOpenBountyBenchClick?.let { onClick ->
                        SecondaryChip(text = "Bench", onClick = onClick)
                    }
                    SecondaryChip(text = "Settings", onClick = onOpenSettingsClick)
                }

                // TEMPORARY: Test buttons (hidden)
                /*
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    onTestAsrClick?.let { onClick ->
                        androidx.compose.material3.TextButton(
                            onClick = onClick,
                            colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                                contentColor = PlauColors.TextPrimary
                            )
                        ) {
                            Text("TEST ASR", fontSize = 12.sp)
                        }
                    }
                    
                    onTestLlmClick?.let { onClick ->
                        androidx.compose.material3.TextButton(
                            onClick = onClick,
                            colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                                contentColor = PlauColors.TextPrimary
                            )
                        ) {
                            Text("TEST LLM", fontSize = 12.sp)
                        }
                    }
                }
                */
            }

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PlauDimens.spacingMedium, vertical = PlauDimens.spacingSmall),
                placeholder = { Text("Search notes…") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { /* no-op */ })
            )
            
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = PlauDimens.spacingMedium,
                    end = PlauDimens.spacingMedium,
                    top = PlauDimens.spacingSmall,
                    bottom = 120.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredNotes.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (notes.isEmpty()) "No notes yet" else "No matches",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PlauColors.TextPrimary
                                )
                                Text(
                                    text = if (notes.isEmpty()) "Record or import audio to get started." else "Try a different search.",
                                    fontSize = 14.sp,
                                    color = PlauColors.TextSecondary,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(items = filteredNotes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onClick = { onNoteClick(note) }
                        )
                    }
                }
            }
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = navigationBarsPadding.calculateBottomPadding() + PlauDimens.spacingLarge)
                .size(PlauDimens.fabSizeLarge)
                .background(PlauColors.SurfaceWhite, CircleShape)
                .border(PlauDimens.borderWidthThin, PlauColors.BorderMedium, CircleShape)
                .clickable(
                    onClick = onRecordClick,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(PlauDimens.fabInnerSize)
                    .background(
                        brush = PlauGradients.horizontalGradient,
                        shape = CircleShape
                    )
            )
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = PlauDimens.spacingLarge,
                    bottom = navigationBarsPadding.calculateBottomPadding() + 32.dp
                )
                .size(PlauDimens.fabSizeSmall)
                .background(PlauColors.SurfaceWhite, CircleShape)
                .border(PlauDimens.borderWidthThin, PlauColors.BorderLight, CircleShape)
                .clickable(
                    onClick = onImportClick,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Import audio",
                modifier = Modifier.size(PlauDimens.iconSizeDefault),
                tint = PlauColors.IconGray
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NotesListScreenPreview() {
    MaterialTheme {
        NotesListScreen(
            notes = listOf(
                NoteUiState(
                    id = "1",
                    title = "MRN-12345-THER-0002",
                    date = "Jun 16, 2025",
                    duration = "00:00:20",
                    hasTranscript = true
                ),
                NoteUiState(
                    id = "2",
                    title = "MRN-12345-THER-0001",
                    date = "Jun 16, 2025",
                    duration = "00:00:20",
                    hasTranscript = true
                )
            ),
            onNoteClick = {},
            onRecordClick = {},
            onImportClick = {},
            onOpenSettingsClick = {}
        )
    }
}
