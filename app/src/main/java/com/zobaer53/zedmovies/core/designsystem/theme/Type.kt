

package com.zobaer53.zedmovies.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.zobaer53.zedmovies.R

private val Montserrat = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold)
)

internal val Typography = defaultTypography()

@Immutable
class zedMoviesTypography {
    val regular = Regular()
    val medium = Medium()
    val semiBold = SemiBold()

    @Immutable
    data class Regular(
        val default: TextStyle = defaultTextStyle(),
        val h1: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = H1FontSize,
            lineHeight = H1LineHeight
        ),
        val h2: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = H2FontSize,
            lineHeight = H2LineHeight
        ),
        val h3: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = H3FontSize,
            lineHeight = H3LineHeight
        ),
        val h4: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = H4FontSize,
            lineHeight = H4LineHeight
        ),
        val h5: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = H5FontSize,
            lineHeight = H5LineHeight
        ),
        val h6: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = H6FontSize,
            lineHeight = H6LineHeight
        ),
        val h7: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = H7FontSize,
            lineHeight = H7LineHeight
        )
    )

    @Immutable
    data class Medium(
        val default: TextStyle = defaultTextStyle(),
        val h1: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = H1FontSize,
            lineHeight = H1LineHeight
        ),
        val h2: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = H2FontSize,
            lineHeight = H2LineHeight
        ),
        val h3: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = H3FontSize,
            lineHeight = H3LineHeight
        ),
        val h4: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = H4FontSize,
            lineHeight = H4LineHeight
        ),
        val h5: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = H5FontSize,
            lineHeight = H5LineHeight
        ),
        val h6: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = H6FontSize,
            lineHeight = H6LineHeight
        ),
        val h7: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = H7FontSize,
            lineHeight = H7LineHeight
        )
    )

    @Immutable
    data class SemiBold(
        val default: TextStyle = defaultTextStyle(),
        val h1: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = H1FontSize,
            lineHeight = H1LineHeight
        ),
        val h2: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = H2FontSize,
            lineHeight = H2LineHeight
        ),
        val h3: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = H3FontSize,
            lineHeight = H3LineHeight
        ),
        val h4: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = H4FontSize,
            lineHeight = H4LineHeight
        ),
        val h5: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = H5FontSize,
            lineHeight = H5LineHeight
        ),
        val h6: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = H6FontSize,
            lineHeight = H6LineHeight
        ),
        val h7: TextStyle = defaultTextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = H7FontSize,
            lineHeight = H7LineHeight
        )
    )
}

private fun defaultTextStyle(
    fontWeight: FontWeight,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    letterSpacing: TextUnit = LetterSpacing
) = TextStyle(
    fontFamily = Montserrat,
    fontWeight = fontWeight,
    fontSize = fontSize,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing
)

private fun defaultTextStyle(
    letterSpacing: TextUnit = LetterSpacing
) = TextStyle(
    fontFamily = Montserrat,
    letterSpacing = letterSpacing
)

private fun defaultTypography() = with(zedMoviesTypography()) {
    Typography(
        displayLarge = semiBold.h1,
        displayMedium = semiBold.h2,
        displaySmall = semiBold.h3,
        headlineLarge = medium.h1,
        headlineMedium = medium.h2,
        headlineSmall = medium.h3,
        titleLarge = medium.h4,
        titleMedium = medium.h5,
        titleSmall = medium.h6,
        bodyLarge = regular.h1,
        bodyMedium = regular.h2,
        bodySmall = regular.h3,
        labelLarge = regular.h4,
        labelMedium = regular.h5,
        labelSmall = regular.h6,
    )
}

internal val LocalzedMoviesTypography = staticCompositionLocalOf { zedMoviesTypography() }

private val H1FontSize = 28.sp
private val H1LineHeight = (34.13f).sp

private val H2FontSize = 24.sp
private val H2LineHeight = (29.26f).sp

private val H3FontSize = 18.sp
private val H3LineHeight = (21.94f).sp

private val H4FontSize = 16.sp
private val H4LineHeight = (19.5f).sp

private val H5FontSize = 14.sp
private val H5LineHeight = (17.07f).sp

private val H6FontSize = 12.sp
private val H6LineHeight = (14.63f).sp

private val H7FontSize = 10.sp
private val H7LineHeight = (12.19f).sp

private val LetterSpacing = (0.12f).sp
