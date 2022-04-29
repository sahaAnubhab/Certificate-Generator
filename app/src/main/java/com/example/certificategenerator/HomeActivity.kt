package com.example.certificategenerator

import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.certificategenerator.daos.CompanyDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    // declaring width and height
    // for our PDF file.
    var pageHeight = 1120
    var pagewidth = 792

    private val PERMISSION_REQUEST_CODE = 200
    val path = File(Environment.getExternalStorageDirectory().absolutePath+"/Certificate")
    companion object{
        private lateinit var fileName: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
//      Checking if directory exist already
        if(!path.exists()){
            path.mkdir()
        }

        auth = Firebase.auth
        button.setOnClickListener{
            auth.signOut()
        }
        val userId = auth.uid
        var uri: Uri? = null

        GlobalScope.launch(Dispatchers.Main){
            val companyDao = CompanyDao()
            companyDao.getCompany(userId!!).addOnCompleteListener{
                Log.d("ResultExist", it.result.exists().toString()+ userId.toString())
                if(it.isSuccessful){
                    if(it.result.exists()){
                        Log.d("Completion", it.result.toString())
                        val imageLogo = it.result.getString("companyLogo")
                        Log.d("ImageString", imageLogo!!)
                        val companyName = it.result.getString("companyName")
                        Glide.with(homeCompanyLogo.context).load(imageLogo).centerCrop().into(homeCompanyLogo)

                        val imageSign = it.result.getString("signature")
                        Log.d("ImageString", imageSign!!)
                        Glide.with(homeSign.context).load(imageSign).centerCrop().into(homeSign)
                    }
                }else{
                    Log.d("ImageFail1", it.exception.toString())
                }
            }.addOnFailureListener{
                Log.d("ImageFail", it.message.toString())
            }
        }

        // below code is used for
        // checking our permissions.
        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            requestPermission()
        }

        createPDFButton.setOnClickListener{
//            Converting the images into bitmap
            val logoDrawable = homeCompanyLogo.drawable as BitmapDrawable
            val logoBitmap = logoDrawable.bitmap

            val signDrawable = homeSign.drawable as BitmapDrawable
            val signBitmap = signDrawable.bitmap

            val name = nameCertificate.text.toString()
            val job = jobCertificate.text.toString()

            generatePDF(logoBitmap, signBitmap, name, job)
        }
    }

    private fun generatePDF(logoBitmap: Bitmap?, signBitmap: Bitmap?, name: String, job: String) {

        // creating an object variable
        // for our PDF document.
        val pdfDocument = PdfDocument()

        // two variables for paint "paint" is used
        // for drawing shapes and we will use "title"
        // for adding text in our PDF file.
        val paint = Paint()
        val title = Paint()

        // we are adding page info to our PDF file
        // in which we will be passing our pageWidth,
        // pageHeight and number of pages and after that
        // we are calling it to create our PDF.
        val mypageInfo = PageInfo.Builder(pagewidth, pageHeight, 1).create()

        // below line is used for setting
        // start page for our PDF file.
        val myPage = pdfDocument.startPage(mypageInfo)

        // creating a variable for canvas
        // from our page of PDF.
        val canvas = myPage.canvas

        // below line is used to draw our image on our PDF file.
        // the first parameter of our drawbitmap method is
        // our bitmap
        // second parameter is position from left
        // third parameter is position from top and last
        // one is our variable for paint.

        // below line is used to draw our image on our PDF file.
        // the first parameter of our drawbitmap method is
        // our bitmap
        // second parameter is position from left
        // third parameter is position from top and last
        // one is our variable for paint.
        canvas.drawBitmap(Bitmap.createScaledBitmap(logoBitmap!!, 200, 200, true), 256f, 200f, paint)
//        canvas.drawBitmap(Bitmap.createScaledBitmap(logoBitmap!!, 100, 100, true),100,100 ,paint)


        // below line is used for adding typeface for
        // our text which we will be adding in our PDF file.
        title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)


        // below line is used for setting text size
        // which we will be displaying in our PDF file.
        title.textSize = 40f

        // below line is sued for setting color
        // of our text inside our PDF file.

        // below line is sued for setting color
        // of our text inside our PDF file.
        title.color = ContextCompat.getColor(this, R.color.purple_200)
        title.textAlign = Paint.Align.CENTER

        // below line is used to draw text in our PDF file.
        // the first parameter is our text, second parameter
        // is position from start, third parameter is position from top
        // and then we are passing our variable of paint which is title.

        // below line is used to draw text in our PDF file.
        // the first parameter is our text, second parameter
        // is position from start, third parameter is position from top
        // and then we are passing our variable of paint which is title.

        val nameCertificate = Paint()
        nameCertificate.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
        nameCertificate.textSize = 80f
        nameCertificate.textAlign = Paint.Align.CENTER
        title.color = ContextCompat.getColor(this, R.color.purple_200)

        canvas.drawText("This is to certify that ", (canvas.width/2).toFloat(), 400f, title)
        canvas.drawText(name, (canvas.width/2).toFloat(), 500f, nameCertificate)
        canvas.drawText("has completed ${jobCertificate.text}", (canvas.width/2).toFloat(), 550f, title)


        canvas.drawBitmap(Bitmap.createScaledBitmap(signBitmap!!, 200, 50, true), 500f, 600f, paint)

// similarly we are creating another text and in this
        // we are aligning this text to center of our PDF file.

        // similarly we are creating another text and in this
        // we are aligning this text to center of our PDF file.
//        title.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
//        title.color = ContextCompat.getColor(this, R.color.purple_200)
//        title.textSize = 15f

        // below line is used for setting
        // our text to center of PDF.

        // below line is used for setting
        // our text to center of PDF.
//        title.textAlign = Paint.Align.CENTER
//        canvas.drawText("This is sample document which we have created.", 396f, 560f, title)

        // after adding all attributes to our
        // PDF file we will be finishing our page.

        // after adding all attributes to our
        // PDF file we will be finishing our page.
        pdfDocument.finishPage(myPage)

        // below line is used to set the name of
        // our PDF file and its path.

        // below line is used to set the name of
        // our PDF file and its path.
        

//        val file = File(Environment.getExternalStorageDirectory(), "Certificate.pdf")

        fileName = "$path/$name"+"Certificate.pdf"

        try {
            // after creating a file name we will
            // write our PDF file to that location.
            pdfDocument.writeTo(FileOutputStream(fileName))

            // below line is to print toast message
            // on completion of PDF generation.
            Toast.makeText(
                this,
                "PDF file generated successfully.",
                Toast.LENGTH_SHORT
            ).show()
            Log.d("PDFGenerated","PDF generated")
        } catch (e: IOException) {
            // below line is used
            // to handle error
            Log.d("PDFNotGenerated",e.message.toString())
        }
        // after storing our pdf to that
        // location we are closing our PDF file.
        // after storing our pdf to that
        // location we are closing our PDF file.
        pdfDocument.close()

        val intent = Intent(this, CertificateView::class.java)
        intent.putExtra("fileName", fileName)
        startActivity(intent)
    }

    private fun checkPermission(): Boolean {
        // checking of permissions.
        val permission1 = ContextCompat.checkSelfPermission(applicationContext,
            permission.WRITE_EXTERNAL_STORAGE
        )
        val permission2 =
            ContextCompat.checkSelfPermission(applicationContext, permission.READ_EXTERNAL_STORAGE)
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }

}