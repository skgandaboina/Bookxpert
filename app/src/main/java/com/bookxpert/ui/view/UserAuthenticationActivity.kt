package com.bookxpert.ui.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bookxpert.R
import com.bookxpert.data.db.entities.User
import com.bookxpert.ui.viewmodel.UserViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class UserAuthenticationActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: UserViewModel
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_authentication)

        viewModel = ViewModelProvider(this)[UserViewModel::class.java]
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Register the Activity Result Launcher
        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                val account = task.result
                firebaseAuthWithGoogle(account.idToken!!)
            } else {
                Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.signInButton).setOnClickListener { view ->
            val button = view as Button
            when (button.text) {
                getString(R.string.sign_in_with_google) -> signIn()
                getString(R.string.sign_out) -> signOut()
            }
        }

        viewModel.getUser().observe(this) { user ->
            user?.let {
                findViewById<TextView>(R.id.userInfo).text = "${user.name}\n${user.email}"
                Glide.with(this).load(user.photoUrl).into(findViewById(R.id.profileImage))
                findViewById<Button>(R.id.signInButton).text = getString(R.string.sign_out)
            }
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun signOut() {
        googleSignInClient.revokeAccess().addOnCompleteListener {
            FirebaseAuth.getInstance().signOut()
            viewModel.getUser().observe(this) { user ->
                user?.let {
                    viewModel.deleteUser(it)
                }
            }
            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
            findViewById<Button>(R.id.signInButton).text = getString(R.string.sign_in_with_google)
            findViewById<TextView>(R.id.userInfo).text = ""
            findViewById<ImageView>(R.id.profileImage).apply {
                Glide.with(this@UserAuthenticationActivity).clear(this)
                setImageDrawable(null)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val newUser = User(
                            uid = it.uid,
                            name = it.displayName ?: "N/A",
                            email = it.email ?: "N/A",
                            photoUrl = it.photoUrl?.toString() ?: ""
                        )
                        viewModel.insertUser(newUser)
                    }
                } else {
                    Toast.makeText(this, "Firebase Auth failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
