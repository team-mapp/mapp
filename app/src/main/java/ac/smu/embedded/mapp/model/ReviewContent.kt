package ac.smu.embedded.mapp.model

import androidx.annotation.IntRange

/**
 * 리뷰 내용을 저장하는 데이터 클래스
 *
 * @property eatenFood 먹은 음식명
 * @property aboutFood 먹은 음식에 대한 평가
 * @property recommendPoint 추천 점수 (0 - 5)
 * @property costEffective 가성비 (0 - 5)
 * @property servicePoint 서비스 평가 (0 - 5)
 * @property waitingTime 대기 시간 (분 단위로 작성)
 * @property bestPlace 명당 자리
 * @property aboutPlace 명당 자리에 대한 평가
 * @property detailQuestion 랜덤으로 제공되는 상세 질문
 * @property detailAnswer 상세 질문에 대한 답변
 */
data class ReviewContent(
    val eatenFood: String?,
    val aboutFood: String?,
    val recommendPoint: Int?,
    @IntRange(from = 0, to = 5) val costEffective: Int?,
    @IntRange(from = 0, to = 5) val servicePoint: Int?,
    val waitingTime: Int?,
    val bestPlace: String?,
    val aboutPlace: String?,
    val detailQuestion: String?,
    val detailAnswer: String?
) {
    companion object {
        const val FIELD_EATEN_FOOD = "eatenFood"
        const val FIELD_ABOUT_FOOD = "aboutFood"
        const val FIELD_RECOMMEND_POINT = "recommendPoint"
        const val FIELD_COST_EFFECTIVE = "costEffective"
        const val FIELD_SERVICE_POINT = "servicePoint"
        const val FIELD_WAITING_TIME = "waitingTime"
        const val FIELD_BEST_PLACE = "bestPlace"
        const val FIELD_ABOUT_PLACE = "aboutPlace"
        const val FIELD_DETAIL_QUESTION = "detailQuestion"
        const val FIELD_DETAIL_ANSWER = "detailAnswer"

        fun fromMap(map: Map<String, Any>): ReviewContent {
            return reviewContent {
                eatenFood { map[FIELD_EATEN_FOOD] as String? }
                aboutFood { map[FIELD_ABOUT_FOOD] as String? }
                recommendPoint { (map[FIELD_RECOMMEND_POINT] as Long?)?.toInt() }
                costEffective { (map[FIELD_COST_EFFECTIVE] as Long?)?.toInt() }
                servicePoint { (map[FIELD_SERVICE_POINT] as Long?)?.toInt() }
                waitingTime { (map[FIELD_WAITING_TIME] as Long?)?.toInt() }
                bestPlace { map[FIELD_BEST_PLACE] as String? }
                aboutPlace { map[FIELD_ABOUT_PLACE] as String? }
                detailQuestion { map[FIELD_DETAIL_QUESTION] as String? }
                detailAnswer { map[FIELD_DETAIL_ANSWER] as String? }
            }
        }
    }
}

@DslMarker
annotation class BuilderDsl

@BuilderDsl
class ReviewContentBuilder() {
    constructor(content: ReviewContent) : this() {
        eatenFood = content.eatenFood
        aboutFood = content.aboutFood
        recommendPoint = content.recommendPoint
        costEffective = content.costEffective
        servicePoint = content.servicePoint
        waitingTime = content.waitingTime
        bestPlace = content.bestPlace
        aboutPlace = content.aboutPlace
        detailQuestion = content.detailQuestion
        detailAnswer = content.detailAnswer
    }

    private var eatenFood: String? = null
    private var aboutFood: String? = null
    private var recommendPoint: Int? = null
    private var costEffective: Int? = null
    private var servicePoint: Int? = null
    private var waitingTime: Int? = null
    private var bestPlace: String? = null
    private var aboutPlace: String? = null
    private var detailQuestion: String? = null
    private var detailAnswer: String? = null

    fun eatenFood(lambda: () -> String?) {
        this.eatenFood = lambda()
    }

    fun aboutFood(lambda: () -> String?) {
        this.aboutFood = lambda()
    }

    fun recommendPoint(@IntRange(from = 0, to = 5) value: Int) {
        this.recommendPoint = value
    }

    fun recommendPoint(lambda: () -> Int?) {
        this.recommendPoint = lambda()
    }

    fun costEffective(@IntRange(from = 0, to = 5) value: Int) {
        this.costEffective = value
    }

    fun costEffective(lambda: () -> Int?) {
        this.costEffective = lambda()
    }

    fun servicePoint(@IntRange(from = 0, to = 5) value: Int) {
        this.servicePoint = value
    }

    fun servicePoint(lambda: () -> Int?) {
        this.servicePoint = lambda()
    }

    fun waitingTime(value: Int) {
        this.waitingTime = value
    }

    fun waitingTime(lambda: () -> Int?) {
        this.waitingTime = lambda()
    }

    fun bestPlace(lambda: () -> String?) {
        this.bestPlace = lambda()
    }

    fun aboutPlace(lambda: () -> String?) {
        this.aboutPlace = lambda()
    }

    fun detailQuestion(lambda: () -> String?) {
        this.detailQuestion = lambda()
    }

    fun detailAnswer(lambda: () -> String?) {
        this.detailAnswer = lambda()
    }

    fun build() = ReviewContent(
        eatenFood,
        aboutFood,
        recommendPoint,
        costEffective,
        servicePoint,
        waitingTime,
        bestPlace,
        aboutPlace,
        detailQuestion,
        detailAnswer
    )
}

fun reviewContent(lambda: ReviewContentBuilder.() -> Unit) =
    ReviewContentBuilder().apply(lambda).build()

fun reviewContent(content: ReviewContent, lambda: ReviewContentBuilder.() -> Unit) =
    ReviewContentBuilder(content).apply(lambda).build()