package ac.smu.embedded.mapp.main

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.model.Status
import ac.smu.embedded.mapp.util.TypedItem
import ac.smu.embedded.mapp.util.getViewModelFactory
import ac.smu.embedded.mapp.util.recyclerAdapter
import ac.smu.embedded.mapp.util.showToast
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_header.view.*
import kotlinx.android.synthetic.main.item_test.view.*

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> { getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToast()

        val adapter =
            recyclerAdapter(
                mapOf(
                    TYPE_HEADER to R.layout.item_header,
                    TYPE_ITEM to R.layout.item_test
                ), mutableListOf(
                    TypedItem(TYPE_HEADER, "Test")
                )
            ) { view, item ->
                if (item.type == TYPE_HEADER) {
                    view.tv_header.text = item.item
                } else if (item.type == TYPE_ITEM) {
                    view.tv_title.text = item.item
                }
            }

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = adapter

        btn_toast.setOnClickListener {
            viewModel.showToast("Toast button clicked")
            adapter.addItem(TypedItem(TYPE_ITEM, "Hello"))
        }

        viewModel.getTestLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    for (document in it.data?.documents!!) {
                        Log.d(TAG, "(LiveData:Task) ${document.data.toString()}")
                    }
                }
                Status.ERROR -> {
                    Log.e(TAG, "(LiveData:Task) Error occurred (${it.error})")
                }
                Status.LOADING -> {
                    Log.d(TAG, "(LiveData:Task) Loading test data...")
                }
            }
        })

        var disposable = viewModel.getTestSingle().subscribe { snapshot, exception ->
            if (exception == null) {
                for (document in snapshot?.documents!!) {
                    Log.d(TAG, "(Single:Task) ${document.data.toString()}")
                }
            } else {
                Log.d(TAG, "(Single:Task) Error occurred ($exception)")
            }
        }

        viewModel.getTestSnapshotLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    for (document in it.data?.documents!!) {
                        Log.d(TAG, "(LiveData:Snapshot) ${document.data.toString()}")
                    }
                }
                Status.ERROR -> {
                    Log.e(TAG, "(LiveData:Snapshot) Error occurred (${it.error})")
                }
                Status.LOADING -> {
                    Log.d(TAG, "(LiveData:Snapshot) Loading test data...")
                }
            }
        })

        disposable = viewModel.getTestSnapshotFlowable().subscribe {
            when (it.status) {
                Status.SUCCESS -> {
                    for (document in it.data?.documents!!) {
                        Log.d(TAG, "(Flowable:Snapshot) ${document.data.toString()}")
                    }
                }
                Status.ERROR -> {
                    Log.e(TAG, "(Flowable:Snapshot) Error occurred (${it.error})")
                }
                Status.LOADING -> {
                    Log.d(TAG, "(Flowable:Snapshot) Loading test data...")
                }
            }
        }
    }

    private fun setupToast() {
        viewModel.toastText.observe(this, Observer {
            showToast(it, Toast.LENGTH_SHORT)
        })
    }

    companion object {
        const val TYPE_HEADER = 100
        const val TYPE_ITEM = 101
        const val TAG = "MainActivity"
    }
}
