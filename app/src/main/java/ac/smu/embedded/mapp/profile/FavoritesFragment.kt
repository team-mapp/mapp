package ac.smu.embedded.mapp.profile

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.common.ContentFragment
import ac.smu.embedded.mapp.common.view.ContentView
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.restaurantDetail.RestaurantDetailActivity
import ac.smu.embedded.mapp.util.BaseRecyclerAdapter
import ac.smu.embedded.mapp.util.EXTRA_DOCUMENT_ID
import ac.smu.embedded.mapp.util.recyclerAdapter
import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_main_content.*
import org.koin.android.viewmodel.ext.android.viewModel

class FavoritesFragment : ContentFragment<Restaurant>() {
    private val profileViewModel: ProfileViewModel by viewModel()

    override fun initAdapter(): BaseRecyclerAdapter<Restaurant> =
        recyclerAdapter(R.layout.item_content) { _, view, value ->
            val contentView = view as ContentView
            with(value) {
                contentView.setContent(image, name)
                contentView.setOnClickListener {
                    val intent =
                        Intent(requireContext(), RestaurantDetailActivity::class.java).apply {
                            putExtra(EXTRA_DOCUMENT_ID, documentId)
                        }
                    startActivity(intent)
                }
            }
        }

    override fun loadContents() {
        profileViewModel.favorites.observe(this, Observer {
            if (it != null) {
                profileViewModel.loadFavoriteRestaurants(it)
                tv_empty_items.visibility = View.GONE
            } else {
                adapter.submitList(listOf())
                tv_empty_items.text = getString(R.string.empty_favorite_list)
                tv_empty_items.visibility = View.VISIBLE
                loading_progress.visibility = View.GONE
            }
        })

        profileViewModel.favoriteRestaurants.observe(this, Observer {
            adapter.submitList(it)
            loading_progress.visibility = View.GONE
        })

        profileViewModel.loadFavorites()
    }
}