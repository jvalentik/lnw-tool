package com.ibm.lnw.presentation.views;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Jan Valentik on 12/1/2015.
 */
@Qualifier
@Retention(RUNTIME)
@Target({FIELD,PARAMETER})
public @interface RequestEvent {
	Type value();

	enum Type {
		SAVE, REFRESH
	}
}

