package com.sifat.loginsignupfrd

import android.content.Intent
import android.os.Bundle
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
import com.sifat.loginsignupfrd.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var firebaseDatbase : FirebaseDatabase
    lateinit var databaseReference : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatbase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatbase.reference.child("users")

        binding.login.setOnClickListener {
            val user = binding.username.text.toString()
            val password = binding.password.text.toString()
            if (user.isNotEmpty()&&password.isNotEmpty()){
                loginUser(user,password)
            }
            else{
                Toast.makeText(this@LoginActivity, "all field are mabdatory", Toast.LENGTH_SHORT).show()

            }
        }

        binding.sign.setOnClickListener {
            val intent2 = Intent(this, SignupActivity::class.java)
            startActivity(intent2)
            finish()
        }

    }
    private fun loginUser(username:String, password:String){
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    for (userSnapshot in dataSnapshot.children){
                        val userData = userSnapshot.getValue(UserData::class.java)
                        if (userData != null && userData.password == password){
                            Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    }
                }
                else{
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaserror: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Database Error ${databaserror.message}", Toast.LENGTH_SHORT).show()

            }
        })
    }
}