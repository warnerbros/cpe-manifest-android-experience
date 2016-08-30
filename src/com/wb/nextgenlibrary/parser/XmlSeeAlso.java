package com.wb.nextgenlibrary.parser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by gzcheng on 3/16/16.
 */
@Target({ElementType.TYPE})
@Retention(RUNTIME)
public @interface XmlSeeAlso {
    Class[] value();
}
