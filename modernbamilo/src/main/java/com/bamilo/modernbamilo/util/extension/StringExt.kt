package com.bamilo.modernbamilo.util.extension

/**
 * This method converts a number string to persian equivalent.
 *
 * @param numberStr The number string that must convert to Persian.
 * @return Persian number string
 * @throws NumberFormatException occurs when the "numberStr" is invalid.
 */
@Throws(NumberFormatException::class)
fun persianize(numberStr: String): String {
    var persianNumberStr = ""
    for (i in 0 until numberStr.length) {
        val numberUnicode = numberStr[i].toInt()
        val persianUnicode: Int
        persianUnicode = when (numberUnicode) {
            in 48..57 -> numberUnicode + 1728   // The digit character is Latin
            in 1632..1641 -> numberUnicode + 144    // The digit is Arabic
            in 1776..1785 -> numberUnicode  // The digit character is Persian
            else -> throw NumberFormatException("\"numberStr\" has an invalid digit character") // The digit character is invalid
        }
        persianNumberStr += persianUnicode.toChar()
    }
    return persianNumberStr
}

/**
 * This method gets a string and converts all digits in it to Persian equivalent.
 *
 * @param str the string that may have some digit characters.
 * @return the processed string that don't have any non-persian digit character.
 */
fun persianizeDigits(str: String): String {
    var persianizedStr = ""
    for (i in 0 until str.length) {
        val unicode = str[i].toInt()
        val persianizedUnicode: Int
        if (unicode in 48..57) {
            // The character is Latin digit
            persianizedUnicode = unicode + 1728
        } else if (unicode in 1632..1641) {
            // The character is Arabic digit
            persianizedUnicode = unicode + 144
        } else {
            persianizedUnicode = unicode
        }
        persianizedStr += persianizedUnicode.toChar()
    }
    return persianizedStr
}