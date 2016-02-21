package com.ibm.lnw.presentation.views.events;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Ján Valentík on 21. 2. 2016.
 */
@Qualifier
@Retention(RUNTIME)
@Target({FIELD,PARAMETER})
public @interface LoginEvent {
    Type value();

    enum Type {
        LOGIN_FAILED, LOGIN_SUCCEEDED
    }
}
