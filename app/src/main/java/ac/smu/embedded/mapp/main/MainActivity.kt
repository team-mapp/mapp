package ac.smu.embedded.mapp.main

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.search.SearchActivity
import ac.smu.embedded.mapp.util.showToast
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        supportActionBar?.apply {
            elevation = 0.0f
            setDisplayShowTitleEnabled(false)
        }

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                view_pager.setCurrentItem(tab?.position!!, true)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // No-op
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // No-op
            }

        })

        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tab_layout.getTabAt(position)?.select()
            }
        })
        view_pager.adapter = MainPagerAdapter(this)

        search_bar_layout.setOnClickListener { navigateSearch() }
    }

    private fun navigateSearch() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(
            intent,
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                search_bar_layout,
                "search_bar"
            ).toBundle()
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_my -> {
                showToast("Not implemented", Toast.LENGTH_SHORT)
            }
        }
        return true
    }
}