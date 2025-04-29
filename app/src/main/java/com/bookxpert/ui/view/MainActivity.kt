package com.bookxpert.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.bookxpert.databinding.ActivityMainBinding
import com.bookxpert.utils.PreferenceManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set onClickListeners dynamically for all buttons
        with(binding) {
            // Load previous theme setting
            val isDarkMode = PreferenceManager.isDarkModeEnabled(binding.root.context)
            applyTheme(isDarkMode)
            themeSwitch.isChecked = isDarkMode

            buttonAuthenticate.setOnClickListener { navigateTo(UserAuthenticationActivity::class.java) }
            buttonImage.setOnClickListener { navigateTo(ImageSelectionActivity::class.java) }
            buttonPDF.setOnClickListener { navigateTo(PdfViewerActivity::class.java) }
            buttonAPIPush.setOnClickListener { navigateTo(ApiPushNotificationActivity::class.java) }
            themeSwitch.setOnCheckedChangeListener { _, isChecked ->
                PreferenceManager.setDarkModeEnabled(binding.root.context,isChecked)
                applyTheme(isChecked)
            }
        }
    }

    // Helper function to handle navigation
    private fun navigateTo(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass))
    }

    // Apply the theme based on the switch state
    private fun applyTheme(isDarkMode: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
