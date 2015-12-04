package com.ibm.lnw.presentation.views;

import com.vaadin.server.FontAwesome;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Jan Valentik on 12/2/2015.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ViewMenuItem {
	int END = Integer.MAX_VALUE;
	int BEGINNING = 0;
	int DEFAULT = 1000;

	boolean enabled() default true;
	String title() default "";
	int order() default DEFAULT;
	FontAwesome icon() default FontAwesome.FILE;

}

