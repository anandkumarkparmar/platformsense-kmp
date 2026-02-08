package io.platformsense.sample

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
import io.platformsense.core.PlatformSense
import io.platformsense.domain.NetworkType
import io.platformsense.domain.PowerState

@Composable
fun App() {
    val scrollState = rememberScrollState()
    val env by PlatformSense.environmentFlow.collectAsState(initial = PlatformSense.environment())
    val caps = PlatformSense.capabilities()

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
            "Environment & capability intelligence for KMP",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Use case 1: Power-aware (reduce animations in low power)
        UseCaseCard(
            title = "Power-aware UI",
            subtitle = "Reduce animations when device is in power saver mode",
            currentValue = env.powerState.name,
            suggestion = if (env.powerState == PowerState.LOW_POWER)
                "Consider disabling or reducing animations"
            else
                "Normal power — animations OK",
            isHighlight = env.powerState == PowerState.LOW_POWER,
        )

        // Use case 2: Network-aware (reduce quality on metered)
        UseCaseCard(
            title = "Network-adaptive content",
            subtitle = "Reduce image quality on metered network",
            currentValue = env.networkType.name,
            suggestion = if (env.networkType == NetworkType.METERED)
                "Use lower resolution or defer heavy downloads"
            else
                "Unmetered — full quality OK",
            isHighlight = env.networkType == NetworkType.METERED,
        )

        // Use case 3: Capability-based (biometric)
        UseCaseCard(
            title = "Capability-based feature",
            subtitle = "Show biometric login only if device supports it",
            currentValue = if (caps.biometric.isAvailable) "Available" else "Not available",
            suggestion = if (caps.biometric.isAvailable)
                "You can show biometric login"
            else
                "Hide or disable biometric option",
            isHighlight = caps.biometric.isAvailable,
        )

        // Use case 4: Reactive (live updates)
        UseCaseCard(
            title = "Reactive environment",
            subtitle = "UI updates automatically when environment changes",
            currentValue = "Live: ${env.networkType}, ${env.powerState}, ${env.deviceClass}",
            suggestion = "This card reflects current snapshot from environmentFlow",
            isHighlight = false,
        )

        // Extra: device & locale
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Device & locale", style = MaterialTheme.typography.titleSmall)
                Text("Device: ${env.deviceClass} • Locale: ${env.locale.ifEmpty { "—" }} • TZ: ${env.timezone.ifEmpty { "—" }}")
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
            containerColor = if (isHighlight)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant,
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
