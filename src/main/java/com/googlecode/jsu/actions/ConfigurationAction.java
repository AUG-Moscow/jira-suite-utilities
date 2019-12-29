package com.googlecode.jsu.actions;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.googlecode.jsu.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

@Scanned
public class ConfigurationAction
extends JiraWebActionSupport {
    private final ConfigurationService configurationService;
    private boolean saved = false;
    private String googleMapsApiKey;

    @Autowired
    public ConfigurationAction(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public boolean isSaved() {
        return this.saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public String getGoogleMapsApiKey() {
        return this.googleMapsApiKey;
    }

    public void setGoogleMapsApiKey(String googleMapsApiKey) {
        this.googleMapsApiKey = googleMapsApiKey;
    }

    protected String doExecute() throws Exception {
        this.configurationService.saveGoogleMapsApiKey(this.getGoogleMapsApiKey());
        this.saved = true;
        return "input";
    }

    public String doDefault() throws Exception {
        this.saved = false;
        this.setGoogleMapsApiKey(this.configurationService.getGoogleMapsApiKey());
        return "input";
    }
}

