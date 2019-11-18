package ac.smu.embedded.mapp.profile

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.model.UploadTaskStatus
import ac.smu.embedded.mapp.profile.FavoritesList
import ac.smu.embedded.mapp.common.view.ProfileView
import ac.smu.embedded.mapp.common.view.ContentView
import ac.smu.embedded.mapp.util.loadStorage
import ac.smu.embedded.mapp.util.recyclerAdapter
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore.MediaColumns.DOCUMENT_ID
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_profile.*
import org.koin.android.viewmodel.ext.android.viewModel
import android.os.PersistableBundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.item_favorites.view.*

class ProfileActivity : AppCompatActivity(R.layout.activity_profile) {

    private val PERMISSION_CODE = 0;
    private val IMAGE_PICK_CODE = 1;
    //private val IMAGE_CROP_CODE = 2;

    private val profileViewModel: ProfileViewModel by viewModel()

    val favoritesList = arrayListOf(
        FavoritesList("식당1"),
        FavoritesList("식당2"),
        FavoritesList("식당3"),
        FavoritesList("식당4")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        val listAdapter = recyclerAdapter(R.layout.item_favorites, favoritesList) { view , value ->
            view.userFavorites.text = "${value.restaurantName}"
        }
        rvFavoritesList.adapter = listAdapter
        rvFavoritesList.layoutManager = LinearLayoutManager(this)

        // 이전 액티비티에서 documentId가 존재하는 경우
//        if (intent.hasExtra(DOCUMENT_ID)) {
//            val documentId = intent.getStringExtra(DOCUMENT_ID)!!
//            initView()
//            //loadContents(documentId)
//        } else {
//            throw UnsupportedOperationException(
//                "Not received intent data, required EXTRA_DATA_TYPE, EXTRA_DOCUMENT_ID"
//            )
//        }

        //프로필 이미지뷰 클릭시
        profileImgView.setOnClickListener {
            //permission 허가 여부 확인
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    //permission 거부
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //permission 허가 요청
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    //permission이 이미 허가된 경우
                    pickImageFromGallery();
                }
            } else {
                //system OS < Marshmallow 버전
                pickImageFromGallery();
            }
        }
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            profileViewModel.uploadImage(data?.data!!, "test").observe(this, Observer {
                if (it.status == UploadTaskStatus.COMPLETE) {
                    profileImgView.loadStorage("test.jpg")
                }
            })
            //profileImgButton.setImageURI(data?.data)
        }
    }

    companion object{
        const val DOCUMENT_ID = "document_id"
    }

    override fun onStart(){1
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    fun initView(){
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }
}