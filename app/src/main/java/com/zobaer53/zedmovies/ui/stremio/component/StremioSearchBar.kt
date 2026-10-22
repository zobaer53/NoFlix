package com.zobaer53.zedmovies.ui.stremio.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme

@Composable
fun StremioSearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    selectedType: String?,
    onTypeSelected: (String?) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChanged,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Search movies & series...",
                        color = zedMoviesTheme.colors.textSecondary,
                        fontSize = 14.sp
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = zedMoviesTheme.colors.accent,
                    unfocusedBorderColor = Color(0xFF2F2A47),
                    focusedContainerColor = zedMoviesTheme.colors.primaryVariant,
                    unfocusedContainerColor = zedMoviesTheme.colors.primaryVariant,
                    focusedTextColor = zedMoviesTheme.colors.textPrimary,
                    unfocusedTextColor = zedMoviesTheme.colors.textPrimary
                ),
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Clear",
                                tint = zedMoviesTheme.colors.textSecondary
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() })
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onSearch,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = zedMoviesTheme.colors.accent
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Search",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Type selection chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChipItem(
                label = "All",
                isSelected = selectedType == null,
                onClick = { onTypeSelected(null) }
            )
            FilterChipItem(
                label = "Movies",
                isSelected = selectedType == "movie",
                onClick = { onTypeSelected("movie") }
            )
            FilterChipItem(
                label = "Series",
                isSelected = selectedType == "series",
                onClick = { onTypeSelected("series") }
            )
        }
    }
}

@Composable
fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) zedMoviesTheme.colors.accent else zedMoviesTheme.colors.primaryVariant
    val textColor = if (isSelected) Color.Black else zedMoviesTheme.colors.textPrimary

    Text(
        text = label,
        modifier = modifier
            .background(bgColor, RoundedCornerShape(999.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        color = textColor,
        fontSize = 12.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
    )
}

