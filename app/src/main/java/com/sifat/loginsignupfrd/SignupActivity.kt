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
import com.sifat.loginsignupfrd.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    lateinit var firebaseDatbase : FirebaseDatabase
    lateinit var databaseReference : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseDatbase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatbase.reference.child("users")

        binding.signup.setOnClickListener {
            val user = binding.username.text.toString()
            val password = binding.password.text.toString()

            if (user.isNotEmpty()&& password.isNotEmpty()){
                signupUser(user, password)
            }
            else{
                Toast.makeText(this@SignupActivity, "all field are mabdatory", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun signupUser(username: String, password:String){
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
                if (!datasnapshot.exists()){
                    val id = databaseReference.push().key
                    val userData = UserData(id, username, password)
                    databaseReference.child(id!!).setValue(userData)
                    Toast.makeText(this@SignupActivity, "Signup succesfull", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this@SignupActivity, "Already Exists", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@SignupActivity, "Dtabase Error:${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}