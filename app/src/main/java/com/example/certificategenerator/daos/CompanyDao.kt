package com.example.certificategenerator.daos

import android.util.Log
import com.example.certificategenerator.models.Company
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CompanyDao {
    val db = Firebase.firestore
    val companyCollection = db.collection("companies")
    val auth = Firebase.auth

    fun addCompany(company: Company){
        GlobalScope.launch {
            company.let {
                companyCollection.document(company.cid).set(it).addOnSuccessListener {
                    Log.d("CompleteUpload", "success")
                }.addOnFailureListener{ exception ->
                    Log.d("CompleteUpload", "Failure ${exception.message}")
                }
            }
        }
    }

    fun getCompany(companyId: String): Task<DocumentSnapshot> {
        return companyCollection.document(companyId).get()
    }
}