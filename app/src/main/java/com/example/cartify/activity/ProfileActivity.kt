package com.example.cartify.activity

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cartify.LoadingDialog
import com.example.cartify.R
import com.example.cartify.databinding.ActivityProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class ProfileActivity : BaseActivity() {
    lateinit var binding: ActivityProfileBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 22
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Initialize Google Sign-In client
        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
        loadingDialog = LoadingDialog(this)
        firebaseDatabase = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        initView()
        updateUIWithUserData()
        setupProfilePictureSelection()

        // Set upload button visibility to GONE initially
        binding.btnUplodeImage.visibility = View.GONE

        // Set up listener for the upload button
        binding.btnUplodeImage.setOnClickListener {
            uploadImage()
        }
    }

    private fun initView() {
        binding.btnBack.setOnClickListener { finish() }


    }

    private fun updateUIWithUserData() {
        sharedPreferences = getSharedPreferences("MYPREFS", Context.MODE_PRIVATE)
        val profile = sharedPreferences.getString("profile", "")
        val username = sharedPreferences.getString("username", "")
        val email = sharedPreferences.getString("email", "")

        binding.txtUsername.text = username
        binding.txtUsernameEmail.text = email

        if (!profile.isNullOrEmpty()) {
            Glide.with(this).load(profile).into(binding.cvProfile)
        }
    }

    private fun setupProfilePictureSelection() {
        binding.cvProfile.setOnClickListener {
            selectImage()
        }
    }

    private fun selectImage() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(intent, "Select Image from here..."),
            PICK_IMAGE_REQUEST
        )
        Log.d(TAG, "Select Image clicked")
    }

    private fun uploadImage() {
        if (filePath != null) {
            loadingDialog.startLoadingDialog()
            val filename = binding.txtUsernameEmail.text.toString()
            val ref = storageReference.child("images/$filename")

            ref.putFile(filePath!!)
                .addOnSuccessListener { taskSnapshot ->
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        loadingDialog.dismissDialog()
                        val downloadUrl = uri.toString()
                        Toast.makeText(this, "Image Uploaded!!", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Image Uploaded: $downloadUrl")

                        // Save the image URL to SharedPreferences
                        saveImageUrlToSharedPreferences(downloadUrl)

                        // Update the profile image view with the new image
                        Glide.with(this).load(downloadUrl).into(binding.cvProfile)

                        // Hide the upload button
                        binding.btnUplodeImage.visibility = View.GONE
                    }
                }
                .addOnFailureListener { e ->
                    loadingDialog.dismissDialog()
                    Toast.makeText(this, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                }
        }
    }


    private fun saveImageUrlToSharedPreferences(imageUrl: String) {
        sharedPreferences = getSharedPreferences("MYPREFS", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("profile", imageUrl)
        editor.apply()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            Log.d(TAG, "File Path: $filePath")

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                binding.cvProfile.setImageBitmap(bitmap)

                // Show the upload button when an image is selected
                binding.btnUplodeImage.visibility = View.VISIBLE
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    // Logout user from Google and clear user data from SharedPreferences
    fun logout(view: View) {
        val message = "Are you sure you want to logout?"
        showConfirmationDialog(message) {
            // Code to execute on confirmation
            googleSignInClient.signOut().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.signOut()
                    Toast.makeText(applicationContext, "Logout successful", Toast.LENGTH_SHORT).show()
                    clearUserData()
                    // Start LoginActivity with flags to clear back stack
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }


    // Handle logout button click event
    private fun showConfirmationDialog(message: String, onConfirm: () -> Unit) {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.logout_item, null)
        builder.setView(dialogView)

        val txtMessage = dialogView.findViewById<TextView>(R.id.txtMessage)
        txtMessage.text = message

        val dialog = builder.create()
        dialog.show()

        val btnNo = dialog.findViewById<Button>(R.id.btnNo)
        val btnYes = dialog.findViewById<Button>(R.id.btnYes)

        btnNo!!.setOnClickListener {
            dialog.dismiss()
        }

        btnYes!!.setOnClickListener {
            dialog.dismiss()
            onConfirm.invoke()
        }
    }

    // Clear user data from SharedPreferences
    private fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }


}
