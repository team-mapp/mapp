package ac.smu.embedded.mapp.common.view

import ac.smu.embedded.mapp.R
import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.palette.graphics.Palette
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.view_content.view.*

/**
 * [MaterialCardView]를 확장한 셀럽, 프로그램, 식당의 리스트에 사용되는 뷰입니다.
 *
 * 뷰에서 사용되는 전체 내용은 setContent(imageUrl, name, isFavorite, visibleFavorite, imageFromStorage)
 * 를 이용하여 설정할 수 있습니다.
 *
 * 일부 내용에 데이터를 설정하고 싶을 경우 아래의 메소드들을 이용하세요.
 *
 * setImage(imageUrl, imageFromStorage)
 * setName(name)
 * setVisibleFavorite(isFavorite, visibleFavorite)
 *
 * 뷰에 있는 즐겨찾기 버튼은 아래의 메소드를 통해 리스너를 등록할 수 있습니다.
 *
 * setOnFavoriteClickListener(listener: (v: View, isFavorite: Boolean) -> Unit)
 * setOnFavoriteClickListener(listener: OnFavoriteClickListener)
 *
 * @sample
 * ```xml
 * <?xml version="1.0" encoding="utf-8"?>
 *   <ac.smu.embedded.mapp.common.view.ContentView
 *      xmlns:android="http://schemas.android.com/apk/res/android"
 *      android:id="@+id/content_view"
 *      android:layout_width="match_parent"
 *      android:layout_height="wrap_content" />
 * ```
 *
 * ```kotlin
 * content_view.setContent(imageUrl, name, true)
 * content_view.setOnFavoriteClickListener { view, isFavorite ->
 *   content_view.isFavorite = !isFavorite
 * }
 * ```
 *
 * *이 뷰는 [ProfileView]를 내부에서 사용하고 있습니다. 이미지 및 타이틀 설정에 대해서는 이 클래스를 확인하세요*
 *
 * @property isFavorite 콘텐츠의 즐겨찾기 여부를 지정하여 즐겨찾기 버튼의 색상을 업데이트합니다
 */
class ContentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val imageRequestListener = object : RequestListener<Bitmap> {
        override fun onResourceReady(
            resource: Bitmap?,
            model: Any?,
            target: Target<Bitmap>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            if (resource != null) {
                val palette = Palette.from(resource).generate()
                val backgroundColor =
                    palette.getDarkMutedColor(
                        getColor(
                            context,
                            R.color.colorSecondary
                        )
                    )
                setCardBackgroundColor(backgroundColor)
            }
            return false
        }

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Bitmap>?,
            isFirstResource: Boolean
        ): Boolean {
            return e != null
        }
    }

    private lateinit var favoriteClickListener: OnFavoriteClickListener

    var isFavorite: Boolean = false
        set(value) {
            field = value
            updateFavorite()
        }

    init {
        radius = context.resources.getDimension(R.dimen.card_content_radius)

        inflate(context, R.layout.view_content, this)
        initView()
    }

    fun setImage(imageUrl: String, isStorage: Boolean) {
        view_profile.setImage(imageUrl, isStorage, imageRequestListener)
    }

    fun setTitle(name: String) {
        view_profile.setTitle(name)
    }

    fun setVisibleFavorite(isVisible: Boolean) {
        btn_favorite.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    fun setOnFavoriteClickListener(listener: (v: View, isFavorite: Boolean) -> Unit) {
        favoriteClickListener = object : OnFavoriteClickListener {
            override fun onClick(v: View, isFavorite: Boolean) {
                listener(v, isFavorite)
            }
        }
    }

    fun setOnFavoriteClickListener(listener: OnFavoriteClickListener) {
        favoriteClickListener = listener
    }

    fun setContent(
        imageUrl: String,
        name: String,
        isFavorite: Boolean = false,
        visibleFavorite: Boolean = false,
        imageFromStorage: Boolean = true
    ) {
        setImage(imageUrl, imageFromStorage)
        setTitle(name)
        setVisibleFavorite(isFavorite || visibleFavorite)
        this.isFavorite = isFavorite
        updateFavorite()
    }

    private fun initView() {
        setCardBackgroundColor(getColor(context, R.color.colorAccent))
        btn_favorite.setOnClickListener {
            if (::favoriteClickListener.isInitialized) {
                favoriteClickListener.onClick(it, isFavorite)
            }
        }
    }

    private fun updateFavorite() {
        if (isFavorite) {
            btn_favorite.setColorFilter(getColor(context, R.color.content_favorite_color))
        } else {
            btn_favorite.setColorFilter(getColor(context, android.R.color.white))
        }
    }

    interface OnFavoriteClickListener {

        fun onClick(v: View, isFavorite: Boolean)

    }
}