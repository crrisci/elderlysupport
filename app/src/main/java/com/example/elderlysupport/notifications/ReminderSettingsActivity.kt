package com.example.elderlysupport.notifications

import com.example.elderlysupport.R

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.elderlysupport.notifications.MedicineNotifications.Availability
import com.example.elderlysupport.notifications.MedicineNotifications.SendResult
import com.example.elderlysupport.ui.theme.ElderlySupportTheme

class ReminderSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ElderlySupportTheme { ReminderSettings() } }
    }

    @Composable
    private fun ReminderSettings() {
        val notifications = remember { MedicineNotifications(applicationContext) }
        var availability by remember { mutableStateOf(notifications.availability()) }
        var resultMessage by rememberSaveable { mutableStateOf(R.string.result_initial) }

        DisposableEffect(lifecycle) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) availability = notifications.availability()
            }
            lifecycle.addObserver(observer)
            onDispose { lifecycle.removeObserver(observer) }
        }

        fun sendDemo() {
            val onTap = PendingIntent.getActivity(
                this@ReminderSettingsActivity, 0,
                Intent(this@ReminderSettingsActivity, ReminderSettingsActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            resultMessage = when (notifications.showReminder(
                DEMO_NOTIFICATION_ID, getString(R.string.demo_title), getString(R.string.demo_message), onTap,
            )) {
                SendResult.SENT -> R.string.result_sent
                SendResult.PERMISSION_REQUIRED -> R.string.result_denied
                SendResult.APP_DISABLED -> R.string.status_app_disabled
                SendResult.CHANNEL_DISABLED -> R.string.status_channel_disabled
            }
            availability = notifications.availability()
        }

        val permissionRequest = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) sendDemo() else {
                resultMessage = R.string.result_denied
                availability = notifications.availability()
            }
        }

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding).fillMaxSize()
                    .verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(stringResource(R.string.demo_heading), style = MaterialTheme.typography.headlineMedium)
                Text(stringResource(R.string.demo_description))
                Text(stringResource(when (availability) {
                    Availability.AVAILABLE -> R.string.status_ready
                    Availability.PERMISSION_REQUIRED -> R.string.status_permission
                    Availability.APP_DISABLED -> R.string.status_app_disabled
                    Availability.CHANNEL_DISABLED -> R.string.status_channel_disabled
                }))
                Button(onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        notifications.availability() == Availability.PERMISSION_REQUIRED
                    ) permissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                    else sendDemo()
                }) { Text(stringResource(R.string.send_notification)) }
                OutlinedButton(onClick = {
                    notifications.cancel(DEMO_NOTIFICATION_ID)
                    resultMessage = R.string.result_cancelled
                }) { Text(stringResource(R.string.cancel_notification)) }
                TextButton(onClick = {
                    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    } else {
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(android.net.Uri.parse("package:$packageName"))
                    }
                    startActivity(intent)
                }) { Text(stringResource(R.string.notification_settings)) }
                Text(stringResource(resultMessage),
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
            }
        }
    }

    private companion object { const val DEMO_NOTIFICATION_ID = 1001 }
}
