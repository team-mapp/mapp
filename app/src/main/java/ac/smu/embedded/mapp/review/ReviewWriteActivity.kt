package ac.smu.embedded.mapp.review

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.model.Review
import ac.smu.embedded.mapp.model.ReviewContent
import ac.smu.embedded.mapp.model.reviewContent
import ac.smu.embedded.mapp.util.CONFIG_USE_ABOUT_FOOD
import ac.smu.embedded.mapp.util.CONFIG_USE_ABOUT_PLACE
import ac.smu.embedded.mapp.util.CONFIG_USE_BEST_PLACE
import ac.smu.embedded.mapp.util.toNullIfEmpty
import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_write_review.*
import kotlinx.android.synthetic.main.content_write_review.*
import org.koin.android.viewmodel.ext.android.viewModel

class ReviewWriteActivity : AppCompatActivity(R.layout.activity_write_review) {

    private val reviewViewModel: ReviewViewModel by viewModel()

    private var review: Review? = null

    private lateinit var waitingTimeMap: Map<String, Int>

    private var useBestPlace = false
    private var useAboutPlace = false
    private var useAboutFood = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra(EXTRA_RESTAURANT_ID)) {
            val restaurantId = intent.getStringExtra(EXTRA_RESTAURANT_ID)!!
            var reviewId: String? = null
            if (intent.hasExtra(EXTRA_REVIEW_ID)) {
                reviewId = intent.getStringExtra(EXTRA_REVIEW_ID)
            }

            initView(restaurantId)
            setupObservers()
            setupUpdatedConfig()
            loadContent(restaurantId, reviewId)
        } else {
            throw UnsupportedOperationException(
                "Not received intent data, required EXTRA_RESTAURANT_ID"
            )
        }
    }

    private fun initView(restaurantId: String) {
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        group_recommend.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (checkedId) {
                R.id.btn_answer_yes -> {
                    if (isChecked && useBestPlace) {
                        // No-op
                    }
                }
                else -> {
                    if (isChecked && useBestPlace) {
                        // No-op
                    }
                }
            }
        }

        edit_eaten_food.addTextChangedListener {
            if (it != null && it.toString().isNotEmpty() && useAboutFood) {
                layout_about_food.visibility = View.VISIBLE
            } else {
                layout_about_food.visibility = View.GONE
            }
        }

        edit_best_place.addTextChangedListener {
            if (it != null && it.toString().isNotEmpty() && useAboutPlace) {
                layout_about_place.visibility = View.VISIBLE
            } else {
                layout_about_place.visibility = View.GONE
            }
        }

        waitingTimeMap = mapOf(
            getString(R.string.waiting_time_none) to 0,
            getString(R.string.waiting_time_10) to 10,
            getString(R.string.waiting_time_30) to 30,
            getString(R.string.waiting_time_60) to 60,
            getString(R.string.waiting_time_over) to 61,
            getString(R.string.waiting_time_dont_know) to 999
        )

        val adapter =
            ArrayAdapter(
                this,
                R.layout.dropdown_popup_item,
                waitingTimeMap.keys.toList()
            )

        waiting_time_dropdown.setAdapter(adapter)

        btn_write_review.setOnClickListener {
            if (review == null) {
                reviewViewModel.saveReview(restaurantId, null, createReviewContent())
            } else {
                reviewViewModel.saveReview(restaurantId, review!!.documentId, createReviewContent())
            }
        }
    }

    private fun setupUpdatedConfig() {
        reviewViewModel.configUpdated.observe(this, Observer {
            Logger.d("Remote config updated")
            reviewViewModel.fetchConfig()
        })
    }

    private fun setupObservers() {
        reviewViewModel.configs.observe(this, Observer {
            useBestPlace = it[CONFIG_USE_BEST_PLACE] as Boolean
            useAboutPlace = it[CONFIG_USE_ABOUT_PLACE] as Boolean
            useAboutFood = it[CONFIG_USE_ABOUT_FOOD] as Boolean

            layout_best_place.visibility = if (useBestPlace) View.VISIBLE else View.GONE
        })

        reviewViewModel.restaurant.observe(this, Observer {
            with(view_profile) {
                if (it != null) {
                    setImage(it.image)
                    setTitle(it.name)
                }
            }
        })

        reviewViewModel.review.observe(this, Observer {
            if (it != null) {
                with(it.content) {
                    when (recommendPoint) {
                        RECOMMEND_YES -> btn_answer_yes.isChecked = true
                        RECOMMEND_SOSO -> btn_answer_soso.isChecked = true
                        RECOMMEND_NO -> btn_answer_no.isChecked = true
                    }
                    rating_cost_effective.rating = costEffective?.toFloat()!!
                    rating_service_point.rating = servicePoint?.toFloat()!!
                    waiting_time_dropdown.setText(waitTimeFromInt(waitingTime!!), false)
                    edit_eaten_food.setText(eatenFood)
                    if (aboutFood != null) {
                        edit_about_food.setText(aboutFood)
                    }
                    if (bestPlace != null) {
                        edit_best_place.setText(bestPlace)
                    }
                    if (aboutPlace != null) {
                        edit_about_place.setText(aboutPlace)
                    }
                    edit_review.setText(detailAnswer)
                }
            }
            review = it
        })


        reviewViewModel.validRecommend.observe(this, Observer {
            tv_error_recommend.visibility = if (!it) View.VISIBLE else View.GONE
        })

        reviewViewModel.validWaitingTime.observe(this, Observer {
            layout_waiting_time.error = if (!it) getString(R.string.msg_req_waiting_time) else null
        })

        reviewViewModel.validEatenFood.observe(this, Observer {
            layout_eaten_food.error = if (!it) getString(R.string.msg_req_eaten_food) else null
        })

        reviewViewModel.validReviewContent.observe(this, Observer {
            layout_review_content.error =
                if (!it) getString(R.string.msg_req_review_content) else null
        })

        reviewViewModel.contentSaved.observe(this, Observer {
            if (it) {
                super.onBackPressed()
            }
        })
    }

    private fun loadContent(restaurantId: String, reviewId: String?) {
        reviewViewModel.loadRestaurant(restaurantId)
        if (reviewId != null) {
            reviewViewModel.loadReview(reviewId)
        }
    }

    private fun createReviewContent(): ReviewContent {
        return reviewContent {
            recommendPoint {
                when (group_recommend.checkedButtonId) {
                    R.id.btn_answer_yes -> RECOMMEND_YES
                    R.id.btn_answer_soso -> RECOMMEND_SOSO
                    R.id.btn_answer_no -> RECOMMEND_NO
                    else -> null
                }
            }
            costEffective(rating_cost_effective.rating.toInt())
            servicePoint(rating_service_point.rating.toInt())
            waitingTime { waitTimeFromString(waiting_time_dropdown.text.toString()) }
            eatenFood { edit_eaten_food.text.toString() }
            aboutFood { edit_about_food.text.toString().toNullIfEmpty() }
            bestPlace { edit_best_place.text.toString().toNullIfEmpty() }
            aboutPlace { edit_about_place.text.toString().toNullIfEmpty() }
            detailAnswer { edit_review.text.toString() }
        }
    }

    private fun waitTimeFromString(value: String): Int? {
        return waitingTimeMap[value]
    }

    private fun waitTimeFromInt(value: Int): String? {
        waitingTimeMap.forEach {
            if (it.value == value) {
                return it.key
            }
        }
        return null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                showExitDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        showExitDialog()
    }

    private fun showExitDialog() {
        val listener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    super.onBackPressed()
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    dialog.cancel()
                }
            }
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.subtitle_write_review))
            .setMessage(getString(R.string.msg_exit_review))
            .setNegativeButton(getString(R.string.answer_no), listener)
            .setPositiveButton(getString(R.string.answer_yes), listener)
            .show()
    }

    companion object {
        const val EXTRA_RESTAURANT_ID = "restaurant_id"
        const val EXTRA_REVIEW_ID = "review_id"

        private const val RECOMMEND_YES = 1
        private const val RECOMMEND_SOSO = 0
        private const val RECOMMEND_NO = -1
    }
}