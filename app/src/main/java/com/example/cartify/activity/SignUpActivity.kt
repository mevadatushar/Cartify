package com.example.cartify.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.cartify.R
import com.example.cartify.Model.User
import com.example.cartify.Extensions.toast
import com.example.cartify.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : BaseActivity() {

    private lateinit var binding: ActivitySignUpBinding
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var progressDialog: ProgressDialog
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var sharedPreferences: SharedPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        progressDialog = ProgressDialog(this)
        sharedPreferences = getSharedPreferences("MYPREFS", Context.MODE_PRIVATE)

        textAutoCheck()

        binding.signInTvSignUpPage.setOnClickListener {
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.signUpBtnSignUpPage.setOnClickListener {
            checkInput()
        }
    }

    private fun textAutoCheck() {
        binding.nameEtSignUpPage.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (binding.nameEtSignUpPage.text.isEmpty()) {
                    binding.nameEtSignUpPage.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                } else if (binding.nameEtSignUpPage.text.length >= 4) {
                    binding.nameEtSignUpPage.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.ic_check
                        ), null
                    )
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                binding.nameEtSignUpPage.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (count >= 4) {
                    binding.nameEtSignUpPage.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.ic_check
                        ), null
                    )
                }
            }
        })

        binding.emailEtSignUpPage.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (binding.emailEtSignUpPage.text.isEmpty()) {
                    binding.emailEtSignUpPage.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                } else if (binding.emailEtSignUpPage.text.matches(emailPattern.toRegex())) {
                    binding.emailEtSignUpPage.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.ic_check
                        ), null
                    )
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                binding.emailEtSignUpPage.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.emailEtSignUpPage.text.matches(emailPattern.toRegex())) {
                    binding.emailEtSignUpPage.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.ic_check
                        ), null
                    )
                }
            }
        })

        binding.PassEtSignUpPage.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (binding.PassEtSignUpPage.text.isEmpty()) {
                    binding.PassEtSignUpPage.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                } else if (binding.PassEtSignUpPage.text.length > 5) {
                    binding.PassEtSignUpPage.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.ic_check
                        ), null
                    )
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                binding.PassEtSignUpPage.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (count > 5) {
                    binding.PassEtSignUpPage.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.ic_check
                        ), null
                    )
                }
            }
        })

        binding.cPassEtSignUpPage.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (binding.cPassEtSignUpPage.text.isEmpty()) {
                    binding.cPassEtSignUpPage.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                } else if (binding.cPassEtSignUpPage.text.toString() == binding.PassEtSignUpPage.text.toString()) {
                    binding.cPassEtSignUpPage.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.ic_check
                        ), null
                    )
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                binding.cPassEtSignUpPage.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.cPassEtSignUpPage.text.toString() == binding.PassEtSignUpPage.text.toString()) {
                    binding.cPassEtSignUpPage.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.ic_check
                        ), null
                    )
                }
            }
        })
    }

    private fun checkInput() {

        val fullname = binding.nameEtSignUpPage.text.toString().trim()
        val email = binding.emailEtSignUpPage.text.toString().trim()
        val password = binding.PassEtSignUpPage.text.toString().trim()
        val confirmPassword = binding.cPassEtSignUpPage.text.toString().trim()

        if (binding.nameEtSignUpPage.text.isEmpty()) {
            toast("Name can't be empty!")
            return
        }
        if (binding.emailEtSignUpPage.text.isEmpty()) {
            toast("Email can't be empty!")
            return
        }

        if (!binding.emailEtSignUpPage.text.matches(emailPattern.toRegex())) {
            toast("Enter a valid email")
            return
        }
        if (binding.PassEtSignUpPage.text.isEmpty()) {
            toast("Password can't be empty!")
            return
        }
        if (binding.PassEtSignUpPage.text.toString() != binding.cPassEtSignUpPage.text.toString()) {
            toast("Passwords do not match")
            return
        }

        signUpWithEmailPassword(fullname, email, password)

    }

    private fun signUpWithEmailPassword(fullname: String, email: String, password: String) {
        progressDialog.setTitle("Please Wait")

        progressDialog.setMessage("Creating Account")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = firebaseAuth.currentUser?.uid ?: ""

                    // Save user data to Firebase Realtime Database
                    val user = User(fullname,  userId, email)
                    firebaseDatabase.reference.child("users").child(email.replace(".", "_")).setValue(user)
                        .addOnCompleteListener { dbTask ->
                            progressDialog.dismiss()
                            if (dbTask.isSuccessful) {
                                // Save user data to SharedPreferences
                                saveUserDataLocally(fullname, email, password)
                                Log.d("TAG", "sharedPreferences: $fullname , $email , $password")
                                progressDialog.setMessage("Save User Data")
                      //          Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Failed to Save user data", Toast.LENGTH_SHORT).show()
                                Log.e("SignUpActivity", "Failed to save user data", dbTask.exception)
                            }
                        }
                } else {
                    progressDialog.dismiss()

                                       toast("Failed to authenticate!")

                   // Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    Log.e("SignUpActivity", "Sign up failed", task.exception)
                }
            }
    }

    private fun saveUserDataLocally(fullname: String, email: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLogin", true)
        editor.putString("username", fullname)
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

//    private fun signIn() {
//        progressDialog.setTitle("Please Wait")
//        progressDialog.setMessage("Creating Account")
//        progressDialog.show()
//
//        val emailV: String = binding.emailEtSignUpPage.text.toString()
//        val passV: String = binding.PassEtSignUpPage.text.toString()
//        val fullname: String = binding.nameEtSignUpPage.text.toString()
//
//        firebaseAuth.createUserWithEmailAndPassword(emailV, passV)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    progressDialog.setMessage("Save User Data")
//                    Log.d("SignUpActivity", "User created successfully")
//
//                    val user = User(fullname, "", firebaseAuth.uid.toString(), emailV, "", "")
//                } else {
//                    progressDialog.dismiss()
//                    Log.e("SignUpActivity", "Failed to create user", task.exception)
//                    toast("Failed to authenticate!")
//                }
//            }
//    }


}
