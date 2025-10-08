package com.marpal.note_scanner.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ModernScrollIndicator(
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val isScrolling by remember {
        derivedStateOf { listState.isScrollInProgress }
    }
    
    val firstVisibleItemIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }
    
    val totalItemsCount by remember {
        derivedStateOf { listState.layoutInfo.totalItemsCount }
    }
    
    var showIndicator by remember { mutableStateOf(false) }
    
    // Auto-hide scroll indicator after 2 seconds of no scrolling
    LaunchedEffect(isScrolling) {
        if (isScrolling) {
            showIndicator = true
        } else {
            delay(2000)
            showIndicator = false
        }
    }
    
    // Calculate scroll progress
    val scrollProgress = if (totalItemsCount > 0) {
        (firstVisibleItemIndex.toFloat() + (listState.firstVisibleItemScrollOffset / 1000f)) / totalItemsCount.toFloat()
    } else {
        0f
    }
    
    val indicatorAlpha by animateFloatAsState(
        targetValue = if (showIndicator && totalItemsCount > 0) 0.7f else 0f,
        animationSpec = tween(300),
        label = "indicatorAlpha"
    )
    
    if (totalItemsCount > 0) {
        Box(
            modifier = modifier
                .fillMaxHeight()
                .width(4.dp)
                .alpha(indicatorAlpha)
        ) {
            // Background track
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .background(
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        RoundedCornerShape(2.dp)
                    )
            )
            
            // Scroll thumb
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.2f) // Thumb size relative to track
                    .width(4.dp)
                    .offset(y = (scrollProgress * (1f - 0.2f) * 100).dp) // Position based on scroll
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@Composable
fun ScrollIndicatorContainer(
    listState: LazyListState,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        content()
        
        ModernScrollIndicator(
            listState = listState,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .padding(vertical = 16.dp)
        )
    }
}