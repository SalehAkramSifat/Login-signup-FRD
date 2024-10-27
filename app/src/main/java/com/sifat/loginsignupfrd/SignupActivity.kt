package com.sifat.loginsignupfrd

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sifat.loginsignupfrd.databinding.ActivitySignupBinding
import java.security.MessageDigest

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        binding.signup.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (isEmailValid(email)) {
                    signupUser(email, hashPassword(password))
                } else {
                    Toast.makeText(this@SignupActivity, "Invalid email format", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@SignupActivity, "All fields are mandatory", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signupUser(email: String, hashedPassword: String) {
        // Show progress bar
        binding.progressBar.visibility = View.VISIBLE

        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Hide progress bar
                binding.progressBar.visibility = View.GONE

                if (!dataSnapshot.exists()) {
                    val id = databaseReference.push().key
                    val userData = UserData(id, email, hashedPassword)
                    databaseReference.child(id!!).setValue(userData)
                    Toast.makeText(this@SignupActivity, "Signup successful", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@SignupActivity, "Email already exists", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Hide progress bar on error
                binding.progressBar.visibility = View.GONE

                Toast.makeText(this@SignupActivity, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun hashPassword(password: String): String {
        return MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
            .joinToString("") { String.format("%02x", it) }
    }
}
