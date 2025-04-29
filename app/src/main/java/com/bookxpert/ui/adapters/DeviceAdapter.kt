package com.bookxpert.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bookxpert.R
import com.bookxpert.data.db.entities.Device

class DeviceAdapter(
    private val devices: List<Device>,
    private val onUpdateClick: (Device) -> Unit,
    private val onDeleteClick: (Device) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.deviceName)
        val details: TextView = itemView.findViewById(R.id.deviceDetails)
        val btnUpdate: Button = itemView.findViewById(R.id.btnUpdate)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]
        holder.name.text = device.name
        holder.details.text = device.data?.let { data ->
            listOfNotNull(
                data.color?.let { "Color: $it" },
                data.capacity?.let { "Capacity: $it" },
                data.price?.let { "Price: $it" },
                data.year?.let { "Year: $it" },
                data.generation?.let { "Generation: $it" },
                data.cpuModel?.let { "CPU Model: $it" },
                data.strapColour?.let { "Strap Colour: $it" },
                data.caseSize?.let { "Case Size: $it" },
                data.description?.let { "Description: $it" },
                data.screenSize?.let { "Screen Size: $it" }
            ).joinToString("\n")
        } ?: "No Details"

        holder.btnUpdate.setOnClickListener {
            onUpdateClick(device)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(device)
        }
    }

    override fun getItemCount(): Int = devices.size
}
