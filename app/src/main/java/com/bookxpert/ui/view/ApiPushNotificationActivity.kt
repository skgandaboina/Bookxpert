package com.bookxpert.ui.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bookxpert.R
import com.bookxpert.data.db.entities.Device
import com.bookxpert.ui.adapters.DeviceAdapter
import com.bookxpert.ui.viewmodel.DeviceViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.lifecycle.ViewModelProvider
import com.bookxpert.utils.PreferenceManager
import com.google.firebase.messaging.FirebaseMessaging

class ApiPushNotificationActivity : AppCompatActivity() {

    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DeviceAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyMessage: TextView
    private lateinit var notificationSwitch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_fcm)

        subscribeToTopic()

        deviceViewModel = ViewModelProvider(this)[DeviceViewModel::class.java]

        // Initialize UI elements
        notificationSwitch = findViewById(R.id.notificationSwitch)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        emptyMessage = findViewById(R.id.emptyMessage)

        notificationSwitch.isChecked = PreferenceManager.isNotificationEnabled(this)

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            PreferenceManager.setNotificationEnabled(this, isChecked)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        deviceViewModel.errorMessage.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        // Observe device data
        deviceViewModel.allDevices.observe(this) { devices ->
            if (devices.isEmpty()) {
                // Show loading if data is empty
                progressBar.visibility = View.GONE
                emptyMessage.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                // Hide loading and display data
                progressBar.visibility = View.GONE
                emptyMessage.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                adapter = DeviceAdapter(
                    devices,
                    onUpdateClick = { device -> showUpdateDialog(device) },
                    onDeleteClick = { device -> deleteDevice(device) }
                )
                recyclerView.adapter = adapter
            }
        }

        // Fetch devices from API initially or refresh if needed
        deviceViewModel.fetchDevices(forceRefresh = false)

        // Add refresh button functionality
        findViewById<View>(R.id.refreshButton).setOnClickListener {
            deviceViewModel.fetchDevices(forceRefresh = true) // Force refresh data from API
        }
    }

    // Apply the theme based on the switch state
    private fun applyTheme(isDarkMode: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    // Show a dialog to update device name
    private fun showUpdateDialog(device: Device) {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Update Device")

        val input = EditText(this)
        input.hint = "Enter new name"
        input.setText(device.name)

        builder.setView(input)

        builder.setPositiveButton("Update") { dialog, _ ->
            val updatedName = input.text.toString().trim()
            if (updatedName.isNotEmpty()) {
                device.name = updatedName
                deviceViewModel.updateDevice(device)
                Toast.makeText(this, "Device updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.show()
    }

    // Show a dialog to confirm device deletion
    private fun deleteDevice(device: Device) {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Delete Device")
        builder.setMessage("Are you sure you want to delete ${device.name}?")

        builder.setPositiveButton("Delete") { dialog, _ ->
            deviceViewModel.deleteDevice(device)
            // Send Local Notification if enabled
            /*if (PreferenceManager.isNotificationEnabled(this)) {
                NotificationHelper.showNotification(
                    context = this,
                    title = "Device Deleted",
                    message = "${device.name} has been deleted."
                )
            }*/
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.show()
    }

    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("updates")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Subscribed to updates topic")
                } else {
                    Log.e("FCM", "Failed to subscribe")
                }
            }
    }
}