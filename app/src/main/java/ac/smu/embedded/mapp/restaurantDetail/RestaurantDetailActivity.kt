package ac.smu.embedded.mapp.restaurantDetail

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.model.Review
import ac.smu.embedded.mapp.review.ReviewViewActivity
import ac.smu.embedded.mapp.review.ReviewWriteActivity
import ac.smu.embedded.mapp.util.BaseRecyclerAdapter
import ac.smu.embedded.mapp.util.load
import ac.smu.embedded.mapp.util.loadStorage
import ac.smu.embedded.mapp.util.recyclerAdapter
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.RequestOptions
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import kotlinx.android.synthetic.main.activity_restaurantdetail.*
import kotlinx.android.synthetic.main.item_restaurant_review.*
import kotlinx.android.synthetic.main.item_restaurant_review.view.*
import org.koin.android.viewmodel.ext.android.viewModel


class RestaurantDetailActivity : AppCompatActivity(R.layout.activity_restaurantdetail),
    OnMapReadyCallback {
    override fun onMapReady(p0: NaverMap) {
        naverMap = p0
    }

    // 뷰 모델 설정
    var naverMap: NaverMap? = null
    private val restaurantDetailViewModel: RestaurantDetailViewModel by viewModel()
    private lateinit var adapter: BaseRecyclerAdapter<ReviewWithUser>
    var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 이전 액티비티에서 documentId가 존재하는 경우
        if (intent.hasExtra(DOCUMENT_ID)) {
            // 여기서 documentId -> restaurantId
            val documentId: String = intent.getStringExtra(DOCUMENT_ID)

            floatingButton.setOnClickListener { view ->
                val intent = Intent(this, ReviewWriteActivity::class.java).apply {
                    putExtra(ReviewWriteActivity.EXTRA_RESTAURANT_ID, documentId)
                }
                startActivity(intent)
            }

            initView()
            loadContents(documentId)

            btn_favorite.setOnClickListener {
                if (isFavorite) {
                    isFavorite = false
                    btn_favorite.setColorFilter(
                        ContextCompat.getColor(
                            this,
                            R.color.card_filter_color
                        )
                    )
                    restaurantDetailViewModel.removeFavorite(documentId)
                } else {
                    isFavorite = true
                    btn_favorite.setColorFilter(
                        ContextCompat.getColor(
                            this,
                            R.color.content_favorite_color
                        )
                    )
                    restaurantDetailViewModel.addFavorite(documentId)
                }
            }

        } else { // 존재하지 않는 경우
            throw UnsupportedOperationException(
                "Not received intent data, required EXTRA_DATA_TYPE, EXTRA_DOCUMENT_ID"
            )
        }

    }

    override fun onStart() {
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


    fun initView() {
        // 액션바 설정
        setSupportActionBar(toolBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        // adapter 설정
        adapter = recyclerAdapter(R.layout.item_restaurant_review) { _, view, value ->
            val user = value.user
            if (user != null) {
                with (user) {
                    if (profileImage != null) {
                        if (isExternalProfile()) {
                            view.ivUserProfile.load(profileImage, listOf(RequestOptions().circleCrop()))
                        }  else {
                            view.ivUserProfile.loadStorage(profileImage, listOf(RequestOptions().circleCrop()))
                        }
                    }
                    view.tvUserName.text = "$displayName"
                }

            }
            view.rbCostRecommendPoint.rating = value.review.content.recommendPoint!!.toFloat()
            view.rbCostServicePoint.rating = value.review.content.servicePoint!!.toFloat()

            view.setOnClickListener {
                val intent = Intent(this, ReviewViewActivity::class.java).apply {
                    putExtra(ReviewViewActivity.EXTRA_REVIEW_ID, value.review.documentId)
                }
                startActivity(intent)
            }
        }
        rvReviewList.adapter = adapter
        rvReviewList.layoutManager = LinearLayoutManager(this)

    }

    fun loadContents(documentId: String) {
        // restaurant 정보 가져오기
        restaurantDetailViewModel.restaurant.observe(this, Observer {
            ivRestaurantImage.loadStorage(it!!.image)
            tvAddressDetail.text = "${it.address}"
            tvCallNumber.text = "${it.phone}"

            updateProfile(it.name, it.image)

            // 지도표시
            val latitude = it.location.latitude
            val longitude = it.location.longitude

            MapView.getMapAsync(this)
            val marker = Marker()
            marker.position = LatLng(latitude, longitude)
            marker.map = naverMap

            val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude, longitude))
            naverMap?.moveCamera(cameraUpdate)

            restaurantDetailViewModel.loadFavorite(documentId)
        })


        // favorite 하트 버튼 색 변경
        restaurantDetailViewModel.favorite.observe(this, Observer {
            if (it) {
                btn_favorite.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.content_favorite_color
                    )
                )
                isFavorite = it
            } else {
                btn_favorite.setColorFilter(ContextCompat.getColor(this, R.color.card_filter_color))
                isFavorite = it
            }
        })

        // review list 가져오기
        restaurantDetailViewModel.reviews.observe(this, Observer {
            if (it != null) {
                tvEmptyReview.visibility = View.GONE
                adapter.submitList(it)
            }else{
                tvEmptyReview.visibility = View.VISIBLE
            }
        })

        restaurantDetailViewModel.loadRestaurant(documentId)
        restaurantDetailViewModel.loadReview(documentId)
    }


    fun updateProfile(name: String, image: String) {
        viewProfile.setName(name)
        viewProfile.setImage(image, true)
    }

    // 뒤로가기 설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val DOCUMENT_ID = "document_id"
    }
}