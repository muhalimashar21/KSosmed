package com.example.ksosmed.logindanregister

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.example.ksosmed.R
import com.example.ksosmed.model.MainActivity
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_register.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        ivImageAkun.setOnClickListener {
            checkPermission()
        }
    }

    val READIMAGE: Int = 253
    fun checkPermission() {
    if (Build.VERSION.SDK_INT >= 23){
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), READIMAGE)
            return
        }
    }
        loadImage()
    }



    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>, grantResults : IntArray){
        when(requestCode){
            READIMAGE -> {
                if (grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    loadImage()
                }else{
                    Toast.makeText(applicationContext, "gambar tidak dapat diakses", Toast.LENGTH_LONG).show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode,
                permissions, grantResults)
        }
    }
    val PICK_IMAGE_CODE = 123
    //load gambar
    fun loadImage() {
        var intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_CODE)
        }

        //result
      override  fun onActivityResult(requestCode: Int, resultCode: Int,
                                     data: Intent?){
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == PICK_IMAGE_CODE && data != null &&
                    resultCode == RESULT_OK){
                //set poto profil
                val selectedImage = data.data
                val filePathColum = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = contentResolver.query(selectedImage,
                filePathColum, null, null, null)
                cursor.moveToFirst()
                val coulomIndex = cursor.getColumnIndex(filePathColum[0])
                val picturePath = cursor.getString(coulomIndex)
                cursor.close()
                ivImageAkun.setImageBitmap(BitmapFactory.decodeFile(picturePath))
            }
        }
//simpan gambar ke firebase
    fun SaveImageInFirebase(){
    //memberi nama gambar yang kana kita save di firebase
        var currentUser = mAuth!!.currentUser
        val email : String= currentUser!!.email.toString()
        val storage = FirebaseStorage.getInstance()
        //link dari firebase storage

        val storageRef = storage.getReferenceFromUrl("gs://ksosmed-74344.appspot.com")
        val df = SimpleDateFormat("ddMMyyHHmmss")
        val dataobj = Date()
        val imagePath = SplitString(email) + "." + df.format(dataobj)+".jpg"
        val ImageRef = storageRef.child("gambar/" + imagePath)
        ivImageAkun.isDrawingCacheEnabled = true
        ivImageAkun.buildDrawingCache()
    //merubah format gambar yang akan kita save
        val ivDrawable = ivImageAkun.drawable as BitmapDrawable
        val bitmap = ivDrawable.bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = ImageRef.putBytes(data)
    var addss =
            FirebaseStorage.getInstance().equals("downloadTokens")
    var DownloadURLz =
            "https:firebasestorage.googleapls.com/v0/b/" +
                    "ksosmed-74344.appspot.com/o/gambar%2F" +
                    SplitString(currentUser.email.toString()) + "." + df
                .format(dataobj) + ".jpg" + "?alt=media&token=" +
                    addss.toString()
    myRef.child("Users").child(currentUser.uid).child("email").setValue(currentUser.email)
    myRef.child("Users").child(currentUser.uid)
    // .child ("profileimage").SetSampler.Value(DownloadURLz
    LoadPost()
    uploadTask.addOnFailureListener {
        Toast.makeText(applicationContext, "gagal upload image",
            Toast.LENGTH_LONG).show()
    }.addOnSuccessListener{task ->
        var addss =
                FirebaseStorage.getInstance().equals("downloadTokens")
        var DownloadURLz =
            "https:firebasestorage.googleapls.com/v0/b/" +
                    "ksosmed-74344.appspot.com/o/gambar%2F" +
                    SplitString(currentUser.email.toString()) + "." + df
                .format(dataobj) + ".jpg" + "?alt=media&token=" +
                    addss.toString()
        myRef.child("Users").child(currentUser.uid).child("email").setValue(currentUser.email)
        myRef.child("Users").child(currentUser.uid)
        // .child ("profileimage").SetSampler.Value(DownloadURLz
        LoadPost()

    }
    }
    // untuk me rename edit text
    fun SplitString(email: String): String {
    val split = email.split("@")
        return split[0]
    }

    override fun OnStart(){
        super.OnStart()
        LoadPost()
    }

    private fun LoadPost() {
        var currentUser = mAuth!!.currentUser
        if (currentUser != null){
            //intent ke mainactivity . Pada Mainactivity akan
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", currentUser.email)
            intent.putExtra("uid", currentUser.uid)
            startActivity(intent)
            finish()
        }
    }


}

