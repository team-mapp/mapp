package ac.smu.embedded.mapp.util

import ac.smu.embedded.mapp.BaseApplication
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions

/**
 * Firebase Cloud Storage 에서 이미지를 가져와 [ImageView]에 보여주는 확장 함수
 *
 * ```kotlin
 * imageView.load(context, "image.jpg")
 * imageView.load(context, "image.jpg", RequestOptions.circleCropTransform())
 * imageView.load(context, "image.jpg", null, requestListener)
 * ```
 *
 * @param context FirebaseStorage 객체를 가져오기 위한 [Context]
 * @param imageLocation Firebase Cloud Storage 내 이미지 경로
 * @param requestOptions 이미지를 [ImageView]에 보여줄 때 지정할 옵션
 * @param requestListener 이미지 결과를 [Drawable] 객체로 받을 수 있는 리스너
 */
fun ImageView.load(
    context: Context,
    imageLocation: String,
    requestOptions: RequestOptions? = null,
    requestListener: RequestListener<Drawable>? = null
) {
    val application = context.applicationContext as BaseApplication
    val reference = application.storage.getReference(imageLocation)

    var builder = Glide.with(this).load(reference)
    if (requestOptions != null) {
        builder = builder.apply(requestOptions)
    }
    if (requestListener != null) {
        builder = builder.listener(requestListener)
    }
    builder.into(this)
}

/**
 * Firebase Cloud Storage 에서 이미지를 [Bitmap]으로 가져와 [ImageView]에 보여주는 확장 함수
 *
 * ```kotlin
 * imageView.loadBitmap(context, "image.jpg")
 * imageView.loadBitmap(context, "image.jpg", RequestOptions.circleCropTransform())
 * imageView.loadBitmap(context, "image.jpg", null, requestListener)
 * ```
 *
 * @param context FirebaseStorage 객체를 가져오기 위한 [Context]
 * @param imageLocation Firebase Cloud Storage 내 이미지 경로
 * @param requestOptions 이미지를 [ImageView]에 보여줄 때 지정할 옵션
 * @param requestListener 이미지 결과를 [Bitmap] 객체로 받을 수 있는 리스너
 */
fun ImageView.loadBitmap(
    context: Context,
    imageLocation: String,
    requestOptions: RequestOptions? = null,
    requestListener: RequestListener<Bitmap>? = null
) {
    val application = context.applicationContext as BaseApplication
    val reference = application.storage.getReference(imageLocation)

    var builder = Glide.with(this).asBitmap().load(reference)
    if (requestOptions != null) {
        builder = builder.apply(requestOptions)
    }
    if (requestListener != null) {
        builder = builder.listener(requestListener)
    }
    builder.into(this)
}