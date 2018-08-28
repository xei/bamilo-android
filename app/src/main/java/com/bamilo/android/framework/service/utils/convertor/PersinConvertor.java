package com.bamilo.android.framework.service.utils.convertor;

/**
 * Created by shahrooz on 1/30/17.
 */

public class PersinConvertor {
    public static String toPersianNumber(String number)
    {
        return number.toString().replaceAll("0","۰").replaceAll("1","۱").replaceAll("2","۲")
                .replaceAll("3","۳").replaceAll("4","۴").replaceAll("5","۵").replaceAll("6","۶")
                .replaceAll("7","۷").replaceAll("8","۸").replaceAll("9","۹");
    }

    public static String toEnglishNumber(String number)
    {
        return number.toString().replaceAll("۰","0").replaceAll("۱","1").replaceAll("۲","2")
                .replaceAll("۳","3").replaceAll("۴","4").replaceAll("۵","5").replaceAll("۶","6")
                .replaceAll("۷","7").replaceAll("۸","8").replaceAll("۹","9");
    }
}
