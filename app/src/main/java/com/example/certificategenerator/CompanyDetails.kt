package com.example.certificategenerator

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.certificategenerator.daos.CompanyDao
import com.example.certificategenerator.models.Company
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_company_details.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File


class CompanyDetails : AppCompatActivity() {

    private val PICK_IMG = 100
    private val PICK_SIGN = 101
    private var imageUriLogo: Uri? = null
    private var imageUriSign: Uri? = null
    var logoDownloadUri: Uri? = null
    var signDownloadUri: Uri? = null


    var companyEmail:String =""
    var companyName:String =""

    private lateinit var auth: FirebaseAuth
//    private lateinit var db: FirebaseAuth
    private lateinit var user_id: String
    private var storageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_details)

//        Get intents from registerActivity and set them into edit text
        companyEmail = intent.getStringExtra("Email").toString()
        companyName = intent.getStringExtra("CompanyName").toString()
        companyNameEditText.setText(companyName)
        companyEmailEditText.setText(companyEmail)

//        Creating instance of auth
        auth = Firebase.auth
        if(auth.currentUser==null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }else {
            user_id = auth.currentUser!!.uid
        }

//        Creating instance of storage
        val storage = Firebase.storage
        storageRef = storage.reference

//        Creating instance of firestore
        val db = Firebase.firestore

//        Click event on the logo upload image view
        companyLogoUpload.setOnClickListener{
            // Create intent to Open Image applications like Gallery, Google Photos
            // Create intent to Open Image applications like Gallery, Google Photos
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            // Start the Intent
            // Start the Intent
            startActivityForResult(galleryIntent, PICK_IMG)
        }

//        Click event on the signature upload image view
        signatureUpload.setOnClickListener{
            // Create intent to Open Image applications like Gallery, Google Photos
            // Create intent to Open Image applications like Gallery, Google Photos
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            // Start the Intent
            // Start the Intent
            startActivityForResult(galleryIntent, PICK_SIGN)
        }



//      Complete button click event
        completeBtn.setOnClickListener{

//          Taking the name and email entered
            companyName = companyNameEditText.text.toString()
            companyEmail = companyEmailEditText.text.toString()

//          Converting both Logo and sign imageView to Bitmap
            val byteArrayOutputStreamLogo = ByteArrayOutputStream()
            val drawableLogo = companyLogoUpload.drawable as BitmapDrawable
            val bitmapLogo : Bitmap = drawableLogo.bitmap
            bitmapLogo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamLogo)
            val thumbLogo = byteArrayOutputStreamLogo.toByteArray()

            val byteArrayOutputStreamSign = ByteArrayOutputStream()
            val drawableSign = signatureUpload.drawable as BitmapDrawable
            val bitmapSign : Bitmap = drawableSign.bitmap
            bitmapSign.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamSign)
            val thumbSign = byteArrayOutputStreamSign.toByteArray()

//          Storing the image in storage and taking their download url
            val imagePathLogo = storageRef!!.child("company_logo").child(user_id+"logo.jpg").putBytes(thumbLogo)
            val imagePathSign = storageRef!!.child("company_sign").child(user_id+"sign.jpg").putBytes(thumbSign)

//          After completion of storing, get url of images and store the details in firestore
            val allTask = Tasks.whenAll(imagePathLogo,imagePathSign)
            allTask.addOnCompleteListener{
                Log.d("CompleteUpload", it.isComplete.toString())
                GlobalScope.launch(Dispatchers.IO) {
                    signDownloadUri =
                        imagePathSign.result.metadata!!.reference!!.downloadUrl.await()
                    logoDownloadUri =
                        imagePathLogo.result.metadata!!.reference!!.downloadUrl.await()
                    uploadData(companyName, companyEmail, logoDownloadUri, signDownloadUri)
                }
            }
        }
    }

//  Function for uploading details to the database.
    private fun uploadData(companyName: String, companyEmail: String, logoDownloadUri: Uri?, signDownloadUri: Uri?) {
        val companyDao = CompanyDao()
        val company = Company(user_id, companyName,companyEmail, logoDownloadUri, signDownloadUri)
        companyDao.addCompany(company)
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMG) {
            imageUriLogo = data?.data
            companyLogoUpload.setImageURI(imageUriLogo)
        }

        if (resultCode == RESULT_OK && requestCode == PICK_SIGN) {
            imageUriSign = data?.data
            signatureUpload.setImageURI(imageUriSign)
        }
    }


}