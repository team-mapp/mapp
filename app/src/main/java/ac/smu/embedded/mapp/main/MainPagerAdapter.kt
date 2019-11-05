package ac.smu.embedded.mapp.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment =
        listOf(CelebsFragment(), ProgramsFragment())[position]

    override fun getItemCount(): Int = 2

}