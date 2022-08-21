package com.badziol.wrzospaliwok01

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Matcher
import java.util.regex.Pattern

// class to decimal digits input filter
class DecimalDigitsInputFilter(
    maxDigitsIncludingPoint: Int, //calkowita maksymalna ilosc liczb
    maxDecimalPlaces: Int //ilosc liczb po przecinku
) : InputFilter {
    private val pattern: Pattern = Pattern.compile(
        "[0-9]{0," + (maxDigitsIncludingPoint - 1) + "}+((\\.[0-9]{0,"
                + (maxDecimalPlaces - 1) + "})?)||(\\.)?"
    )

    override fun filter(
        p0: CharSequence?, p1: Int, p2: Int, p3: Spanned?, p4: Int, p5: Int
    ): CharSequence? {
        p3?.apply {
            val matcher: Matcher = pattern.matcher(p3)
            return if (!matcher.matches()) "" else null
        }
        return null
    }
}