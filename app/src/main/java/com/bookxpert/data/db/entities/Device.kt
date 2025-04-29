package com.bookxpert.data.db.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class Device(
    @PrimaryKey val id: String,
    var name: String,
    @Embedded val data: DeviceData? = null
)
