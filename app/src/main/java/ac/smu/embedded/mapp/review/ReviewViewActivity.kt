package ac.smu.embedded.mapp.review

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.model.Review
import android.os.Bundle
import android.text.format.DateUtils
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.activity_view_review.*
import kotlinx.android.synthetic.main.content_view_review.*
import org.koin.android.viewmodel.ext.android.viewModel

class ReviewViewActivity : AppCompatActivity(R.layout.activity_view_review) {

    private val reviewViewModel: ReviewViewModel by viewModel()

    private var review: Review? = null

    private lateinit var waitingTimeMap: Map<String, Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra(EXTRA_REVIEW_ID)) {
            var reviewId: String? = null
            if (intent.hasExtra(EXTRA_REVIEW_ID)) {
                reviewId = intent.getStringExtra(EXTRA_REVIEW_ID)
            }

            initView()
            setupObservers()
            loadContent(reviewId)
        } else {
            throw UnsupportedOperationException(
                "Not received intent data, required EXTRA_REVIEW_ID"
            )
        }
    }

    private fun initView() {
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        waitingTimeMap = mapOf(
            getString(R.string.waiting_time_none) to 0,
            getString(R.string.waiting_time_10) to 10,
            getString(R.string.waiting_time_30) to 30,
            getString(R.string.waiting_time_60) to 60,
            getString(R.string.waiting_time_over) to 61,
            getString(R.string.waiting_time_dont_know) to 999
        )
    }

    private fun setupObservers() {
        reviewViewModel.restaurant.observe(this, Observer {
            with(view_profile) {
                if (it != null) {
                    setImage(it.image)
                    setTitle(it.name)
                }
            }
        })

        reviewViewModel.user.observe(this, Observer {
            with(view_user_profile) {
                if (it != null) {
                    if (it.profileImage != null) {
                        setImage(it.profileImage, !it.isExternalProfile())
                    }
                    setTitle(it.displayName!!)
                }
            }
        })

        reviewViewModel.review.observe(this, Observer {
            if (it != null) {
                reviewViewModel.loadUser(it.userId)
                reviewViewModel.loadRestaurant(it.restaurantId)
                view_user_profile.setSubtitle(getDate(it.createdAt, it.updatedAt))

                with(it.content) {
                    when (recommendPoint) {
                        RECOMMEND_YES -> {
                            iv_recommend.setImageDrawable(getDrawable(R.drawable.ic_very_satisfied))
                            tv_recommend.text = getString(R.string.answer_yes)
                        }
                        RECOMMEND_SOSO -> {
                            iv_recommend.setImageDrawable(getDrawable(R.drawable.ic_dissatisfied))
                            tv_recommend.text = getString(R.string.answer_so_so)
                        }
                        RECOMMEND_NO -> {
                            iv_recommend.setImageDrawable(getDrawable(R.drawable.ic_very_dissatisfied))
                            tv_recommend.text = getString(R.string.answer_no)
                        }
                    }

                    rating_cost_effective.rating = costEffective?.toFloat()!!
                    rating_service_point.rating = servicePoint?.toFloat()!!
                    tv_waiting_time.text = waitTimeFromInt(waitingTime!!)
                    tv_eaten_food.text = eatenFood

                    if (aboutFood != null) {
                        group_about_food.visibility = View.VISIBLE
                        tv_about_food.text = aboutFood
                    } else {
                        group_about_food.visibility = View.GONE
                    }

                    if (bestPlace != null) {
                        group_best_place.visibility = View.VISIBLE
                        tv_best_place.text = bestPlace
                    } else {
                        group_best_place.visibility = View.GONE
                    }

                    if (aboutPlace != null) {
                        group_about_place.visibility = View.VISIBLE
                        tv_about_place.text = aboutPlace
                    } else {
                        group_about_place.visibility = View.GONE
                    }

                    tv_review_content.text = detailAnswer
                }
            }
            review = it
        })
    }

    private fun loadContent(reviewId: String?) {
        if (reviewId != null) {
            reviewViewModel.loadReview(reviewId)
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_REVIEW_ID = "review_id"

        private const val RECOMMEND_YES = 1
        private const val RECOMMEND_SOSO = 0
        private const val RECOMMEND_NO = -1
    }
}