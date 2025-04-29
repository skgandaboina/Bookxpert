package com.bookxpert.ui.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bookxpert.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Optional: set splash theme
        setTheme(R.style.Theme_DeviceApp_Splash)

        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(2000) // 2 second splash
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}
