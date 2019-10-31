package ac.smu.embedded.mapp.restaurantDetail

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.util.BaseRecyclerAdapter
import ac.smu.embedded.mapp.util.TypedItem
import ac.smu.embedded.mapp.util.recyclerAdapter
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_restaurantdetail.*
import kotlinx.android.synthetic.main.item_restaurant_review.view.*

class RestaurantDetailActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurantdetail)

        var reviewList = arrayListOf<ReviewList>(
            ReviewList(null, "gaeun","gaeun review"),
            ReviewList(null,"gaeun2","gaeun2 review2"),
            ReviewList(null,"gaeun3","gaeun2 review3"),
            ReviewList(null,"gaeun4","gaeun2 review4"),
            ReviewList(null,"gaeun5","gaeun2 review5"),
            ReviewList(null,"gaeun6","gaeun2 review6")
            )

        // adapter 설정
        val mAdapter = recyclerAdapter<ReviewList>(R.layout.item_restaurant_review, reviewList){
            view,value -> view.tvUserName.text = value.userName
            view.tvUserReview.text = value.reviewContent
        }

        rvReviewList.adapter = mAdapter

        // 레이아웃 매니저 설정
        rvReviewList.layoutManager = LinearLayoutManager(this)
        rvReviewList.setHasFixedSize(true)

    }
}