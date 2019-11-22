package ac.smu.embedded.mapp.intro

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.common.UserViewModel
import ac.smu.embedded.mapp.profile.ProfileSettingActivity
import ac.smu.embedded.mapp.util.EXTRA_FROM_INTRO
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_intro.*
import org.koin.android.viewmodel.ext.android.viewModel

class IntroActivity : AppCompatActivity(R.layout.activity_intro) {

    private val userViewModel: UserViewModel by viewModel()

//    private val OPEN_GALLERY = 1
//    private val introViewModel: IntroViewModel by viewModel()
//    private var currentImageURL: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setupObservers()
    }

    private fun initView() {
        btn_google_signin.setOnClickListener {
            userViewModel.signInWithGoogle(this, getString(R.string.default_web_client_id))
        }
    }

    private fun setupObservers() {
        userViewModel.userData.observe(this, Observer {
            if (it != null) {
                val intent = Intent(this, ProfileSettingActivity::class.java).apply {
                    putExtra(EXTRA_FROM_INTRO, true)
                }
                startActivity(intent)
                finish()
            }
        })

//        userViewModel.userData.observe(this, Observer {
//            Log.d("IntroActivity", it.toString())
//            if (it != null && currentImageURL != null) {
//                introViewModel.upladImage(currentImageURL!!)
//            }
//        })
//
//        introViewModel.uploadCompleted.observe(this, Observer {
//            if (it) {
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//            }
//        })

//        textView4.setOnClickListener {
//            userViewModel.signInWithGoogle(this, getString(R.string.default_web_client_id))
//        }
        // 이미지뷰에 리스너 추가하기
//        imageView.setOnClickListener {
//            openGallery()
//        }
        // imageView.loadStorage("")
    }

    // gallery를 여는 함수
//    private fun openGallery() {
//        var intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "image/*"
//        startActivityForResult(intent, OPEN_GALLERY)
//    }
//
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        userViewModel.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == OPEN_GALLERY) {
//                currentImageURL = data?.data
//                introViewModel.upladImage(currentImageURL!!) //!!null채크안하고 무조건넣겠다.
//
//                try {
//                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImageURL)
//                    imageView.setImageBitmap(bitmap)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        } else {
//            Log.d("ActivityResult", "something wrong")
//        }
    }
}