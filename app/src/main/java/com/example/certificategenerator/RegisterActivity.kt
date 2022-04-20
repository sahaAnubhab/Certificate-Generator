package com.example.certificategenerator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    var companyName: String =""
    var email: String =""
    var password: String =""
    var rePassword: String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = Firebase.auth

        alreadyRegisteredClick.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener{
            companyName = companyNameText.text.toString()
            email = emailRegister.text.toString()
            password = passwordRegister.text.toString()
            rePassword = passwordReRegister.text.toString()

            if(companyName.isEmpty() || email.isEmpty() || password.isEmpty() || rePassword.isEmpty()){
                Toast.makeText(this, "Company name cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(password != rePassword){
                Toast.makeText(this, "The password does not match with Re- Entered password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            createAccount(email, password)
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
                reload();
        }
    }

    private fun reload() {
//        val intent = Intent(this, HomeActivity::class.java)
    }

    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
        // [END create_user_with_email]
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user!=null){
            val intent = Intent(this, CompanyDetails::class.java)
            intent.putExtra("Company Name", companyName)
            intent.putExtra("Email", email)
            startActivity(intent)
        }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}