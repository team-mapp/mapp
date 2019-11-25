package ac.smu.embedded.mapp.common.view

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.util.load
import ac.smu.embedded.mapp.util.loadAsBitmap
import ac.smu.embedded.mapp.util.loadStorageAsBitmap
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.view_profile.view.*

/**
 * [ConstraintLayout]를 확장한 프로필을 표시하기 위한 뷰입니다.
 *
 * 프로필의 내용은 아래의 메소드를 통해 설정할 수 있습니다.
 *
 * setImage(drawable)
 * setImage(imageUrl, isStorage, requestListener)
 * setName(name)
 *
 * @sample
 * ```xml
 * <ac.smu.embedded.mapp.common.view.ProfileView
 *   android:id="@+id/view_profile"
 *   android:layout_width="wrap_content"
 *   android:layout_height="wrap_content"
 *   />
 * ```
 *
 * ```kotlin
 * val url = "http://....jpg"
 * view_profile.setImage(url, false)
 * view_profile.setName(name)
 *
 * val storageUrl = "gs:~~~.jpg"
 * view_profile.setImage(storageUrl)
 * ```
 */
class ProfileView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val imageRequestOptions = listOf(RequestOptions.circleCropTransform())

    private var title: String? = null
    private var titleTextColor = 0
    private var subtitleTextColor = 0
    private var subtitle: String? = null
    private var image: Drawable? = null
    private var imageTintColor = 0

    init {
        inflate(context, R.layout.view_profile, this)
        context.withStyledAttributes(attrs, R.styleable.ProfileView) {
            title = getString(R.styleable.ProfileView_title)
            titleTextColor = getColor(R.styleable.ProfileView_titleTextColor, 0)
            subtitle = getString(R.styleable.ProfileView_subtitle)
            subtitleTextColor = getColor(R.styleable.ProfileView_subtitleTextColor, 0)
            image = getDrawable(R.styleable.ProfileView_image)
            imageTintColor = getColor(R.styleable.ProfileView_imageTint, 0)
        }

        if (title != null) {
            setTitle(title!!)
        }
        if (titleTextColor != 0) {
            tv_title.setTextColor(titleTextColor)
        }
        if (subtitle != null) {
            setSubtitle(subtitle!!)
        }
        if (subtitleTextColor != 0) {
            tv_title.setTextColor(subtitleTextColor)
        }
        if (image != null) {
            setImage(image!!)
        }
        if (imageTintColor != 0) {
            iv_content.setColorFilter(imageTintColor)
        }
    }

    fun setImage(drawable: Drawable) {
        iv_content.load(drawable, imageRequestOptions)
    }

    fun setImage(
        imageUrl: String,
        isStorage: Boolean = true,
        requestListener: RequestListener<Bitmap>? = null
    ) {
        iv_content.clearColorFilter()
        if (isStorage) {
            iv_content.loadStorageAsBitmap(imageUrl, imageRequestOptions, requestListener)
        } else {
            iv_content.loadAsBitmap(imageUrl, imageRequestOptions, requestListener)
        }
    }

    @Deprecated(message = "setName 대신에 setTitle을 사용해주세요")
    fun setName(name: String) {
        tv_title.text = name
    }

    fun setTitle(title: String) {
        tv_title.text = title
    }

    fun setSubtitle(subtitle: String) {
        tv_subtitle.text = subtitle
        tv_subtitle.visibility = View.VISIBLE
    }
}