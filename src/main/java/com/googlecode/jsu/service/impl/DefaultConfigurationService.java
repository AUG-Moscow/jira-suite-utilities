package com.googlecode.jsu.service.impl;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.googlecode.jsu.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("configurationService")
public class DefaultConfigurationService implements ConfigurationService {
    private static final String GOOGLE_MAPS_API_KEY = "jsu.google.maps.api.key";
    private PluginSettingsFactory pluginSettingsFactory;

    @Autowired
    public DefaultConfigurationService(@ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public String getGoogleMapsApiKey() {
        PluginSettings pluginSettings = this.pluginSettingsFactory.createGlobalSettings();
        return (String)pluginSettings.get(GOOGLE_MAPS_API_KEY);
    }

    @Override
    public void saveGoogleMapsApiKey(String googleMapsApiKey) {
        PluginSettings pluginSettings = this.pluginSettingsFactory.createGlobalSettings();
        pluginSettings.put(GOOGLE_MAPS_API_KEY, googleMapsApiKey);
    }
}

