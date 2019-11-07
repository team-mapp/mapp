package ac.smu.embedded.mapp.common.view

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.util.loadAsBitmap
import ac.smu.embedded.mapp.util.loadStorageAsBitmap
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
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

    private var imageTintColor = 0
    private var nameTextColor = 0
    private var defaultImageDrawable: Drawable? = null
    private var defaultName: String? = null

    init {
        inflate(context, R.layout.view_profile, this)
        context.withStyledAttributes(attrs, R.styleable.ProfileView) {
            imageTintColor = getColor(R.styleable.ProfileView_imageTint, 0)
            nameTextColor = getColor(R.styleable.ProfileView_textColor, 0)
            defaultImageDrawable = getDrawable(R.styleable.ProfileView_image)
            defaultName = getString(R.styleable.ProfileView_name)
        }

        if (imageTintColor != 0) {
            iv_content.setColorFilter(imageTintColor)
        }

        if (nameTextColor != 0) {
            tv_content.setTextColor(nameTextColor)
        }

        if (defaultImageDrawable != null) {
            setImage(defaultImageDrawable!!)
        }

        if (defaultName != null) {
            setName(defaultName!!)
        }
    }

    fun setImage(drawable: Drawable) {
        iv_content.setImageDrawable(drawable)
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

    fun setName(name: String) {
        tv_content.text = name
    }
}