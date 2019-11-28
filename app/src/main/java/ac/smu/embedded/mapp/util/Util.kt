package ac.smu.embedded.mapp.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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

object NaverMapUtil {

    fun startNaverMap(context: Context, lat: Double, lon: Double, name: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("nmap://place?lat=$lat&lng=$lon&name=$name")
        ).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        }
        val list =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (list.isEmpty()) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0?q=$lat,$lon($name)")
                )
            )
        } else {
            context.startActivity(intent)
        }
    }
}
