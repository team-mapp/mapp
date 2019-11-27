package ac.smu.embedded.mapp.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProfilePagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment =
        listOf(FavoritesFragment(), ReviewsFragment())[position]

    override fun getItemCount(): Int = 2

}