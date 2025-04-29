package com.bookxpert.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bookxpert.data.db.entities.Device

@Dao
interface DeviceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(devices: List<Device>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: Device)

    @Update
    suspend fun update(device: Device)

    @Delete
    suspend fun delete(device: Device)

    @Query("SELECT * FROM devices")
    fun getAllDevices(): LiveData<List<Device>>
}
