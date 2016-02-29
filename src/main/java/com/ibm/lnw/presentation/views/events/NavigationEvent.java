package com.ibm.lnw.presentation.views.events;

import java.util.Map;

/**
 * Created by Ján Valentík on 21. 2. 2016.
 */
public class NavigationEvent {
    private final String navigateTo;
    private final String navigateFrom;
    private final Map<String, String[]> parameterMap;

    public NavigationEvent(String navigateTo, String navigateFrom) {
        this(navigateTo, navigateFrom, null);
    }

    public NavigationEvent(String navigateTo, String navigateFrom, Map<String, String[]> parameterMap) {
        this.navigateFrom = navigateFrom;
        this.navigateTo = navigateTo;
        this.parameterMap = parameterMap;
    }

    public NavigationEvent(String navigateTo) {
        this(navigateTo, "not provided", null);

    }

    public String getNavigateTo() {
        return navigateTo;
    }

    public String getNavigateFrom() {
        return navigateFrom;
    }

    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }
}
