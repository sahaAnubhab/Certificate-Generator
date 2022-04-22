package com.example.certificategenerator.models

import android.net.Uri

data class Company(
    val cid: String = "",
    val companyName: String= "",
    val companyEmail: String= "",
    val companyLogo: Uri? = null,
    val signature: Uri? = null
)
