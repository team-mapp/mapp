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
        userViewModel.signInProgress.observe(this, Observer {
            loading_progress.visibility = it
        })

        userViewModel.userData.observe(this, Observer {
            if (it != null) {
                val intent = Intent(this, ProfileSettingActivity::class.java).apply {
                    putExtra(EXTRA_FROM_INTRO, true)
                }
                startActivity(intent)
                finish()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        userViewModel.onActivityResult(requestCode, resultCode, data)
    }
}