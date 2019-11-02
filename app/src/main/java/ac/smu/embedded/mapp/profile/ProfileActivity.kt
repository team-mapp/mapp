package ac.smu.embedded.mapp.profile

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.util.recyclerAdapter
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.item_favorites.view.*


class ProfileActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        var favoritesList = arrayListOf<FavoritesList>(
            FavoritesList("맛집1"),
            FavoritesList("맛집2"),
            FavoritesList("맛집3"),
            FavoritesList("맛집4")
        )

        val mAdapter = recyclerAdapter<FavoritesList>(R.layout.item_favorites, favoritesList) {
            view, favoritesList ->  view.restaurantName
        }

        rvFavoritesList.adapter = mAdapter
        rvFavoritesList.layoutManager = LinearLayoutManager(this)
        rvFavoritesList.setHasFixedSize(true)

    }
}