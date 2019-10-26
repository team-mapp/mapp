package ac.smu.embedded.mapp.util

import ac.smu.embedded.mapp.BaseApplication
import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.load(context: Context, imageLocation: String) {
    val application = context.applicationContext as BaseApplication
    val reference = application.storage.getReference(imageLocation)
    Glide.with(this).load(reference).into(this)
}