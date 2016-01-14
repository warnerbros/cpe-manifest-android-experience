package com.wb.nextgen.util.utils;


import com.wb.nextgen.R;
import android.content.Context;

public class Translator extends LocalizedHelper {
    private static Translator instance = null;

    private final String[] englishGenres, frenchGenres;

    private Translator(Context context) {
        englishGenres = context.getResources().getStringArray(R.array.dvd_browse_catagories_english);
        frenchGenres = context.getResources().getStringArray(R.array.dvd_browse_catagories);
    }

    public static synchronized void initialize(Context context) {
        if (instance == null) {
            instance = new Translator(context);
        }
    }

    public static Translator instance() {
        return instance;
    }

    public String translateGenre(String genre) {
        if (!isFrance) {
            return genre;
        }
        for (int i = 0; i < englishGenres.length; i++) {
            if (englishGenres[i].equals(genre)) {
                return frenchGenres[i];
            }
        }
        return genre;
    }
}
