package cab.snapp.carpool.passenger.util.typography

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.widget.TextView

/**
 * @author Hamidreza Hosseinkhani
 * hosseinkhani@live.com
 *
 * This class helps you to set more that one typeface to a TextView.
 * By default, I declared two syntax (English & Arabic) and set the fonts to TextView based on the syntax.
 * Developers must instantiate this class by passing the fonts path from "Assets" directory and call "performTypeface" method.
 * Notice that this method is expensive and may take some mili-seconds to appear the text in the TextView, since performing a task on the text,
 * must posted to the end of the View looper queue.
 * This class is using "Calligraphy" library, then import it ASAP. (https://github.com/chrisjenx/Calligraphy)
 *
 * You can add or modify scripts by overriding the method "getSyntax".
 */
class MultiTypefaceHelper(context: Context, englishFontPath: String, arabicFontPath: String) {

    private val mEnglishTypeface: Typeface?
    private val mArabicTypeface: Typeface?

    init {
        this.mEnglishTypeface = TypefaceUtils.load(context.assets, englishFontPath)
        this.mArabicTypeface = TypefaceUtils.load(context.assets, arabicFontPath)
    }

    @JvmOverloads
    fun performTypeface(tv: TextView?, text: String?, forceRtl: Boolean = true) {
        var text = text
        if (tv == null || text == null || text.length == 0) {
            return
        }
        if (forceRtl) {
            text = '\u200f' + text
            text = text.replace("\r\n", "\r\n\u200f")
        }
        val stringBuilder = SpannableStringBuilder(text)
        var startIndex = 0
        var index = 1
        var currentSyntax = getSyntax(text.codePointAt(startIndex))
        while (index < text.length) {
            val tmpSyntax = getSyntax(text.codePointAt(index))
            if (tmpSyntax != currentSyntax) {
                setTypeface(stringBuilder, startIndex, index, currentSyntax)
                currentSyntax = tmpSyntax
                startIndex = index
            }
            index++
        }
        setTypeface(stringBuilder, startIndex, index, currentSyntax)
        tv.text = stringBuilder
    }

    private fun setTypeface(stringBuilder: SpannableStringBuilder, start: Int, end: Int, syntax: Int) {
        when (syntax) {
            SYNTAX_ENGLISH -> stringBuilder.setSpan(CalligraphyTypefaceSpan(mEnglishTypeface), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            SYNTAX_ARABIC -> stringBuilder.setSpan(CalligraphyTypefaceSpan(mArabicTypeface), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            else -> stringBuilder.setSpan(CalligraphyTypefaceSpan(mEnglishTypeface), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    /**
     * This method is a helper function to determine the syntax of a character based on the Unicode.
     *
     * @param unicode : the Unicode of a character
     * @return determined syntax of that character
     */
    private fun getSyntax(unicode: Int): Int {
        return if (unicode >= 0x0600 && unicode <= 0x06ff) SYNTAX_ARABIC else SYNTAX_ENGLISH
    }

    companion object {
        private val SYNTAX_ENGLISH = 0
        private val SYNTAX_ARABIC = 1
    }
}
/**
 * This method set the typefaces to the text on the TextView.
 * This may take a few mili-seconds
 * This traverse a string and use an Automata to build a formatted word.
 *
 * @param tv is the TextView
 * @param text is the formated text
 */
