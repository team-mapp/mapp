package ac.smu.embedded.mapp.main

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.detail.DetailActivity
import ac.smu.embedded.mapp.model.Program
import ac.smu.embedded.mapp.util.BaseRecyclerAdapter
import ac.smu.embedded.mapp.util.loadStorage
import ac.smu.embedded.mapp.util.recyclerAdapter
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_main_content.*
import kotlinx.android.synthetic.main.item_content_card.view.*

class ProgramsFragment : ContentFragment<Program>() {
    override fun initAdapter(): BaseRecyclerAdapter<Program> =
        recyclerAdapter(R.layout.item_content_card) { view, value ->
            view.iv_content.loadStorage(value.image)
            view.tv_content.text = value.name
            view.setOnClickListener {
                navigateDetail(DetailActivity.TYPE_PROGRAM, value.documentId)
            }
        }

    override fun loadContents() {
        mainViewModel.loadPrograms().observe(this, Observer { resource ->
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