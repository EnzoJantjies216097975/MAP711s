package com.map711s.namibiahockey.ui.components.buttons

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.map711s.namibiahockey.util.DeepLinkHandler

@Composable
fun ShareButton(
    deepLinkHandler: DeepLinkHandler,
    type: String,
    id: String,
    title: String,
    description: String = ""
) {
    val context = LocalContext.current

    IconButton(
        onClick = {
            shareContent(
                context = context,
                deepLinkHandler = deepLinkHandler,
                type = type,
                id = id,
                title = title,
                description = description
            )
        }
    ) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share"
        )
    }
}

private fun shareContent(
    context: Context,
    deepLinkHandler: DeepLinkHandler,
    type: String,
    id: String,
    title: String,
    description: String
) {
    val deepLinkUrl = deepLinkHandler.generateDeepLinkUrl(type, id)

    val shareText = buildString {
        append(title)
        if (description.isNotEmpty()) {
            append("\n\n")
            append(description)
        }
        append("\n\n")
        append("Check it out: ")
        append(deepLinkUrl)
    }

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Share via")

    try {
        context.startActivity(shareIntent)
    } catch (e:Exception) {
        Toast.makeText(context, "No app available to share content.", Toast.LENGTH_SHORT).show()
    }
}