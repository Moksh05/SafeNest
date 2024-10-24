package com.example.safenest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.safenest.databinding.ActivityLoginSignupBinding
import com.example.safenest.models.UserModel

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginSignup : AppCompatActivity() {

    private var firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityLoginSignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

       binding.switchtologin.setOnClickListener {
           binding.flipper.showNext()
       }
        binding.switchtosignup.setOnClickListener {
            binding.flipper.showPrevious()
        }

        binding.loginbtn.setOnClickListener { signIn() }
        binding.signupbtn.setOnClickListener { SignUp() }
    }

    private fun signIn(){
        val email = binding.loginmail.text.toString().trim()
        val password = binding.loginpass.text.toString().trim()

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{task->
            if(task.isSuccessful){
                Toast.makeText(this,"User Verified ,Loging In",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@LoginSignup,MainActivity::class.java))
            }
        }.addOnFailureListener {
            Toast.makeText(this,"Failed to Login , Try Again",Toast.LENGTH_SHORT).show()
        }
    }
    private fun SignUp() {
        val phonenumber = binding.signupphonenumber.text.toString().trim()
        val email = binding.signupemail.text.toString().trim()
        val password = binding.signuppass.text.toString().trim()
        //val phoneNumber = binding.signupphone.text.toString().trim()

        if (!email.contains("@gmail.com")) {
            Toast.makeText(
                this,
                "Please use a valid email, provided by gmail.com",
                Toast.LENGTH_SHORT
            ).show()
            binding.signupemail.text = null
            return
        }

        if (password.length < 6) {
            Toast.makeText(
                this,
                "Enter a valid password, having at least 6 characters",
                Toast.LENGTH_SHORT
            ).show()
            binding.signuppass.text = null
            return
        }

        if (phonenumber.isEmpty() || phonenumber.length <10) {
            Toast.makeText(
                this,
                "Please enter a valid phone number",
                Toast.LENGTH_SHORT
            ).show()
            //binding.signupphone.text = null
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = firebaseAuth.currentUser?.email
                if (userId != null) {
                    // Create a user model with the entered details
                    val userModel = UserModel(
                        phonenumber = phonenumber,
                        email = email
                    )

                    // Save the user information in Firestore
                    val db = FirebaseFirestore.getInstance()
                    db.collection("users").document(userId).set(userModel)
                        .addOnSuccessListener {
                            binding.flipper.showPrevious()
                            Toast.makeText(
                                this,
                                "Account Created. Please login",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.flipper.displayedChild = 1
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Failed to save user data: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(
                this,
                "Signing Up failed. Please try again later \n${e.toString()}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    override fun onStart() {
        if(firebaseAuth.currentUser!=null){
            startActivity(Intent(this@LoginSignup,MainActivity::class.java))
            Toast.makeText(this,"${firebaseAuth.currentUser!!.email}" , Toast.LENGTH_SHORT).show()
        }
        super.onStart()
    }

}