package ac.smu.embedded.mapp.main

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.common.view.ContentView
import ac.smu.embedded.mapp.detail.DetailActivity
import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.util.BaseRecyclerAdapter
import ac.smu.embedded.mapp.util.recyclerAdapter
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_detail.*

class CelebsFragment : ContentFragment<Celeb>() {
    override fun initAdapter(): BaseRecyclerAdapter<Celeb> =
        recyclerAdapter(R.layout.item_content) { _, view, value ->
            val contentView = view as ContentView
            contentView.setContent(value.image, value.name)
            contentView.setOnClickListener {
                navigateDetail(
                    DetailActivity.TYPE_CELEB,
                    value.documentId
                )
            }
        }

    override fun loadContents() {
        mainViewModel.loadCelebs().observe(this, Observer {
            loading_progress.visibility = View.GONE
            adapter.submitList(it)
        })
    }
}