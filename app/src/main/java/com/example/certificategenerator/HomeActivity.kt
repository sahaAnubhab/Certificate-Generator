package com.example.certificategenerator

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.certificategenerator.daos.CompanyDao
import com.example.certificategenerator.models.Company
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        auth = Firebase.auth
        button.setOnClickListener{
            auth.signOut()
        }
        val userId = auth.uid
        var uri: Uri? = null

        GlobalScope.launch(Dispatchers.Main){
            val companyDao = CompanyDao()
            companyDao.getCompany(userId!!).addOnCompleteListener{
                if(it.isSuccessful){
                    if(it.result.exists()){
                        val image = it.result.getString("companyLogo")
                        Log.d("ImageString", image!!)
                        val companyName = it.result.getString("companyName")
                        Glide.with(imageView.context).load(image).centerCrop().into(imageView)
                        textView.text = companyName
                    }
                }else{
                    Log.d("ImageFail", it.exception.toString())
                }
            }.addOnFailureListener{
                Log.d("ImageFail", it.message.toString())
            }


        }



    }
    private fun a(companyName: String) {
        textView.text = companyName
    }
}