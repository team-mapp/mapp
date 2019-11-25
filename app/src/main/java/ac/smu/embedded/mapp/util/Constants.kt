package ac.smu.embedded.mapp.util

const val EXTRA_FROM_INTRO = "from_intro"
const val EXTRA_DOCUMENT_ID = "document_id"

const val CONFIG_USE_BEST_PLACE = "use_best_place"

/**
 * Firebase remote config 에 사용할 필드들의 기본값을 정의합니다
 */
val remoteConfigs = mapOf(
    CONFIG_USE_BEST_PLACE to true
)

object NotificationConstants {

    const val TYPE_REVIEW_CREATED = "review_created"

}