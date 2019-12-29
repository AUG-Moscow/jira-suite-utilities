package com.googlecode.jsu.customfields;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.impl.RenderableTextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.TextFieldCharacterLengthValidator;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.googlecode.jsu.service.ConfigurationService;
import java.util.Map;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;

@Scanned
public class DirectionsCFType
extends RenderableTextCFType {
    private final ConfigurationService configurationService;
    private final JiraAuthenticationContext jiraAuthenticationContext;

    @Autowired
    public DirectionsCFType(@ComponentImport CustomFieldValuePersister customFieldValuePersister, @ComponentImport GenericConfigManager genericConfigManager, @ComponentImport TextFieldCharacterLengthValidator textFieldCharacterLengthValidator, ConfigurationService configurationService, @ComponentImport JiraAuthenticationContext jiraAuthenticationContext) {
        super(customFieldValuePersister, genericConfigManager, textFieldCharacterLengthValidator, jiraAuthenticationContext);
        this.configurationService = configurationService;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
    }

    public String getSingularObjectFromString(String string) throws FieldValidationException {
        if (string != null && !string.contains("=>")) {
            String message = this.jiraAuthenticationContext.getI18nHelper().getText("edit-directions.invalid_directions");
            throw new FieldValidationException(message);
        }
        return string;
    }

    @Nonnull
    public Map<String, Object> getVelocityParameters(Issue issue, CustomField field, FieldLayoutItem fieldLayoutItem) {
        Map<String, Object> params = super.getVelocityParameters(issue, field, fieldLayoutItem);
        params.put("googleMapsApiKey", this.configurationService.getGoogleMapsApiKey());
        return params;
    }
}

