package ac.smu.embedded.mapp.main

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.detail.DetailActivity
import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.util.BaseRecyclerAdapter
import ac.smu.embedded.mapp.util.load
import ac.smu.embedded.mapp.util.recyclerAdapter
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_main_content.*
import kotlinx.android.synthetic.main.item_content_card.view.*

class CelebsFragment : ContentFragment<Celeb>() {
    override fun initAdapter(): BaseRecyclerAdapter<Celeb> =
        recyclerAdapter(R.layout.item_content_card) { view, value ->
            view.iv_content.load(value.image)
            view.tv_content.text = value.name
            view.setOnClickListener {
                navigateDetail(DetailActivity.TYPE_CELEB, value.documentId)
            }
        }

    override fun loadContents() {
        mainViewModel.loadCelebs().observe(this, Observer { resource ->
            resource.onSuccess {
                loading_progress.visibility = View.GONE
                adapter.submitList(it!!)
            }.onError {
                loading_progress.visibility = View.GONE
                it.printStackTrace()
            }.onLoading {
                loading_progress.visibility = View.VISIBLE
            }
        })
    }
}