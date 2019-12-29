JIRA Suite Utilities
====================

This plugin Jira provides various utilities when defining workflows.

It is compatible with Jira 7.6.0.

Features
--------

* Transitions manager: Generate statistics information about transitions.

* Many transitions validators:
  * User Is In Any Groups: Allows only users of a given group to execute the transition.
  * User Is In Any Roles: Allows only users of a given role to execute the transition.
  * Value Field: Allows to execute a transition if the given value of a field is equal to a constant value, or simply set.
  * User Is In Custom field: Allows only users in a given custom field to execute the transition.
  * Date Compare: Compare two date fields during a workflow transition.
  * Date Expression Compare: Compares a date field to a date expression during a workflow transition.
  * Fields Required: Fields required during a workflow transition.
  * Regular Expression Check: Validate field contents against a regular expression during a workflow transition.
  * Date Window: Compares two date fields, by adding a time span in days to one of them.
   
* Many transitions actions:
  * Copy Value From Other Field: Copies the value of one field to another, either within the same issue or from parent to sub-task.
  * Update Issue Custom Field: Updates an issue custom field to a given value.
  * Clear Field Value: Clear value of a given field.
 
* Transitions Summary Tab Panel: Show how much time was spent in executing each transition.

How to build
------------

* First download latest JIRA SDK: https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project
* Then clone the repo: `$ git clone https://github.com/AUGSpb/jira-suite-utilities.git`
* Enter the plugin directory: `$ cd jira-suite-utilities`
* Package the plugin: `$ atlas-mvn clean && atlas-mvn package`
* Check the jar is created in `target` sub-directory: `$ jar tf target/jira-suite-utilities-2.0.0-SNAPSHOT.jar`
* Import the plugin jar into your jira instance
  * In section 'Administration'/'Apps'/'Manage apps'
  * Make sure there is no existing 'Jira Suite Utilities' plugin already installed, if so uninstall it
  * Select the `Upload app` link at the top of the add-ons list to import the plugin jar file.


Original plugin information
---------------------------

This is the source code for the JIRA Suite Utilities plugin.

* **Home Page**: https://plugins.atlassian.com/plugin/details/5048
* **Issue Tracking**: https://studio.plugins.atlassian.com/browse/JSUTIL
