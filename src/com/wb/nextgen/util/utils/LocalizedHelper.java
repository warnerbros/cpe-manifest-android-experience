package com.wb.nextgen.util.utils;

import net.flixster.android.localization.Localizer;

public class LocalizedHelper {

    protected final boolean isFrance;

    protected LocalizedHelper() {
        isFrance = "FR".contentEquals(Localizer.getLocale().getCountry());
    }
}
