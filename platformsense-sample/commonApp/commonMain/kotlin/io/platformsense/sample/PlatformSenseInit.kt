package io.platformsense.sample

/**
 * Platform-specific initialization of PlatformSense.
 * Android: real [io.platformsense.platform.android.AndroidPlatformSenseWiring].
 * Desktop / iOS: fake wiring for demo (no platform providers on JVM/iOS in this sample).
 */
expect fun initPlatformSense()
