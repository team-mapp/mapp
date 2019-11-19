package ac.smu.embedded.mapp.splash

import ac.smu.embedded.mapp.intro.IntroActivity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


// TODO (1) 기기 내 설정 값을 통해 로그인이 된 경우 MainActivity로 이동하기
// TODO (2) Splash, IntroActivity의 백그라운드 색상을 다른 화면과 맞추기 (로고 내 배경색 지워야함)
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, IntroActivity::class.java)
        startActivity(intent)
        finish()
    }
}
