package ac.smu.embedded.mapp.profile

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.common.ContentFragment
import ac.smu.embedded.mapp.review.ReviewViewActivity
import ac.smu.embedded.mapp.review.ReviewWriteActivity
import ac.smu.embedded.mapp.util.*
import android.content.Intent
import android.text.format.DateUtils
import android.view.View
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.fragment_main_content.*
import kotlinx.android.synthetic.main.item_profile_review.*
import org.koin.android.viewmodel.ext.android.viewModel

class ReviewsFragment : ContentFragment<ReviewWithRestaurant>() {
    private val profileViewModel: ProfileViewModel by viewModel()

    override fun initAdapter(): BaseRecyclerAdapter<ReviewWithRestaurant> =
        recyclerAdapter(R.layout.item_profile_review) { viewHolder, view, value ->
            val restaurant = value.restaurant
            if (restaurant != null) {
                with(restaurant) {
                    viewHolder.iv_restaurant.loadStorage(
                        image,
                        listOf(RequestOptions.circleCropTransform())
                    )
                    viewHolder.tv_restaurant_name.text = name
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

                viewHolder.group_control.addOnButtonCheckedListener { _, checkedId, isChecked ->
                    when (checkedId) {
                        R.id.btn_edit -> {
                            if (isChecked) {
                                val intent =
                                    Intent(
                                        requireContext(),
                                        ReviewWriteActivity::class.java
                                    ).apply {
                                        putExtra(
                                            ReviewWriteActivity.EXTRA_RESTAURANT_ID,
                                            restaurantId
                                        )
                                        putExtra(ReviewWriteActivity.EXTRA_REVIEW_ID, documentId)
                                    }
                                startActivity(intent)
                                viewHolder.btn_edit.isChecked = false
                            }
                        }
                        R.id.btn_delete -> {
                            if (isChecked) {
                                profileViewModel.removeReview(documentId)
                                viewHolder.btn_delete.isChecked = false
                            }
                        }
                    }
                }
            }

            view.setOnClickListener {
                val intent = Intent(requireContext(), ReviewViewActivity::class.java).apply {
                    putExtra(ReviewViewActivity.EXTRA_REVIEW_ID, value.review.documentId)
                }
                startActivity(intent)
            }
        }

    override fun loadContents() {
        profileViewModel.reviews.observe(this, Observer {
            if (it != null) {
                adapter.submitList(it)
                tv_empty_items.visibility = View.GONE
            } else {
                adapter.submitList(listOf())
                tv_empty_items.text = getString(R.string.empty_my_review_list)
                tv_empty_items.visibility = View.VISIBLE
            }
            loading_progress.visibility = View.GONE
        })

        profileViewModel.loadUserReviews()
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
}