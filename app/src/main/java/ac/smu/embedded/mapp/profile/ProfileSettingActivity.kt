package ac.smu.embedded.mapp.profile

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.main.MainActivity
import ac.smu.embedded.mapp.util.EXTRA_FROM_INTRO
import ac.smu.embedded.mapp.util.load
import ac.smu.embedded.mapp.util.loadStorage
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_profile_setting.*
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileSettingActivity : AppCompatActivity(R.layout.activity_profile_setting) {

    private val profileSettingViewModel: ProfileSettingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var isFromIntro = false
        if (intent.hasExtra(EXTRA_FROM_INTRO)) {
            isFromIntro = intent.getBooleanExtra(EXTRA_FROM_INTRO, false)
        }
        initView(isFromIntro)
        setupObservers(isFromIntro)
    }

    private fun initView(isFromIntro: Boolean) {
        if (isFromIntro) {
            toolbar.visibility = View.GONE
            tv_profile_setting.visibility = View.VISIBLE
            btn_get_started.visibility = View.VISIBLE
        } else {
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
            }
        }

        iv_profile.setOnClickListener {
            getImageFromGallery()
        }

        btn_get_started.setOnClickListener {
            updateProfile()
        }
    }

    private fun setupObservers(isFromIntro: Boolean) {
        profileSettingViewModel.user.observe(this, Observer {
            if (it != null) {
                with(it) {
                    val externalProfile = isExternalProfile()
                    val requestOptions = listOf(RequestOptions.circleCropTransform())
                    if (profileImage != null) {
                        iv_profile.clearColorFilter()
                        if (externalProfile) {
                            iv_profile.load(profileImage, requestOptions)
                        } else {
                            iv_profile.loadStorage(profileImage, requestOptions)
                        }
                    }
                    edit_username.setText(displayName)
                }
            }
        })

        profileSettingViewModel.validUsername.observe(this, Observer {
            layout_username.error = if (!it) getString(R.string.msg_req_username) else null
        })

        profileSettingViewModel.profileUpdated.observe(this, Observer {
            if (it) {
                if (isFromIntro) {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                finish()
            }
        })

        profileSettingViewModel.profileImage.observe(this, Observer {
            if (it != null) {
                iv_profile.loadStorage(
                    it,
                    listOf(RequestOptions().circleCrop())
                )
            }
        })

        profileSettingViewModel.showProgress.observe(this, Observer {
            loading_progress.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    private fun updateProfile() {
        profileSettingViewModel.updateProfile(edit_username.text.toString())
    }

    private fun getImageFromGallery() {
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        startActivityForResult(intent, RC_IMAGE_GALLERY)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile_setting, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> super.onBackPressed()
            R.id.action_save -> {
                updateProfile()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_IMAGE_GALLERY -> {
                if (resultCode == Activity.RESULT_OK
                    && data != null
                    && data.data != null
                ) {
                    profileSettingViewModel.uploadImage(data.data!!)
                }
            }
        }
    }

    companion object {

        private const val RC_IMAGE_GALLERY = 100

    }
}