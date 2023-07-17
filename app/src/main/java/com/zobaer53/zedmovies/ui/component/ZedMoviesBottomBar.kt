
package com.zobaer53.zedmovies.ui.component

import androidx.annotation.FloatRange
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.navigation.TopLevelDestination

@Composable
fun zedMoviesBottomBar(
    destinations: Array<TopLevelDestination>,
    currentDestination: TopLevelDestination,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = zedMoviesTheme.colors.primary
) {
    Surface(
        modifier = modifier,
        color = color
    ) {
        val animationSpec = BottomBarAnimationSpec

        zedMoviesBottomNavLayout(
            modifier = Modifier.windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                )
            ),
            selectedIndex = currentDestination.ordinal,
            itemCount = destinations.size,
            animationSpec = animationSpec,
            indicator = { ZedMoviesBottomNavIndicator() }
        ) {
            destinations.forEach { destination ->
                val selected = destination == currentDestination
                val tint by animateColorAsState(
                    if (selected) {
                        zedMoviesTheme.colors.accent
                    } else {
                        zedMoviesTheme.colors.textSecondary
                    }
                )

                val icon = painterResource(id = destination.iconResourceId)
                val text = stringResource(id = destination.textResourceId)

                zedMoviesBottomNavigationItem(
                    modifier = Modifier
                        //.padding(zedMoviesTheme.spacing.smallMedium)
                        .clip(CircleShape),
                    icon = {
                        Icon(
                            painter = icon,
                            tint = tint,
                            contentDescription = text
                        )
                    },
                    text = {
                        Text(
                            text = text,
                            color = tint,
                            style = zedMoviesTheme.typography.medium.h6,
                            maxLines = 1
                        )
                    },
                    selected = selected,
                    onSelect = { onNavigateToDestination(destination) },
                    animationSpec = animationSpec
                )
            }
        }
    }
}

@Composable
private fun zedMoviesBottomNavLayout(
    selectedIndex: Int,
    itemCount: Int,
    animationSpec: AnimationSpec<Float>,
    indicator: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val selectionFractions = remember(itemCount) {
        List(itemCount) { index ->
            Animatable(if (index == selectedIndex) 1f else 0f)
        }
    }
    selectionFractions.forEachIndexed { index, selectionFraction ->
        val targetValue = if (index == selectedIndex) 1f else 0f
        LaunchedEffect(key1 = targetValue, key2 = animationSpec) {
            selectionFraction.animateTo(targetValue = targetValue, animationSpec = animationSpec)
        }
    }

    val indicatorIndex = remember { Animatable(initialValue = 0f) }
    val targetIndicatorIndex = selectedIndex.toFloat()
    LaunchedEffect(key1 = targetIndicatorIndex) {
        indicatorIndex.animateTo(targetValue = targetIndicatorIndex, animationSpec = animationSpec)
    }

    Layout(
        modifier = modifier.height(72.dp),
        content = {
            content()
            Box(modifier = Modifier.layoutId(IndicatorLayoutId), content = indicator)
        }
    ) { measurables, constraints ->
        require(itemCount == (measurables.size - 1))

        val unselectedWidth = constraints.maxWidth / (itemCount + 1)
        val selectedWidth = 2 * unselectedWidth
        val indicatorMeasurable = measurables.first { it.layoutId == IndicatorLayoutId }

        val itemPlaceables = measurables
            .filterNot { it == indicatorMeasurable }
            .mapIndexed { index, measurable ->
                val width = lerp(
                    start = unselectedWidth,
                    stop = selectedWidth,
                    fraction = selectionFractions[index].value
                )
                measurable.measure(
                    constraints.copy(
                        minWidth = width,
                        maxWidth = width
                    )
                )
            }
        val indicatorPlaceable = indicatorMeasurable.measure(
            constraints.copy(
                minWidth = selectedWidth,
                maxWidth = selectedWidth
            )
        )

        layout(
            width = constraints.maxWidth,
            height = itemPlaceables.maxByOrNull { it.height }?.height ?: 0
        ) {
            val indicatorLeft = indicatorIndex.value * unselectedWidth
            indicatorPlaceable.placeRelative(x = indicatorLeft.toInt(), y = 0)
            var x = 0
            itemPlaceables.forEach { placeable ->
                placeable.placeRelative(x = x, y = 0)
                x += placeable.width
            }
        }
    }
}

@Composable
private fun zedMoviesBottomNavigationItem(
    icon: @Composable BoxScope.() -> Unit,
    text: @Composable BoxScope.() -> Unit,
    selected: Boolean,
    onSelect: () -> Unit,
    animationSpec: AnimationSpec<Float>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.selectable(selected = selected, onClick = onSelect),
        contentAlignment = Alignment.Center
    ) {
        val animationProgress by animateFloatAsState(
            targetValue = if (selected) 1f else 0f,
            animationSpec = animationSpec
        )
        zedMoviesBottomNavItemLayout(
            icon = icon,
            text = text,
            animationProgress = animationProgress
        )
    }
}

@Composable
private fun zedMoviesBottomNavItemLayout(
    icon: @Composable BoxScope.() -> Unit,
    text: @Composable BoxScope.() -> Unit,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float,
    modifier: Modifier = Modifier,
    defaultTransformOrigin: TransformOrigin = TransformOrigin(
        TransformOriginPivotFractionX,
        TransformOriginPivotFractionY
    )
) {
    Layout(
        content = {
            Box(
                modifier = Modifier
                    .layoutId(IconLayoutId)
                    .padding(horizontal = zedMoviesTheme.spacing.extraSmall),
                content = icon
            )
            val scale = lerp(
                start = BottomNavItemLayoutLerpStart,
                stop = BottomNavItemLayoutLerpStop,
                fraction = animationProgress
            )
            Box(
                modifier = Modifier
                    .layoutId(TextLayoutId)
                    .padding(horizontal = zedMoviesTheme.spacing.extraSmall)
                    .graphicsLayer {
                        alpha = animationProgress
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = defaultTransformOrigin
                    },
                content = text
            )
        },
        modifier = modifier
    ) { measurables, constraints ->
        val iconPlaceable = measurables.first { it.layoutId == IconLayoutId }.measure(constraints)
        val textPlaceable = measurables.first { it.layoutId == TextLayoutId }.measure(constraints)

        placeTextAndIcon(
            textPlaceable = textPlaceable,
            iconPlaceable = iconPlaceable,
            width = constraints.maxWidth,
            height = constraints.maxHeight,
            animationProgress = animationProgress
        )
    }
}

@Composable
private fun ZedMoviesBottomNavIndicator(
    modifier: Modifier = Modifier,
    padding: Dp = zedMoviesTheme.spacing.smallMedium,
    color: Color = zedMoviesTheme.colors.primaryVariant,
    shape: Shape = CircleShape
) {
    Spacer(
        modifier = modifier
            .fillMaxSize()
            .padding(padding)
            .background(color = color, shape = shape)
    )
}

private fun MeasureScope.placeTextAndIcon(
    textPlaceable: Placeable,
    iconPlaceable: Placeable,
    width: Int,
    height: Int,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float
): MeasureResult {
    val iconY = (height - iconPlaceable.height) / 2
    val textY = (height - textPlaceable.height) / 2

    val textWidth = textPlaceable.width * animationProgress
    val iconX = (width - textWidth - iconPlaceable.width) / 2
    val textX = iconX + iconPlaceable.width

    return layout(width = width, height = height) {
        iconPlaceable.placeRelative(x = iconX.toInt(), y = iconY)
        if (animationProgress != 0f) {
            textPlaceable.placeRelative(x = textX.toInt(), y = textY)
        }
    }
}

private const val TextLayoutId = "text"
private const val IconLayoutId = "icon"
private const val IndicatorLayoutId = "indicator"
private const val TransformOriginPivotFractionX = 0f
private const val TransformOriginPivotFractionY = 0.5f
private const val BottomNavItemLayoutLerpStart = 0.6f
private const val BottomNavItemLayoutLerpStop = 1f
private val BottomBarAnimationSpec = SpringSpec<Float>(
    stiffness = 800f,
    dampingRatio = 0.8f
)
