package com.bookxpert.ui.view

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bookxpert.R
import java.io.File

class ImageSelectionActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var cameraImageUri: Uri

    private val permissionRequestCamera = 101
    private val permissionRequestGallery = 102

    // Capture full-size image
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            imageView.setImageURI(cameraImageUri)
        } else {
            Toast.makeText(this, "Camera cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    // Select image from gallery
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageView.setImageURI(uri)
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_selection)

        imageView = findViewById(R.id.imageView)
        val buttonCapture = findViewById<Button>(R.id.buttonCapture)
        val buttonGallery = findViewById<Button>(R.id.buttonGallery)

        buttonCapture.setOnClickListener {
            checkCameraPermission()
        }

        buttonGallery.setOnClickListener {
            checkGalleryPermission()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), permissionRequestCamera)
        } else {
            openCamera()
        }
    }

    private fun checkGalleryPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), permissionRequestGallery)
        } else {
            openGallery()
        }
    }

    private fun openCamera() {
        val photoFile = File.createTempFile("camera_image", ".jpg", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        cameraImageUri = FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",  // Make sure it matches your manifest
            photoFile
        )

        cameraLauncher.launch(cameraImageUri)
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            return
        }

        when (requestCode) {
            permissionRequestCamera -> openCamera()
            permissionRequestGallery -> openGallery()
        }
    }
}
