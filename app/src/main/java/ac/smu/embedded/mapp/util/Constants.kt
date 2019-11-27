package ac.smu.embedded.mapp.util

const val RECOMMEND_YES = 1
const val RECOMMEND_SOSO = 0
const val RECOMMEND_NO = -1

const val EXTRA_FROM_INTRO = "from_intro"
const val EXTRA_DOCUMENT_ID = "document_id"

const val CONFIG_USE_BEST_PLACE = "use_best_place"
const val CONFIG_USE_ABOUT_PLACE = "use_about_place"
const val CONFIG_USE_ABOUT_FOOD = "use_about_food"

/**
 * Firebase remote config 에 사용할 필드들의 기본값을 정의합니다
 */
val remoteConfigs = mapOf(
    CONFIG_USE_BEST_PLACE to false,
    CONFIG_USE_ABOUT_PLACE to false,
    CONFIG_USE_ABOUT_FOOD to false
)

object NotificationConstants {

    const val TYPE_REVIEW_CREATED = "review_created"

}