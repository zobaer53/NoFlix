

package com.zobaer53.zedmovies.ui.smallcomponent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesTextField
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun zedMoviesSelectionDialog(
    expanded: Boolean,
    onDismiss: () -> Unit,
    values: List<Pair<String, String>>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    val filteredValues by remember {
        derivedStateOf {
            values.filter { (_, value) ->
                value.startsWith(query.trim(), ignoreCase = true)
            }
        }
    }
    val isError = filteredValues.isEmpty()

    if (expanded) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = modifier) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    zedMoviesTheme.colors.primary,
                                    zedMoviesTheme.colors.accent,
                                    zedMoviesTheme.colors.primaryVariant
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    zedMoviesBackButton(
                        modifier = Modifier
                            .padding(zedMoviesTheme.spacing.medium)
                            .align(Alignment.TopStart),
                        onClick = onDismiss
                    )

                    Card(
                        modifier = Modifier.padding(
                            horizontal = zedMoviesTheme.spacing.medium,
                            vertical = 80.dp
                        ),
                        shape = zedMoviesTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(containerColor = zedMoviesTheme.colors.primary)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            zedMoviesTextField(
                                modifier = Modifier.padding(
                                    vertical = zedMoviesTheme.spacing.smallMedium,
                                    horizontal = zedMoviesTheme.spacing.extraMedium
                                ),
                                value = query,
                                onValueChange = { query = it },
                                placeholderResourceId = R.string.search,
                                iconResourceId = R.drawable.ic_search,
                                isError = isError
                            )

                            LazyColumn {
                                items(filteredValues) { item ->
                                    val key = item.first
                                    val value = item.second

                                    Text(
                                        modifier = Modifier
                                            .clickable {
                                                onSelect(key)
                                                onDismiss()
                                            }
                                            .padding(
                                                horizontal = zedMoviesTheme.spacing.extraLarge,
                                                vertical = zedMoviesTheme.spacing.medium
                                            )
                                            .fillMaxWidth(),
                                        text = value,
                                        style = zedMoviesTheme.typography.regular.h4,
                                        color = zedMoviesTheme.colors.textPrimary,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
