package com.example.androidslices

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.net.wifi.WifiManager
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.*

class MySliceProvider : SliceProvider() {

    override fun onCreateSliceProvider(): Boolean {
        return true
    }

    override fun onMapIntentToUri(intent: Intent?): Uri {
        var uriBuilder: Uri.Builder = Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
        if (intent == null) return uriBuilder.build()

        val data = intent.data
        if (data != null && data.path != null) {
            uriBuilder = uriBuilder.path(data.path)
        }

        val context = context
        if (context != null) {
            uriBuilder = uriBuilder.authority(context.packageName)
        }

        return uriBuilder.build()
    }

    override fun onBindSlice(sliceUri: Uri): Slice? {
        val context = context ?: return null
        return when {
            sliceUri.path == "/hello-world" -> createHelloWorldSlice(context, sliceUri)
            sliceUri.path == "/complex" -> createComplexSlice(context, sliceUri)
            sliceUri.path == "/interactive" -> createInteractiveSlice(context, sliceUri)

            else -> null
        }
    }

    private fun createHelloWorldSlice(context: Context, sliceUri: Uri): Slice {
        return list(context, sliceUri, ListBuilder.INFINITY) {
            row {
                title = "Hello world!"
                primaryAction = createActivityAction()
            }
        }
    }

    private fun createComplexSlice(context: Context, sliceUri: Uri): Slice {
        return list(context, sliceUri, ListBuilder.INFINITY) {
            row {
                title = "Warsaw"
                subtitle = "19°C, Sunny"
                primaryAction = createActivityAction()

                setTitleItem(
                        IconCompat.createWithResource(context, R.drawable.ic_wb_sunny_black_24dp).apply {
                            setTint(Color.parseColor("#F8F523"))
                        }, ListBuilder.SMALL_IMAGE
                )
            }

            gridRow {
                cell {
                    addImage(IconCompat.createWithResource(context, R.drawable.ic_wb_cloudy_black_24dp), ListBuilder.SMALL_IMAGE)
                    addTitleText("Monday")
                    addText("15°C, Cloudy")
                }

                cell {
                    addImage(IconCompat.createWithResource(context, R.drawable.ic_wb_sunny_black_24dp), ListBuilder.SMALL_IMAGE)
                    addTitleText("Tuesday")
                    addText("16°C, Sunny")
                }

                cell {
                    addImage(IconCompat.createWithResource(context, R.drawable.ic_wb_cloudy_black_24dp), ListBuilder.SMALL_IMAGE)
                    addTitleText("Wednesday")
                    addText("18°C, Cloudy")
                }
            }

            setSeeMoreAction(PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0))
        }
    }

    private fun createInteractiveSlice(context: Context, sliceUri: Uri): Slice {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val isWifiEnabled = wifiManager.isWifiEnabled
        val subtitle = if (isWifiEnabled) "Enabled" else "Disabled"

        return list(context, sliceUri, ListBuilder.INFINITY) {
            row {
                title = "Make me a Toast!"
                primaryAction = createToastAction()
            }

            row {
                title = "Wi-Fi"
                this.subtitle = subtitle
                primaryAction = createWiFiToggleAction(isWifiEnabled)
            }
        }
    }

    private fun createToastAction(): SliceAction {
        val intent = Intent(context, MyBroadcastReceiver::class.java).apply {
            action = MyBroadcastReceiver.TOAST_ACTION
        }

        return SliceAction.create(
                PendingIntent.getBroadcast(context, 0, intent, 0),
                IconCompat.createWithResource(context, R.drawable.ic_wb_sunny_black_24dp),
                ListBuilder.SMALL_IMAGE,
                "Make Toast"
        )
    }

    private fun createWiFiToggleAction(isBluetoothEnabled: Boolean): SliceAction {
        val intent = Intent(context, MyBroadcastReceiver::class.java).apply {
            action = MyBroadcastReceiver.WIFI_TOGGLE_ACTION
        }

        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        return SliceAction.createToggle(pendingIntent, "Wi-Fi Action", isBluetoothEnabled)
    }

    private fun createActivityAction(): SliceAction? {
        return SliceAction.create(
                PendingIntent.getActivity(
                        context, 0, Intent(context, MainActivity::class.java), 0
                ),
                IconCompat.createWithResource(context, R.drawable.ic_launcher_foreground),
                ListBuilder.ICON_IMAGE,
                "Open App"
        )
    }

    override fun onSlicePinned(sliceUri: Uri?) {
    }

    override fun onSliceUnpinned(sliceUri: Uri?) {
    }

    companion object {
        val INTERACTIVE_SLICE_URI = Uri.parse("content://com.example.androidslices/interactive")
    }
}
