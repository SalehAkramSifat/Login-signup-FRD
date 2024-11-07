package com.sifat.loginsignupfrd

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sifat.loginsignupfrd.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var firebaseDatbase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = binding.progressBar
        firebaseDatbase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatbase.reference.child("users")

        binding.login.setOnClickListener {
            val user = binding.username.text.toString()
            val password = binding.password.text.toString()
            if (user.isNotEmpty() && password.isNotEmpty()) {
                loginUser(user, password)
            } else {
                Toast.makeText(this@LoginActivity, "all field are mabdatory", Toast.LENGTH_SHORT)
                    .show()

            }
        }

        binding.sign.setOnClickListener {
            val intent2 = Intent(this, SignupActivity::class.java)
            startActivity(intent2)
            finish()
        }

    }

    private fun loginUser(username: String, password: String) {
        progressBar.visibility = View.VISIBLE
        databaseReference.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    progressBar.visibility = View.GONE 
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val userData = userSnapshot.getValue(UserData::class.java)
                            if (userData != null) {
                                if (userData.password == password) {
                                    Toast.makeText(this@LoginActivity,"Login Successful",Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                                    finish()
                                    return 
                                } else {
                                    Toast.makeText(this@LoginActivity,"Incorrect password",Toast.LENGTH_SHORT).show()
                                    return
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaserror: DatabaseError) {
                    progressBar.visibility = View.GONE // Hide progress bar on error
                    Toast.makeText(
                        this@LoginActivity,
                        "Database Error: ${databaserror.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}