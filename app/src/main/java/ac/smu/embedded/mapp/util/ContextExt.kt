package ac.smu.embedded.mapp.util

import ac.smu.embedded.mapp.BaseApplication
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showToast(@StringRes resId: Int, timeLength: Int) =
    showToast(this.getString(resId), timeLength)

fun Context.showToast(toastText: String, timeLength: Int) {
    Toast.makeText(this, toastText, timeLength).show()
}

fun Context.getViewModelFactory(): ViewModelFactory = ViewModelFactory(
    applicationContext as BaseApplication
)