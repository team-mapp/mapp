package ac.smu.embedded.mapp.restaurantDetail

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.review.ReviewViewActivity
import ac.smu.embedded.mapp.review.ReviewWriteActivity
import ac.smu.embedded.mapp.util.BaseRecyclerAdapter
import ac.smu.embedded.mapp.util.load
import ac.smu.embedded.mapp.util.loadStorage
import ac.smu.embedded.mapp.util.recyclerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import kotlinx.android.synthetic.main.activity_restaurantdetail.*
import kotlinx.android.synthetic.main.item_restaurant_review.*
import kotlinx.android.synthetic.main.view_review_average_panel.*
import org.koin.android.viewmodel.ext.android.viewModel


class RestaurantDetailActivity : AppCompatActivity(R.layout.activity_restaurantdetail),
    OnMapReadyCallback {

    // 뷰 모델 설정
    private val restaurantDetailViewModel: RestaurantDetailViewModel by viewModel()

    private var naverMap: NaverMap? = null
    private lateinit var adapter: BaseRecyclerAdapter<ReviewWithUser>
    private var isFavorite: Boolean = false

    private lateinit var waitingTimeMap: Map<String, Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 이전 액티비티에서 documentId가 존재하는 경우
        if (intent.hasExtra(DOCUMENT_ID)) {
            // 여기서 documentId -> restaurantId
            val documentId: String = intent.getStringExtra(DOCUMENT_ID)!!

            initView()
            loadContents(documentId)

            floatingButton.setOnClickListener {
                val intent = Intent(this, ReviewWriteActivity::class.java).apply {
                    putExtra(ReviewWriteActivity.EXTRA_RESTAURANT_ID, documentId)
                }
                startActivity(intent)
            }

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

    private fun initView() {
        // 액션바 설정
        setSupportActionBar(toolBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        // adapter 설정
        adapter = recyclerAdapter(R.layout.item_restaurant_review) { viewHolder, view, value ->
            val user = value.user
            if (user != null) {
                with(user) {
                    if (profileImage != null) {
                        if (isExternalProfile()) {
                            viewHolder.ivUserProfile.load(
                                profileImage,
                                listOf(RequestOptions.circleCropTransform())
                            )
                        } else {
                            viewHolder.ivUserProfile.loadStorage(
                                profileImage,
                                listOf(RequestOptions.circleCropTransform())
                            )
                        }
                    }
                    viewHolder.tvUserName.text = "$displayName"
                }
            }

            with(value.review) {
                val recommendText = when (content.recommendPoint) {
                    RECOMMEND_NO -> getString(R.string.recommend_no)
                    RECOMMEND_SOSO -> getString(R.string.recommend_so_so)
                    RECOMMEND_YES -> getString(R.string.recommend_yes)
                    else -> ""
                }

                viewHolder.rbCostEffective.rating = content.costEffective!!.toFloat()
                viewHolder.rbServicePoint.rating = content.servicePoint!!.toFloat()
                viewHolder.tv_review_content.text = content.detailAnswer!!
                viewHolder.tv_recommend.text = "\"$recommendText\""
                viewHolder.tv_date.text = getDate(createdAt, updatedAt)
            }

            view.setOnClickListener {
                val intent = Intent(this, ReviewViewActivity::class.java).apply {
                    putExtra(ReviewViewActivity.EXTRA_REVIEW_ID, value.review.documentId)
                }
                startActivity(intent)
            }
        }
        rvReviewList.adapter = adapter
        rvReviewList.layoutManager = LinearLayoutManager(this)

        waitingTimeMap = mapOf(
            getString(R.string.waiting_time_none) to 0,
            getString(R.string.waiting_time_10) to 10,
            getString(R.string.waiting_time_30) to 30,
            getString(R.string.waiting_time_60) to 60,
            getString(R.string.waiting_time_over) to 61,
            getString(R.string.waiting_time_dont_know) to 999
        )
    }

    @SuppressLint("SetTextI18n")
    private fun loadContents(documentId: String) {
        // restaurant 정보 가져오기
        restaurantDetailViewModel.restaurant.observe(this, Observer {
            ivRestaurantImage.loadStorage(it!!.image, listOf(RequestOptions.centerCropTransform()))
            tvAddressDetail.text = it.address
            tvCallNumber.text = it.phone

            updateProfile(it.name, it.image)

            // 지도표시
            val latitude = it.location.latitude
            val longitude = it.location.longitude

            MapView.getMapAsync(this)
            val marker = Marker().apply {
                position = LatLng(latitude, longitude)
            }
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
                tvReview.text = "${getString(R.string.msg_review)} (${it.size})"

                view_avg_panel.visibility = View.VISIBLE

                val avgCostEffective = it.fold(0f) { acc, reviewWithUser ->
                    acc + reviewWithUser.review.content.costEffective!!
                } / it.size

                tv_avg_cost_effective.text = String.format("%.1f", avgCostEffective)

                val avgServicePoint = it.fold(0f) { acc, reviewWithUser ->
                    acc + reviewWithUser.review.content.servicePoint!!
                } / it.size

                tv_avg_service_point.text = String.format("%.1f", avgServicePoint)

                val groupWaitingTime = it.groupBy { it.review.content.waitingTime }
                var maxWaitingTimeSize = 0
                var maxWaitingTime = 0
                groupWaitingTime.forEach { entry ->
                    if (entry.value.size > maxWaitingTimeSize) {
                        maxWaitingTime = entry.key!!
                        maxWaitingTimeSize = entry.value.size
                    }
                }

                tv_avg_waiting_time.text = waitTimeFromInt(maxWaitingTime)

                tvEmptyReview.visibility = View.GONE
                adapter.submitList(it)
            } else {
                view_avg_panel.visibility = View.GONE
                tvEmptyReview.visibility = View.VISIBLE
            }
            loading_progress.visibility = View.GONE
        })

        restaurantDetailViewModel.loadRestaurant(documentId)
        restaurantDetailViewModel.loadReview(documentId)
    }

    private fun waitTimeFromInt(value: Int): String? {
        waitingTimeMap.forEach {
            if (it.value == value) {
                return it.key
            }
        }
        return null
    }

    private fun getDate(createdAt: Timestamp, updatedAt: Timestamp): String {
        return if (createdAt == updatedAt) {
            String.format(
                getString(R.string.created_date_format),
                DateUtils.getRelativeTimeSpanString(createdAt.toDate().time).toString()
            )
        } else {
            String.format(
                getString(R.string.updated_date_format),
                DateUtils.getRelativeTimeSpanString(updatedAt.toDate().time).toString()
            )
        }
    }

    private fun updateProfile(name: String, image: String) {
        viewProfile.setTitle(name)
        viewProfile.setImage(image, true)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
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

    // 뒤로가기 설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val DOCUMENT_ID = "document_id"

        private const val RECOMMEND_YES = 1
        private const val RECOMMEND_SOSO = 0
        private const val RECOMMEND_NO = -1
    }
}