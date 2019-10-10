package ac.smu.embedded.findingtaste.main

import ac.smu.embedded.findingtaste.R
import ac.smu.embedded.findingtaste.util.getViewModelFactory
import ac.smu.embedded.findingtaste.util.showToast
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> { getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToast()
        btn_toast.setOnClickListener {
            viewModel.showToast("Toast button clicked")
        }
    }

    private fun setupToast() {
        viewModel.toastText.observe(this, Observer {
            showToast(it, Toast.LENGTH_SHORT)
        })
    }
}
