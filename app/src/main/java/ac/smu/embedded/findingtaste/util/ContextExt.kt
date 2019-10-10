package ac.smu.embedded.findingtaste.util

import ac.smu.embedded.findingtaste.ViewModelFactory
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showToast(@StringRes resId: Int, timeLength: Int) =
    showToast(this.getString(resId), timeLength)

fun Context.showToast(toastText: String, timeLength: Int) {
    Toast.makeText(this, toastText, timeLength).show()
}

fun Context.getViewModelFactory(): ViewModelFactory {
    return ViewModelFactory()
}