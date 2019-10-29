package ac.smu.embedded.mapp.util

import ac.smu.embedded.mapp.BaseApplication
import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

/**
 * Firebase Cloud Storage 에서 이미지를 가져와 [ImageView]에 보여주는 확장 함수
 *
 * @param context
 * @param imageLocation Firebase Cloud Storage 내 이미지 경로
 */
fun ImageView.load(context: Context, imageLocation: String) {
    val application = context.applicationContext as BaseApplication
    val reference = application.storage.getReference(imageLocation)
    Glide.with(this).load(reference).into(this)
}