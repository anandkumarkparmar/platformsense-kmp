package io.github.anandkumarkparmar.platformsense.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.anandkumarkparmar.platformsense.core.PlatformSense
import io.github.anandkumarkparmar.platformsense.core.models.device.BiometricStatus
import io.github.anandkumarkparmar.platformsense.core.models.state.PowerState

@Composable
fun App() {
    val scrollState = rememberScrollState()

    val networkInfo by PlatformSense.network.flow().collectAsState(initial = PlatformSense.network.current())
    val powerInfo by PlatformSense.power.flow().collectAsState(initial = PlatformSense.power.current())
    val deviceInfo = PlatformSense.device.current()
    val localeInfo by PlatformSense.locale.flow().collectAsState(initial = PlatformSense.locale.current())
    val timezoneInfo by PlatformSense.timezone.flow().collectAsState(initial = PlatformSense.timezone.current())
    val biometricInfo = PlatformSense.biometric.current()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            "PlatformSense",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            "Platform sensing for KMP",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Use case 1: Power-aware (reduce animations in low power)
        UseCaseCard(
            title = "Power-aware UI",
            subtitle = "Reduce animations when device is in power saver mode",
            currentValue = "Status: ${powerInfo.status.name} (Battery: ${powerInfo.batteryLevel?.let {
                "${(it * 100).toInt()}%"
            } ?: "N/A"})",
            suggestion = if (powerInfo.status == PowerState.LOW_POWER ||
                powerInfo.batteryLevel ?: 1.0f < 0.2f
            ) {
                "Consider disabling or reducing animations"
            } else {
                "Normal power — animations OK"
            },
            isHighlight = powerInfo.status == PowerState.LOW_POWER || powerInfo.batteryLevel ?: 1.0f < 0.2f,
        )

        // Use case 2: Network-aware (reduce quality on metered)
        UseCaseCard(
            title = "Network-adaptive content",
            subtitle = "Reduce image quality on metered network",
            currentValue = "Type: ${networkInfo.type.name} (Metered: ${networkInfo.isMetered})",
            suggestion = if (networkInfo.isMetered) {
                "Use lower resolution or defer heavy downloads"
            } else if (!networkInfo.isConnected) {
                "Show offline UI"
            } else {
                "Unmetered & Connected — full quality OK"
            },
            isHighlight = networkInfo.isMetered || !networkInfo.isConnected,
        )

        // Use case 3: Capability-based (biometric)
        UseCaseCard(
            title = "Capability-based feature",
            subtitle = "Show biometric login only if device supports it",
            currentValue = "Status: ${biometricInfo.status.name} (${biometricInfo.type.name})",
            suggestion = if (biometricInfo.status == BiometricStatus.READY) {
                "You can show biometric login"
            } else {
                "Hide or disable biometric option"
            },
            isHighlight = biometricInfo.status == BiometricStatus.READY,
        )

        // Use case 4: Reactive (live updates)
        UseCaseCard(
            title = "Reactive providers",
            subtitle = "UI updates automatically when providers emit new values",
            currentValue = "Live: net=${networkInfo.type}, " +
                "pwr=${powerInfo.status}, " +
                "dev=${deviceInfo.deviceClass}",
            suggestion = "This card reflects current values from provider flows",
            isHighlight = false,
        )

        // Extra: device & locale
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Device & locale", style = MaterialTheme.typography.titleSmall)
                Text(
                    "Device: ${deviceInfo.deviceClass} " +
                        "(${deviceInfo.manufacturer} ${deviceInfo.model}) • " +
                        "OS: ${deviceInfo.osName} ${deviceInfo.osVersion} • " +
                        "Language: ${localeInfo.language} • " +
                        "Country: ${localeInfo.country} • " +
                        "24hr: ${if (localeInfo.is24HourFormat) "Yes" else "No"} • " +
                        "TZ: ${timezoneInfo.id.ifEmpty { "—" }} " +
                        "(${timezoneInfo.displayName})"
                )
            }
        }
    }
}

@Composable
private fun UseCaseCard(
    title: String,
    subtitle: String,
    currentValue: String,
    suggestion: String,
    isHighlight: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlight) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Current: $currentValue",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                suggestion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
