package com.example.ksosmed.logindanregister

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.ksosmed.R
import com.example.ksosmed.model.MainActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    //deklarasi variabel untuk firebase dkk
    private var mAuth: FirebaseAuth? = null
    private var database = FirebaseDatabase.getInstance()
    private var  myRef = database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // get instance
        mAuth = FirebaseAuth.getInstance()
        //onclick untuk daftar
        tvDaftar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
//event Onclik Login
    fun btnLoginPage(view : View){
    LoginToFirebase(
        etEmail.text.toString(),
        etEmail.text.toString()
    )
}



    //onStart
    fun OnStart (){
        super.onStart()
        LoadPost()
    }

    private fun LoadPost() {
        var currentUser = mAuth!!.currentUser
        if (currentUser != null){
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", currentUser.email)
            intent.putExtra("uid", currentUser.uid)
            startActivity(intent)
        }
    }

    //login ke firebase
    fun LoginToFirebase(email: String, password: String) {
    //firebase login dengan email dan password
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {task ->
                //jika sukses
                if (task.isSuccessful) {
                    var currentUser = mAuth!!.currentUser
                    Toast.makeText(
                        applicationContext, "sukses login", Toast.LENGTH_LONG
                    ).show()
                    //save data ke firebase berdasarkan input pada edit text
                    myRef.child("Users").child(currentUser!!.uid).child("email")
                        .setValue(currentUser.email)
                    LoadPost()
                }else{
                    Toast.makeText(
                        applicationContext, "gagal login ", Toast.LENGTH_LONG).show()
                }
            }
    }

}
