package com.example.ai_image_client.navigation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab

private val tabs = listOf(GalleryTab, GenerationTab)

@Composable
fun AppSidebar(
    expanded: Boolean,
    onToggle: () -> Unit,
    onNavigate: () -> Unit = {},
) {
    val tabNavigator = LocalTabNavigator.current
    val sidebarWidth = if (expanded) 200.dp else 60.dp

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(sidebarWidth)
            .animateContentSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(vertical = 8.dp, horizontal = 4.dp),
    ) {
        // Toggle button
        IconButton(onClick = onToggle, modifier = Modifier.padding(horizontal = 4.dp)) {
            Icon(
                Icons.Default.Menu,
                contentDescription = "Toggle sidebar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(8.dp))

        tabs.forEach { tab ->
            SidebarItem(
                tab = tab,
                selected = tabNavigator.current.key == tab.key,
                expanded = expanded,
                onClick = {
                    onNavigate()
                    tabNavigator.current = tab
                },
            )
        }
    }
}

@Composable
private fun SidebarItem(tab: Tab, selected: Boolean, expanded: Boolean, onClick: () -> Unit) {
    val bgColor = if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .then(if (expanded) Modifier.width(168.dp) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Accent strip for selected
        if (selected) {
            Box(
                Modifier
                    .width(3.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(Modifier.width(8.dp))
        }

        tab.options.icon?.let { painter ->
            Icon(
                painter = painter,
                contentDescription = tab.options.title,
                tint = contentColor,
                modifier = Modifier.size(20.dp),
            )
        }

        if (expanded) {
            Spacer(Modifier.width(12.dp))
            Text(
                text = tab.options.title,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
            )
        }
    }
}
