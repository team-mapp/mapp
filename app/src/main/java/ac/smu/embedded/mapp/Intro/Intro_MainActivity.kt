package ac.smu.embedded.mapp.Intro

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.common.UserViewModel
import ac.smu.embedded.mapp.main.MainActivity
import ac.smu.embedded.mapp.util.loadStorage
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_intro__main.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.net.URI


class Intro_MainActivity : AppCompatActivity() {


    private val OPEN_GALLERY = 1
    private val userViewModel: UserViewModel by viewModel()
    private val introViewModel: IntroViewModel by viewModel()
    private
    var currentImageURL: Uri? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro__main)

        userViewModel.userData.observe(this, Observer {
            Log.d("IntroActivity", it.toString())
            if (it != null && currentImageURL != null) {
                introViewModel.upladImage(currentImageURL!!)!!.observe(this, Observer {
                    it.onComplete {
                        var intent = Intent(this,MainActivity::class.java )
                        startActivity(intent)
                    }
                })

            }
        })

        textView4.setOnClickListener{

            userViewModel.signInWithGoogle(this, getString(R.string.default_web_client_id))


        }
        //이미지뷰에 리스너 추가하기
       imageView.setOnClickListener{

           openGallery()
       }
        //imageView.loadStorage("")


    }

    //gallery를 여는 함수
    private fun openGallery(){
        var intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent, OPEN_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        userViewModel.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            if (requestCode == OPEN_GALLERY){

                currentImageURL =data?.data
                introViewModel.upladImage(currentImageURL!!)//!!null채크안하고 무조건넣겠다.

                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,currentImageURL)
                    imageView.setImageBitmap(bitmap)
                }
                catch(e:Exception){
                    e.printStackTrace()
                }
            }
        }
        else{
            Log.d("ActivityResult","something wrong")
        }
    }

}


