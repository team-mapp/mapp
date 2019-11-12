package ac.smu.embedded.mapp.util

import com.orhanobut.logger.Logger

fun emptyFieldError(
    modelName: String,
    documentId: String,
    field: String
): Nothing {
    val errorMessage = "Empty $field field in $modelName($documentId)"
    Logger.e(errorMessage)
    throw NoSuchFieldException(errorMessage)
}