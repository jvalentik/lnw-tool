package com.ibm.lnw.presentation;

import com.ibm.lnw.backend.domain.User;
import com.ibm.lnw.presentation.views.events.LoginEvent;
import com.ibm.lnw.presentation.views.events.NavigationEvent;

import javax.enterprise.event.Observes;
import java.io.Serializable;

/**
 * Created by Ján Valentík on 21. 2. 2016.
 */
public interface NavigationService extends Serializable {

   void onNavigationEvent(@Observes NavigationEvent event);
   void onSuccessfulLoginEvent(@Observes @LoginEvent(LoginEvent.Type.LOGIN_SUCCEEDED) User user);
}