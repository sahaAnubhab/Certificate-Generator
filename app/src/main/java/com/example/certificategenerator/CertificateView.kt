package com.example.certificategenerator

import android.os.Bundle
import android.os.Environment
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_certificate_view.*
import java.io.File


class CertificateView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_certificate_view)
        val intent = intent.getStringExtra("fileName").toString()
//        val path = File(Environment.getExternalStorageDirectory().absolutePath+"/Certificate")
//        val file = File("$path/AnubhabCertificate.pdf")
        Log.d("gettingIntent", intent)
        val file = File(intent)
        pdfView.fromFile(file)
            .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
            .enableSwipe(true)
            .enableDoubletap(true)
            .swipeVertical(false)
            .defaultPage(1)
            .showMinimap(false)
            .load()
    }
}