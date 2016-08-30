package com.wb.nextgenlibrary.parser;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by gzcheng on 3/16/16.
 */
@Retention(RUNTIME)
@Target({FIELD})
public @interface XmlEnumValue {
    String value();
}

