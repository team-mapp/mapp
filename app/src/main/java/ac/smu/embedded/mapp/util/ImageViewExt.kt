package ac.smu.embedded.mapp.util

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

/**
 * [Glide]를 통해 [ImageView]에 여러 소스를 통해 이미지를 보여주는 확장 함수
 *
 * ```kotlin
 * imageView.load(context, "image.jpg")
 * imageView.load(context, "image.jpg", listOf(RequestOptions.circleCropTransform()))
 * imageView.load(context, "image.jpg", null, requestListener)
 * ```
 *
 * @param imageSource [Glide]에서 지원하는 이미지 소스
 * @param requestOptionsList 이미지를 보여줄때 정하는 옵션 리스트
 * @param requestListener 이미지 로드 시 [Drawable] 객체를 얻기 위한 리스너
 */
fun ImageView.load(
    imageSource: Any,
    requestOptionsList: List<RequestOptions>? = null,
    requestListener: RequestListener<Drawable>? = null
) {
    var builder = Glide.with(this)
        .load(imageSource)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

    requestOptionsList?.forEach {
        builder = builder.apply(it)
    }

    if (requestListener != null) {
        builder = builder.listener(requestListener)
    }

    builder.into(this)
}

/**
 * [Glide]를 통해 [ImageView]에 여러 소스를 통해 [Bitmap]으로 가져와 이미지를 보여주는 확장 함수
 *
 * ```kotlin
 * imageView.loadAsBitmap(context, "image.jpg")
 * imageView.loadAsBitmap(context, "image.jpg", listOf(RequestOptions.circleCropTransform()))
 * imageView.loadAsBitmap(context, "image.jpg", null, requestListener)
 * ```
 *
 * @param imageSource [Glide]에서 지원하는 이미지 소스
 * @param requestOptionsList 이미지를 보여줄때 정하는 옵션 리스트
 * @param requestListener 이미지 로드 시 [Bitmap] 객체를 얻기 위한 리스너
 */
fun ImageView.loadAsBitmap(
    imageSource: Any,
    requestOptionsList: List<RequestOptions>? = null,
    requestListener: RequestListener<Bitmap>? = null
) {
    var builder = Glide.with(this)
        .asBitmap()
        .load(imageSource)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

    requestOptionsList?.forEach {
        builder = builder.apply(it)
    }

    if (requestListener != null) {
        builder = builder.listener(requestListener)
    }

    builder.into(this)
}

/**
 * Firebase Cloud Storage 에서 이미지를 가져와 [ImageView]에 보여주는 확장 함수
 *
 * ```kotlin
 * imageView.loadStorage(context, "image.jpg")
 * imageView.loadStorage(context, "image.jpg", listOf(RequestOptions.circleCropTransform()))
 * imageView.loadStorage(context, "image.jpg", null, requestListener)
 * ```
 *
 * @param imageLocation Firebase cloud storage 내 이미지 경로
 * @param requestOptionsList 이미지를 보여줄때 정하는 옵션 리스트
 * @param requestListener 이미지 로드 시 [Drawable] 객체를 얻기 위한 리스너
 */
fun ImageView.loadStorage(
    imageLocation: String,
    requestOptionsList: List<RequestOptions>? = null,
    requestListener: RequestListener<Drawable>? = null
) = load(fetchImageReference(imageLocation), requestOptionsList, requestListener)

/**
 * Firebase Cloud Storage 에서 이미지를 [Bitmap]으로 가져와 [ImageView]에 보여주는 확장 함수
 *
 * ```kotlin
 * imageView.loadStorageAsBitmap(context, "image.jpg")
 * imageView.loadStorageAsBitmap(context, "image.jpg", listOf(RequestOptions.circleCropTransform()))
 * imageView.loadStorageAsBitmap(context, "image.jpg", null, requestListener)
 * ```
 *
 * @param imageLocation Firebase cloud storage 내 이미지 경로
 * @param requestOptionsList 이미지를 보여줄때 정하는 옵션 리스트
 * @param requestListener 이미지 로드 시 [Bitmap] 객체를 얻기 위한 리스너
 */
fun ImageView.loadStorageAsBitmap(
    imageLocation: String,
    requestOptionsList: List<RequestOptions>? = null,
    requestListener: RequestListener<Bitmap>? = null
) = loadAsBitmap(fetchImageReference(imageLocation), requestOptionsList, requestListener)

private fun fetchImageReference(imageLocation: String): StorageReference =
    Firebase.storage.getReference(imageLocation)