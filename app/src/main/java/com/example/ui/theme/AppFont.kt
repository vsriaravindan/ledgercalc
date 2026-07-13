package com.example.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.R

enum class AppFont(
    val displayName: String,
    val fontFamily: FontFamily,
    val icon: ImageVector = Icons.Default.FontDownload,
) {
    DEFAULT("Default", FontFamily.Default),
    INTER("Inter", FontFamily(Font(R.font.inter, FontWeight.Normal))),
    JETBRAINS("JetBrains Mono", FontFamily(Font(R.font.jetbrains_mono, FontWeight.Normal))),
    VT323("VT323 (Terminal)", FontFamily(Font(R.font.vt323, FontWeight.Normal))),
    SERIF("Serif", FontFamily.Serif),
    MONOSPACE("Monospace", FontFamily.Monospace),
    SANS_SERIF("Sans-Serif", FontFamily.SansSerif),
}
