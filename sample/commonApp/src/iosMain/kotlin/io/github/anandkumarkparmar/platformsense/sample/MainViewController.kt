package io.github.anandkumarkparmar.platformsense.sample

import androidx.compose.ui.window.ComposeUIViewController

/**
 * Entry point for the iOS sample app.
 * Called from [ContentView.swift] via [ComposeView].
 */
@Suppress("ktlint:standard:function-naming") // Factory function for UIViewController — uppercase is the iOS convention
fun MainViewController() = ComposeUIViewController { App() }
