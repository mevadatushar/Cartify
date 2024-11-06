package com.example.cartify.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.cartify.Extensions.toast
import com.example.cartify.FirebaseUtils.firebaseAuth
import com.example.cartify.LoadingDialog
import com.example.cartify.Model.User
import com.example.cartify.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginActivity : BaseActivity() {

    private lateinit var signInEmail: String
    private lateinit var signInPassword: String
    private lateinit var signInBtn: Button
    private lateinit var emailEt: EditText
    private lateinit var passEt: EditText
    private lateinit var emailError: TextView
    private lateinit var passwordError: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("MYPREFS", Context.MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.reference.child("users")

        val signUpTv = findViewById<TextView>(R.id.signUpTv)
        signInBtn = findViewById(R.id.loginBtn)
        emailEt = findViewById(R.id.emailEt)
        passEt = findViewById(R.id.PassEt)
        emailError = findViewById(R.id.emailError)
        passwordError = findViewById(R.id.passwordError)
        loadingDialog = LoadingDialog(this)

        textAutoCheck()

        signUpTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        signInBtn.setOnClickListener {
            checkInput()
        }
    }

    private fun textAutoCheck() {
        emailEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (emailEt.text.isEmpty()) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                } else if (Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext, R.drawable.ic_check), null)
                    emailError.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext, R.drawable.ic_check), null)
                    emailError.visibility = View.GONE
                }
            }
        })

        passEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (passEt.text.isEmpty()) {
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                } else if (passEt.text.length > 4) {
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext, R.drawable.ic_check), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                passwordError.visibility = View.GONE
                if (count > 4) {
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext, R.drawable.ic_check), null)
                }
            }
        })
    }

    private fun checkInput() {
        if (emailEt.text.isEmpty()) {
            emailError.visibility = View.VISIBLE
            emailError.text = "Email Can't be Empty"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
            emailError.visibility = View.VISIBLE
            emailError.text = "Enter Valid Email"
            return
        }
        if (passEt.text.isEmpty()) {
            passwordError.visibility = View.VISIBLE
            passwordError.text = "Password Can't be Empty"
            return
        }

        if (passEt.text.isNotEmpty() && emailEt.text.isNotEmpty()) {
            emailError.visibility = View.GONE
            passwordError.visibility = View.GONE
            signInUser()
        }
    }

    private fun signInUser() {
        loadingDialog.startLoadingDialog()
        signInEmail = emailEt.text.toString().trim()
        signInPassword = passEt.text.toString().trim()

        firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPassword)
            .addOnCompleteListener { signIn ->
                if (signIn.isSuccessful) {
                    loadingDialog.dismissDialog()
                    saveUserData()
                } else {
                    toast("sign in failed")
                    loadingDialog.dismissDialog()
                }
            }
    }

    private fun saveUserData() {
        val currentUser = firebaseAuth.currentUser ?: return
        val userId = currentUser.uid
        Log.d("tag","userId: $userId")

        reference.child(emailEt.text.toString().replace(".", "_")).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("LoginActivity", "onDataChange triggered")

                if (snapshot.exists()) {
                    val userData = snapshot.getValue(User::class.java)
                    if (userData != null) {
                        user = userData
                        val sharedPreferencesEditor: SharedPreferences.Editor = sharedPreferences.edit()
                        sharedPreferencesEditor.putString("email", user.userEmail)
                        sharedPreferencesEditor.putString("username", user.userName)
                        sharedPreferencesEditor.putString("userUID", user.userUid)
                        sharedPreferencesEditor.apply()

                        Log.d("LoginActivity", "User data retrieved: $user")

                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        toast("Signed in successfully")
                        finish()
                    } else {
                        Log.e("LoginActivity", "User data is null")
                        toast("Failed to retrieve user data")
                    }
                } else {
                    Log.e("LoginActivity", "No data available at specified location")
                    toast("No user data found")
                }
                loadingDialog.dismissDialog()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("LoginActivity", "onCancelled: " + error.message)
                toast("Database error: " + error.message)
                loadingDialog.dismissDialog()
            }
        })
    }

    companion object {
        lateinit var user: User
    }
}
