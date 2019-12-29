package com.googlecode.jsu.customfields;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.SelectCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.rest.json.beans.JiraBaseUrls;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.googlecode.jsu.service.ConfigurationService;
import java.util.Map;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;

@Scanned
public class LocationSelectCFType
extends SelectCFType {
    private final ConfigurationService configurationService;

    @Autowired
    public LocationSelectCFType(@ComponentImport CustomFieldValuePersister customFieldValuePersister, @ComponentImport OptionsManager optionsManager, @ComponentImport GenericConfigManager genericConfigManager, @ComponentImport JiraBaseUrls jiraBaseUrls, ConfigurationService configurationService) {
        super(customFieldValuePersister, optionsManager, genericConfigManager, jiraBaseUrls);
        this.configurationService = configurationService;
    }

    @Nonnull
    public Map<String, Object> getVelocityParameters(Issue issue, CustomField field, FieldLayoutItem fieldLayoutItem) {
        Map params = super.getVelocityParameters(issue, field, fieldLayoutItem);
        params.put("googleMapsApiKey", this.configurationService.getGoogleMapsApiKey());
        return params;
    }
}

