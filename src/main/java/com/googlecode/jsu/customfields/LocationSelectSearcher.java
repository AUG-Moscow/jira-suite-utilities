package com.googlecode.jsu.customfields;

import com.atlassian.jira.issue.customfields.searchers.SelectSearcher;
import com.atlassian.jira.util.ComponentFactory;
import com.atlassian.jira.util.ComponentLocator;
import com.atlassian.jira.util.JiraComponentFactory;
import com.atlassian.jira.util.JiraComponentLocator;

public class LocationSelectSearcher
extends SelectSearcher {
    public LocationSelectSearcher() {
        super((ComponentFactory)JiraComponentFactory.getInstance(), (ComponentLocator)new JiraComponentLocator());
    }
}

