package ac.smu.embedded.mapp.main

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.detail.DetailActivity
import ac.smu.embedded.mapp.util.BaseRecyclerAdapter
import ac.smu.embedded.mapp.util.MarginItemDecoration
import ac.smu.embedded.mapp.util.getViewModelFactory
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main_content.*

abstract class ContentFragment<T> : Fragment(R.layout.fragment_main_content) {

    protected val viewModel by activityViewModels<MainViewModel> { requireActivity().getViewModelFactory() }
    protected lateinit var adapter: BaseRecyclerAdapter<T>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = initAdapter()
        initView()
        loadContents()
    }

    private fun initView() {
        val marginDimen = resources.getDimension(R.dimen.keyline_small).toInt()
        content_view.layoutManager = LinearLayoutManager(requireContext())
        content_view.addItemDecoration(MarginItemDecoration(marginBottom = marginDimen))
        content_view.adapter = adapter
    }

    protected fun navigateDetail(type: Int, documentId: String) {
        val intent = Intent(requireActivity(), DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_DATA_TYPE, type)
        intent.putExtra(DetailActivity.EXTRA_DOCUMENT_ID, documentId)
        startActivity(intent)
    }

    abstract fun initAdapter(): BaseRecyclerAdapter<T>

    abstract fun loadContents(): Unit
}