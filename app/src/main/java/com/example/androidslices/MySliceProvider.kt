package com.example.androidslices

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Handler
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.*
import java.util.concurrent.Executors

class MySliceProvider : SliceProvider() {

    private var currentValue = -1

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
            sliceUri.path == "/dynamic" -> createDynamicSlice(context, sliceUri)

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
        val wifiSubtitle = if (isWifiEnabled) "Enabled" else "Disabled"

        return list(context, sliceUri, ListBuilder.INFINITY) {
            row {
                title = "Make me a Toast!"
                primaryAction = createToastAction()
            }

            row {
                title = "Wi-Fi"
                subtitle = wifiSubtitle
                primaryAction = createWiFiToggleAction(isWifiEnabled)
            }
        }
    }

    private fun createDynamicSlice(context: Context, sliceUri: Uri): Slice {
        fetchCurrentValueAsync()

        return if (currentValue >= 0) {
            list(context, sliceUri, ListBuilder.INFINITY) {
                inputRange {
                    title = "Change value"
                    value = currentValue
                    primaryAction = createActivityAction()
                    inputAction = createInputPendingIntent()
                }
            }
        } else {
            list(context, sliceUri, ListBuilder.INFINITY) {
                row {
                    title = "Loading"
                    primaryAction = createActivityAction()
                }
            }
        }
    }

    private fun fetchCurrentValueAsync() {
        Executors.newSingleThreadExecutor().execute {
            val context = context ?: return@execute

            val value = SharedPreferencesUtil.getValue(context)
            if (value != currentValue) {
                currentValue = value
                context.contentResolver.notifyChange(DYNAMIC_SLICE_URI, null)
            }
        }
    }

    private fun createInputPendingIntent(): PendingIntent {
        val intent = Intent(context, MyBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(context, 0, intent, 0)
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

    private fun createWiFiToggleAction(isWiFiEnabled: Boolean): SliceAction {
        val intent = Intent(context, MyBroadcastReceiver::class.java).apply {
            action = MyBroadcastReceiver.WIFI_TOGGLE_ACTION
        }

        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        return SliceAction.createToggle(pendingIntent, "Wi-Fi Action", isWiFiEnabled)
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
        val context = context ?: return

        if (sliceUri == DYNAMIC_SLICE_URI) {
            currentValue = SharedPreferencesUtil.getValue(context)
            context.contentResolver.notifyChange(DYNAMIC_SLICE_URI, null)
        }
    }

    override fun onSliceUnpinned(sliceUri: Uri?) {
    }

    companion object {
        val INTERACTIVE_SLICE_URI = Uri.parse("content://com.example.androidslices/interactive")
        val DYNAMIC_SLICE_URI = Uri.parse("content://com.example.androidslices/dynamic")
    }
}
