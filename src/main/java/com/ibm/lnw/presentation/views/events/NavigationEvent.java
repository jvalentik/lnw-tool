package com.ibm.lnw.presentation.views.events;

/**
 * Created by Ján Valentík on 21. 2. 2016.
 */
public class NavigationEvent {
    private final String navigateTo;
    private final String navigateFrom;

    public NavigationEvent(String navigateTo, String navigateFrom) {
        this.navigateFrom = navigateFrom;
        this.navigateTo = navigateTo;
    }

    public NavigationEvent(String navigateTo) {
        this(navigateTo, "not provided");

    }

    public String getNavigateTo() {
        return navigateTo;
    }

    public String getNavigateFrom() {
        return navigateFrom;
    }

}
