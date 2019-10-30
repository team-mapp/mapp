package ac.smu.embedded.mapp.main

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.util.*
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main_content.*
import kotlinx.android.synthetic.main.item_content_card.view.*

class CelebsFragment : Fragment(R.layout.fragment_main_content) {

    private val viewModel by activityViewModels<MainViewModel> { requireActivity().getViewModelFactory() }
    private lateinit var adapter: BaseRecyclerAdapter<Celeb>

    override fun onStart() {
        super.onStart()
        initView()

        viewModel.loadCelebs().observe(this, Observer { resource ->
            resource.onSuccess {
                loading_progress.visibility = View.GONE
                adapter.replaceItems(it!!)
            }.onError {
                loading_progress.visibility = View.GONE
                it.printStackTrace()
            }.onLoading {
                loading_progress.visibility = View.VISIBLE
            }
        })
    }

    private fun initView() {
        adapter =
            recyclerAdapter(R.layout.item_content_card, mutableListOf()) { view, value ->
                view.iv_content.load(requireContext(), value.image)
                view.tv_content.text = value.name
            }

        val marginDimen = resources.getDimension(R.dimen.keyline_small).toInt()

        content_view.layoutManager = LinearLayoutManager(requireContext())
        content_view.addItemDecoration(MarginItemDecoration(marginBottom = marginDimen))
        content_view.adapter = adapter
    }
}