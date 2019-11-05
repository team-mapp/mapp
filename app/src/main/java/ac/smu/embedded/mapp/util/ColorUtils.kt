package ac.smu.embedded.mapp.util

import android.graphics.Color
import kotlin.math.min

object ColorUtils {
    fun manipulateColor(color: Int, factor: Float): Int {
        val a = Color.alpha(color);
        val r = Math.round(Color.red(color) * factor);
        val g = Math.round(Color.green(color) * factor);
        val b = Math.round(Color.blue(color) * factor);
        return Color.argb(
            a,
            min(r, 255),
            min(g, 255),
            min(b, 255)
        );
    }
}