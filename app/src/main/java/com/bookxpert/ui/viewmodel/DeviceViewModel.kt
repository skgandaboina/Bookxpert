package com.bookxpert.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bookxpert.data.db.AppDatabase
import com.bookxpert.data.db.entities.Device
import com.bookxpert.data.repository.DeviceRepository
import com.bookxpert.utils.NetworkUtils
import com.bookxpert.utils.PreferenceManager
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class DeviceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DeviceRepository
    private val appContext = application.applicationContext
    val allDevices = AppDatabase.getDatabase(application).deviceDao().getAllDevices()
    val errorMessage = MutableLiveData<String>()

    init {
        val dao = AppDatabase.getDatabase(application).deviceDao()
        repository = DeviceRepository(dao)
    }

    // Function to fetch data from API if there's no data in DB or if user clicks refresh
    fun fetchDevices(forceRefresh: Boolean = false) {
        if (forceRefresh) {
            fetchDevicesFromApi()
        } else {
            allDevices.observeForever { devices ->
                if (devices.isNullOrEmpty()) {
                    fetchDevicesFromApi()
                }
            }
        }
    }

    private fun fetchDevicesFromApi() = viewModelScope.launch {
        if (!NetworkUtils.isNetworkAvailable(appContext)) {
            errorMessage.postValue("No Internet Connection")
            return@launch
        }
        repository.fetchDevicesFromApiAndSave()
    }

    fun updateDevice(device: Device) = viewModelScope.launch {
        repository.updateDevice(device)
    }

    fun deleteDevice(device: Device) = viewModelScope.launch {
        repository.deleteDevice(device)

        // Check if user enabled notifications
        if (PreferenceManager.isNotificationEnabled(appContext)) {
            sendDeleteNotification(device)
        }
    }

    private fun sendDeleteNotification(device: Device) {
        val notification = JSONObject()
        val notificationBody = JSONObject()

        notificationBody.put("title", "Device Deleted")
        notificationBody.put("body", "Deleted device: ${device.name}")

        notification.put("to", "/topics/updates") // If you subscribed to topics
        notification.put("notification", notificationBody)

        sendPushNotification(notification)
    }

    private fun sendPushNotification(notification: JSONObject) {
        val client = OkHttpClient()
        val mediaType = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, notification.toString())

        val request = Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .post(body)
            .addHeader("Authorization", "key=YOUR_SERVER_KEY")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FCM", "Failed to send notification: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("FCM", "Notification sent: ${response.body()?.string()}")
            }
        })
    }
}
