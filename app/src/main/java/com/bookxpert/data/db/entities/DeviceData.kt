package com.bookxpert.data.db.entities

import com.google.gson.annotations.SerializedName

data class DeviceData(
    @SerializedName(value = "color", alternate = ["Color"])
    val color: String? = null,
    @SerializedName(value = "capacity", alternate = ["capacity GB", "Hard disk size", "Capacity"])
    val capacity: String? = null,
    @SerializedName(value = "price", alternate = ["Price"])
    val price: Double? = null,
    val year: Int? = null,
    @SerializedName(value = "generation", alternate = ["Generation"])
    val generation: String? = null,
    @SerializedName("CPU model")
    val cpuModel: String? = null,
    @SerializedName("Strap Colour")
    val strapColour: String? = null,
    @SerializedName("Case Size")
    val caseSize: String? = null,
    @SerializedName("Description")
    val description: String? = null,
    @SerializedName("Screen size")
    val screenSize: Double? = null
)
