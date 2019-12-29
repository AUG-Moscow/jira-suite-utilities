package com.googlecode.jsu.customfields;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.CalculatedCFType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.googlecode.jsu.helpers.FormattableDuration;
import com.googlecode.jsu.transitionssummary.TransitionSummary;
import com.googlecode.jsu.transitionssummary.TransitionsManager;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@Scanned
public class TimeInSourceStatusCFType
extends CalculatedCFType<FormattableDuration, FormattableDuration> {
    protected final CustomFieldValuePersister customFieldValuePersister;
    protected final GenericConfigManager genericConfigManager;
    private final TransitionsManager transitionsManager;

    @Autowired
    public TimeInSourceStatusCFType(@ComponentImport CustomFieldValuePersister customFieldValuePersister, @ComponentImport GenericConfigManager genericConfigManager, TransitionsManager transitionsManager) {
        this.customFieldValuePersister = customFieldValuePersister;
        this.genericConfigManager = genericConfigManager;
        this.transitionsManager = transitionsManager;
    }

    public String getStringFromSingularObject(FormattableDuration value) {
        return value != null ? value.toString() : "0";
    }

    public FormattableDuration getSingularObjectFromString(String value) throws FieldValidationException {
        return value != null ? new FormattableDuration(value) : new FormattableDuration(0L);
    }

    public FormattableDuration getValueFromIssue(CustomField customField, Issue issue) {
        List<TransitionSummary> summaries = this.transitionsManager.getTransitionSummary(issue);
        long duration = 0L;
        TransitionSummary lastSummary = null;
        for (TransitionSummary summary : summaries) {
            if (summary.getFromStatus().getId().equals(issue.getStatusObject().getId())) {
                duration += summary.getDurationInMillis();
            }
            lastSummary = summary;
        }
        duration = lastSummary != null ? (duration += System.currentTimeMillis() - lastSummary.getLastUpdate().getTime()) : (duration += System.currentTimeMillis() - issue.getCreated().getTime());
        return new FormattableDuration(duration);
    }

    public FormattableDuration getDefaultValue(FieldConfig fieldConfig) {
        return new FormattableDuration(0L);
    }
}

