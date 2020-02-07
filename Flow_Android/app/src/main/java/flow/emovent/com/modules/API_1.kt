package flow.emovent.com.modules
import java.security.MessageDigest
import android.graphics.Bitmap
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.annotation.RequiresApi
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.webkit.MimeTypeMap
import java.math.BigDecimal
import kotlin.math.roundToInt


fun String.toSHA512(): String {
    val bytes = this.toByteArray()
    val md = MessageDigest.getInstance("SHA-512")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}


fun String.toMD5(): String {
    val bytes = this.toByteArray()
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}


fun isNullOrEmpty(str: String?): Boolean {
    if (str != null && !str.isEmpty() && str.trim().isNotEmpty())
        return false
    return true
}

fun getWidth(activity: Activity):Int{
    val display=activity.windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size.x
}

fun getHight(activity: Activity):Int{
    val display=activity.windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size.y
}


const val BITMAP_SCALE = 0.600f
const val BLUR_RADIUS = 20.0f //max radius 25.0

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun blur(v: View): Bitmap {
    return blur(v.context, getScreenshot(v))
}

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun blur(ctx: Context, image: Bitmap): Bitmap {
    val width = (image.width * BITMAP_SCALE).roundToInt()
    val height = (image.height * BITMAP_SCALE).roundToInt()

    val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
    val outputBitmap = Bitmap.createBitmap(inputBitmap)

    val rs = RenderScript.create(ctx)
    val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
    val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
    val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
    theIntrinsic.setRadius(BLUR_RADIUS)
    theIntrinsic.setInput(tmpIn)
    theIntrinsic.forEach(tmpOut)
    tmpOut.copyTo(outputBitmap)
    return outputBitmap
}

fun getScreenshot(v: View): Bitmap {
    val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
    val c = Canvas(b)
    v.draw(c)
    return b
}

fun endNumber(num: BigDecimal):String{
    return when {
        num > 0.toBigDecimal() -> "+$num"
        num < 0.toBigDecimal() -> num.toString()
        num == 0.toBigDecimal() -> "0.0"
        else -> { Log.ERROR.toString()}
    }
}

val FADE_DURATION:Long = 500
fun setFadeAnimation(view: View) {
    val anim = AlphaAnimation(0.0f, 1.0f)
    anim.duration = FADE_DURATION
    view.startAnimation(anim)
}


//Get mimeType
fun getMimeType(path: String): String {
    val extension = MimeTypeMap.getFileExtensionFromUrl(path)
    val type=MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    return type?:"text/plain"
}

fun Any?.toStringOrEmpty() = this?.toString() ?: ""