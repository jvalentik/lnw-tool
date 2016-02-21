package com.ibm.lnw.presentation.views.events;

/**
 * Created by Ján Valentík on 21. 2. 2016.
 */
public class NavigationEvent {
    private final String navigateTo;

    public NavigationEvent(String navigateTo) {
        this.navigateTo = navigateTo;
    }

    public String getNavigateTo() {
        return navigateTo;
    }

}
