package com.wb.nextgenlibrary.parser;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by gzcheng on 3/16/16.
 */
@Retention(RUNTIME)
@Target({TYPE})
public @interface XmlRegistry {
}
