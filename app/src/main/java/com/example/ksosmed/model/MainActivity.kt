package com.example.ksosmed.model

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.example.ksosmed.DataPostingan
import com.example.ksosmed.InfoPostingan
import com.example.ksosmed.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_add_post.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_list.view.*

class MainActivity : AppCompatActivity() {
    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    private var mAuth: FirebaseAuth? = null
    private var firebaseStorage : FirebaseStorage? = null
    //deklarasi variabel list post
    val ListPost = ArrayList<DataPostingan>()
    //variabel inner class variabel
    var adpater: MyPostAdapter? = null
    //deklarasi info user
    var myemail: String? = null
    var UserUID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        var b: Bundle = intent.extras
        //myemail = email pada firebase database
        myemail = b.getString("email")
        // id user
        UserUID = b.getString("uid")
        //tambahkan postingan baru berdasarkan class Data postingan
        ListPost.add(DataPostingan("0", "him", "url", "add"))
        //set Adapter
        adpater = MyPostAdapter(this, ListPost)
        lvTweets.adapter = adpater
        //load post yang sudah ada
        LoadPost()
    }

    inner class MyPostAdapter: BaseAdapter {
        var listNotesAdapter = ArrayList<DataPostingan>()
        var context: Context

        constructor(
            context: Context, listNotesAdapter:
                    ArrayList<DataPostingan>
        ): super(){
            this.listNotesAdapter = listNotesAdapter
            this.context = context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var mypost = listNotesAdapter[p0]
            if (mypost.postPersonUID.equals("add")){
                //code untuk tambahkan postingan
                var myView = layoutInflater.inflate(
                    R.layout.activity_add_post,
                    null
                )
                //button pilih gambar
                myView.iv_gambar.setOnClickListener {
                    //load gambar yang kana di post
                    loadImage()
                }
                //button upload gambar
                myView.iv_post.setOnClickListener {
                    //upload server
                    myRef.child("posts").push().setValue(
                        InfoPostingan(
                            UserUID!!,
                            myView.etPost.text.toString(),
                            DownloadURL!!
                        )
                    )
                    myView.etPost.setText("")
                }
                return myView
                //tampilkan loading ketika upload gambar
            }else if (mypost.postPersonUID.equals("loading")){
                val
                        myView = layoutInflater.inflate(R.layout.loading_ticket, null)
                return myView
                //tampilkan welcome to bla bla bla
            }else if (mypost.postPersonUID.equals("ads")){
                val
                        myView = layoutInflater.inflate(R.layout.layout_welcome, null)
                return myView
                //tampilkan postingan
            }else {
                //layout post (itemlist)
                val
                        myView = layoutInflater.inflate(R.layout.item_list, null)
                //isi dari postingan
                myView.txt_detail_postingan.text = mypost.postText
                //tampilkan gambar
                Glide.with(context).load(mypost.postImageURL)
                //place holder untuk tampilan ketika gambar masuk loading
                    .placeholder(R.mipmap.ic_launcher)
                    .centerCrop()
                    .into(myView.gambar_postingan)
                //tampilka gambar username dna poto user
                myRef.child("Users").child(mypost.postPersonUID!!)
                    .addValueEventListener(object :
                    ValueEventListener{
                        override fun onDataChange(
                            dataSnapshot:
                            DataSnapshot
                        ){
                            try {
                                //poto user
                                var td = dataSnapshot.value as HashMap<String, Any>
                                for (key in td.keys){
                                    var userInfo = td[key] as String
                                    if (key.equals("ProfileImage")){
                                        Glide.with(context)
                                            .load(userInfo)
                                            .placeholder(R.mipmap.ic_launcher)
                                            .into(myView.poto_user)
                                        //username
                                    }else{
                                        myView.txtUsername.text =
                                                SplitString(userInfo)
                                    }
                                }
                            }catch (ex: Exception){
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                            p0:
                            DatabaseError
                        }
                        //tangkap data dari item
                        override fun getItem(p0: Int): Any{
                            return listNotesAdapter[p0]
                        }
                        //tangkap id item
                        override fun getItemId(p0: Int): Long{
                            return p0.toLong()
                        }
                        //get jumlah item
                        override fun getCount(): Int {
                            return listNotesAdapter.size
                        }

                    })
            }
            //Load Image
            val PICK_IMAGE_CODE = 123


        }



    }

    fun loadImage() {
    //intent galeri atau aplikasi ke foto yang lainnya
        var intent = Intent(
            Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, PICK_IMAGE_CODE)
    }

    override fun onActivityResult(
        requestCode: Int, resultCode:Int,
        data: Intent?
    ){
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_CODE && data != null && resultCode
        == RESULT_OK
        ){
            val selectedImage = data.data
            val filePathColum = arrayOf(MediaStore.Images.Media.DATA)
            var cursor =
                    contentResolver.query(selectedImage, filePathColum, null, null, null)
            cursor.moveToFirst()
            val coulomIndex = cursor.getColumnIndex(filePathColum[0])
            val picturePath =  cursor.getString(coulomIndex)
            cursor.close()
            //upload gambar dengan format bitmap
            UploadImage(BitmapFactory.decodeFile(picturePath))
        }
    }
    //download url gambar
    var DownloadURL: String? = ""

    //upload gambar
    private fun UploadImage(bitmap: Bitmap?) {

    }


    
}
