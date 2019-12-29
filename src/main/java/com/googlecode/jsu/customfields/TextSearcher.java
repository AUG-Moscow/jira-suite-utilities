package com.googlecode.jsu.customfields;

import com.atlassian.jira.issue.customfields.searchers.transformer.CustomFieldInputHelper;
import com.atlassian.jira.jql.operand.JqlOperandResolver;
import com.atlassian.jira.web.FieldVisibilityManager;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;

@Scanned
public class TextSearcher
extends com.atlassian.jira.issue.customfields.searchers.TextSearcher {
    @Autowired
    public TextSearcher(@ComponentImport FieldVisibilityManager fieldVisibilityManager, @ComponentImport JqlOperandResolver jqlOperandResolver, @ComponentImport CustomFieldInputHelper customFieldInputHelper) {
        super(fieldVisibilityManager, jqlOperandResolver, customFieldInputHelper);
    }
}

