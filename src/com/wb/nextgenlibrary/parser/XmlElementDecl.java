package com.wb.nextgenlibrary.parser;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by gzcheng on 3/16/16.
 */
@Retention(RUNTIME)
@Target({METHOD})
public @interface XmlElementDecl {
    /**
     * scope of the mapping.
     *
     * <p>
     * If this is not {@link XmlElementDecl.GLOBAL}, then this element
     * declaration mapping is only active within the specified class.
     */
    Class scope() default GLOBAL.class;

    /**
     * namespace name of the XML element.
     * <p>
     * If the value is "##default", then the value is the namespace
     * name for the package of the class containing this factory method.
     *
     * @see #name()
     */
    String namespace() default "##default";

    /**
     * local name of the XML element.
     *
     * <p>
     * <b> Note to reviewers: </b> There is no default name; since
     * the annotation is on a factory method, it is not clear that the
     * method name can be derived from the factory method name.
     * @see #namespace()
     */
    String name();

    /**
     * namespace name of a substitution group's head XML element.
     * <p>
     * This specifies the namespace name of the XML element whose local
     * name is specified by <tt>substitutionHeadName()</tt>.
     * <p>
     * If <tt>susbtitutionHeadName()</tt> is "", then this
     * value can only be "##default". But the value is ignored since
     * since this element is not part of susbtitution group when the
     * value of <tt>susbstitutionHeadName()</tt> is "".
     * <p>
     * If <tt>susbtitutionHeadName()</tt> is not "" and the value is
     * "##default", then the namespace name is the namespace name to
     * which the package of the containing class, marked with {@link
     * XmlRegistry }, is mapped.
     * <p>
     * If <tt>susbtitutionHeadName()</tt> is not "" and the value is
     * not "##default", then the value is the namespace name.
     *
     * @see #substitutionHeadName()
     */
    String substitutionHeadNamespace() default "##default";

    /**
     * XML local name of a substitution group's head element.
     * <p>
     * If the value is "", then this element is not part of any
     * substitution group.
     *
     * @see #substitutionHeadNamespace()
     */
    String substitutionHeadName() default "";

    /**
     * Default value of this element.
     *
     * <p>
     * The <pre>'\u0000'</pre> value specified as a default of this annotation element
     * is used as a poor-man's substitute for null to allow implementations
     * to recognize the 'no default value' state.
     */
    String defaultValue() default "\u0000";

    /**
     * Used in {@link XmlElementDecl#scope()} to
     * signal that the declaration is in the global scope.
     */
    public final class GLOBAL {}
}

