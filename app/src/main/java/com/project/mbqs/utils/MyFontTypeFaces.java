package com.project.mbqs.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Locale;

public class MyFontTypeFaces {
    //returns typeface for numbers
    public static Typeface getNumberFont(Context context){

        return Typeface.createFromAsset(context.getApplicationContext().getAssets(),
                String.format(Locale.US, "fonts/%s", "glegoo_bold.ttf"));

    }
}
