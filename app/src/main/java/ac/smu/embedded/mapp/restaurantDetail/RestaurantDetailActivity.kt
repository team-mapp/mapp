package ac.smu.embedded.mapp.restaurantDetail

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.util.loadStorage
import ac.smu.embedded.mapp.util.recyclerAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import kotlinx.android.synthetic.main.activity_restaurantdetail.*
import kotlinx.android.synthetic.main.item_restaurant_review.view.*
import org.koin.android.viewmodel.ext.android.viewModel


class RestaurantDetailActivity : AppCompatActivity(R.layout.activity_restaurantdetail), OnMapReadyCallback{
    override fun onMapReady(p0: NaverMap) {
        naverMap = p0
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // 뷰 모델 설정
    var naverMap:NaverMap? = null
    private val restaurantDetailViewModel:RestaurantDetailViewModel by viewModel()

    var reviewList = arrayListOf(
        ReviewList(null,"user 1","nothing"),
        ReviewList(null,"user 2","nothing"),
        ReviewList(null,"user 3","nothing"),
        ReviewList(null,"user 4","nothing"),
        ReviewList(null,"user 5","nothing")
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val listAdapter = recyclerAdapter(R.layout.item_restaurant_review, reviewList){view, value ->
            view.tvUserName.text = "${value.userName}"
            view.tvUserReview.text = "${value.reviewContent}"
        }

        rvReviewList.adapter = listAdapter
        rvReviewList.layoutManager = LinearLayoutManager(this)
        tvReview.text = "리뷰(${listAdapter.itemCount})"

        // 이전 액티비티에서 documentId가 존재하는 경우
        if (intent.hasExtra(DOCUMENT_ID)) {
            val documentId = intent.getStringExtra(DOCUMENT_ID)!!

            initView()
            loadContents(documentId)

        } else { // 존재하지 않는 경우
            throw UnsupportedOperationException(
                "Not received intent data, required EXTRA_DATA_TYPE, EXTRA_DOCUMENT_ID"
            )
        }
    }


    override fun onStart(){
        super.onStart()
        MapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        MapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        MapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        MapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        MapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        MapView.onLowMemory()
    }


    fun initView(){
        setSupportActionBar(toolBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }


    }

    fun loadContents(documentId:String){
        restaurantDetailViewModel.loadRestaurant(documentId).observe(this, Observer { resource ->
            resource.onSuccess {
                ivRestaurantImage.loadStorage(it!!.image)
                tvAddressDetail.text = "${it?.address}"
                tvCallNumber.text = "${it?.phone}"
                updateProfile(it!!.name, it.image)

                val latitude = it?.location.latitude
                val longitude = it?.location.longitude

                MapView.getMapAsync(this)
                val marker = Marker()
                marker.position = LatLng(latitude, longitude)
                marker.map = naverMap

                val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude, longitude))
                naverMap?.moveCamera(cameraUpdate)


            }
        })
    }

    fun updateProfile(name:String, image:String){
        viewProfile.setName(name)
        viewProfile.setImage(image,true)
    }
    companion object{
        const val DOCUMENT_ID = "document_id"
    }
}