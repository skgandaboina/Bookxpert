package com.bookxpert.data.repository

import android.util.Log
import com.bookxpert.data.api.RetrofitClient
import com.bookxpert.data.db.dao.DeviceDao
import com.bookxpert.data.db.entities.Device

class DeviceRepository(private val deviceDao: DeviceDao) {

    suspend fun fetchDevicesFromApiAndSave() {
        try {
            val response = RetrofitClient.apiService.getDevices()
            if (response.isSuccessful) {
                response.body()?.let { deviceList ->
                    deviceDao.insertAll(deviceList)
                }
            } else {
                Log.e("DeviceRepository", "API error: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("DeviceRepository", "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun updateDevice(device: Device) {
        deviceDao.update(device)
    }

    suspend fun deleteDevice(device: Device) {
        deviceDao.delete(device)
    }
}
