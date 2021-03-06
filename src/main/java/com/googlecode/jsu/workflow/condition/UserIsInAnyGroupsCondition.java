package com.googlecode.jsu.workflow.condition;

import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.workflow.condition.AbstractJiraCondition;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowContext;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gustavo Martin
 * <p>
 * This Condition validates if the current user is in any of the selected groups.
 */
public class UserIsInAnyGroupsCondition extends AbstractJiraCondition {
    private static final Logger LOG = LoggerFactory.getLogger(UserIsInAnyGroupsCondition.class);

    private final WorkflowUtils workflowUtils;
    private final UserManager userManager;
    private final CrowdService crowdService;

    public UserIsInAnyGroupsCondition(WorkflowUtils workflowUtils, UserManager userManager, CrowdService crowdService) {
        this.workflowUtils = workflowUtils;
        this.userManager = userManager;
        this.crowdService = crowdService;
    }

    /* (non-Javadoc)
     * @see com.opensymphony.workflow.Condition#passesCondition(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet)
     */
    public boolean passesCondition(Map transientVars, Map args, PropertySet ps) {
        // Obtains the current user.
        WorkflowContext context = (WorkflowContext) transientVars.get("context");
        String caller = context.getCaller();

        if (caller != null) { // null -> User not logged in
            ApplicationUser userLogged = workflowUtils.getApplicationUser(caller);

            // If there aren't groups selected, hidGroupsList is equal to "".
            // And groupsSelected will be an empty collection.
            String strGroupsSelected = (String) args.get("hidGroupsList");
            Collection<Group> groupsSelected = workflowUtils.getGroups(strGroupsSelected, WorkflowUtils.SPLITTER);

            for (Group group : groupsSelected) {
                try {
                    if (crowdService.isUserMemberOfGroup(userLogged.getDirectoryUser(), group)) {
                        return true;
                    }
                } catch (Exception e) {
                    //see JSUTIL-68
                }
            }
        }

        return false;
    }


}
