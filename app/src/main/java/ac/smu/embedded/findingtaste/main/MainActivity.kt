package ac.smu.embedded.findingtaste.main

import ac.smu.embedded.findingtaste.R
import ac.smu.embedded.findingtaste.util.BaseRecyclerAdapter
import ac.smu.embedded.findingtaste.util.getViewModelFactory
import ac.smu.embedded.findingtaste.util.recyclerAdapter
import ac.smu.embedded.findingtaste.util.showToast
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_test.view.*

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> { getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToast()

        btn_toast.setOnClickListener {
            viewModel.showToast("Toast button clicked")
            (recycler_view.adapter as BaseRecyclerAdapter<String>).addItem("Hello")
        }

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter =
            recyclerAdapter(
                R.layout.item_test,
                mutableListOf<String>()
            ) { view, string ->
                view.tv_title.text = string
            }
    }

    private fun setupToast() {
        viewModel.toastText.observe(this, Observer {
            showToast(it, Toast.LENGTH_SHORT)
        })
    }
}
