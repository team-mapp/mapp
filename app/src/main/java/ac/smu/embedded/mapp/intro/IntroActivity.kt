package ac.smu.embedded.mapp.intro

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.common.UserViewModel
import ac.smu.embedded.mapp.main.MainActivity
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_intro.*
import org.koin.android.viewmodel.ext.android.viewModel

// TODO (3) '시작하기' 대신 '구글로 시작하기'와 같은 버튼이면 좋을것 같음
// TODO (4) 프로필 설정 내 프로필 이미지를 원형으로 변경 및 64dp 로 변경, 닉네임을 설정할 수 없으므로 nickname 텍스트는 제거했으면 함
// TODO (5) 카메라로 프로필 설정하는 건 어디에...? 프로필 설정 시 다이얼로그로 카메라, 갤러리 중 선택하도록 변경하기
// TODO     갤러리에서만 가져올거라면 매니페스트에서 권한 삭제하기
// TODO (7) build.gradle 에 추가한 'gun0912.ted:tedpermission:2.1.0'를 사용하지 않는데 앞으로 사용하지 않을거라면 삭제를 권장
class IntroActivity : AppCompatActivity() {

    private val OPEN_GALLERY = 1
    private val userViewModel: UserViewModel by viewModel()
    private val introViewModel: IntroViewModel by viewModel()
    private var currentImageURL: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        userViewModel.userData.observe(this, Observer {
            Log.d("IntroActivity", it.toString())
            if (it != null && currentImageURL != null) {
                introViewModel.upladImage(currentImageURL!!)
            }
        })

        introViewModel.uploadCompleted.observe(this, Observer {
            if (it) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        })

        textView4.setOnClickListener {
            userViewModel.signInWithGoogle(this, getString(R.string.default_web_client_id))
        }
        // 이미지뷰에 리스너 추가하기
        imageView.setOnClickListener {
            openGallery()
        }
        // imageView.loadStorage("")
    }

    // gallery를 여는 함수
    private fun openGallery() {
        var intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, OPEN_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        userViewModel.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == OPEN_GALLERY) {
                currentImageURL = data?.data
                introViewModel.upladImage(currentImageURL!!) //!!null채크안하고 무조건넣겠다.

                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImageURL)
                    imageView.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            Log.d("ActivityResult", "something wrong")
        }
    }
}