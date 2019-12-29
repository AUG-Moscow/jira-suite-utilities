package com.googlecode.jsu.conditions;

import com.atlassian.jira.component.ComponentAccessor;

public class UserIsAdminCondition
extends com.atlassian.jira.plugin.webfragment.conditions.UserIsAdminCondition {
    public UserIsAdminCondition() {
        super(ComponentAccessor.getPermissionManager());
    }
}

