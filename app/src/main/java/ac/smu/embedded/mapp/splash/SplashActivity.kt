package ac.smu.embedded.mapp.splash

import ac.smu.embedded.mapp.common.UserViewModel
import ac.smu.embedded.mapp.intro.IntroActivity
import ac.smu.embedded.mapp.main.MainActivity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.viewmodel.ext.android.viewModel

class SplashActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uid = userViewModel.getUserWithoutProfile()
        val intent: Intent = if (uid != null) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, IntroActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
