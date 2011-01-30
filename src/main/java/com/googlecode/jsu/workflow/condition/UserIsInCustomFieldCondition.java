package com.googlecode.jsu.workflow.condition;

import static com.googlecode.jsu.workflow.WorkflowUserIsInCustomFieldConditionPluginFactory.getAllowUserInField;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.workflow.condition.AbstractJiraCondition;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;
import com.opensymphony.user.UserManager;
import com.opensymphony.workflow.WorkflowContext;

/**
 * This Condition validates if the current user is in any of the selected groups.
 * @author Anton Afanassiev
 */
public class UserIsInCustomFieldCondition extends AbstractJiraCondition {
    private final Logger log = Logger.getLogger(UserIsInCustomFieldCondition.class);

    /* (non-Javadoc)
     * @see com.opensymphony.workflow.Condition#passesCondition(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet)
     */
    @SuppressWarnings("rawtypes")
    public boolean passesCondition(Map transientVars, Map args, PropertySet ps) {
        boolean allowUser = false;

        try {
            // Obtains the current user.
            WorkflowContext context = (WorkflowContext) transientVars.get("context");
            User userLogged = UserManager.getInstance().getUser(context.getCaller());

            // If there aren't groups selected, hidGroupsList is equal to "".
            // And groupsSelected will be an empty collection.
            String fieldKey = (String) args.get("fieldsList");

			boolean allowUserInField = getAllowUserInField(args);

            Field field = (Field) WorkflowUtils.getFieldFromKey(fieldKey);
            Issue issue = getIssue(transientVars);

            Object fieldValue = WorkflowUtils.getFieldValueFromIssue(issue, field);

            if (fieldValue != null) {
                if (fieldValue instanceof Collection) {
                    // support for MultiUser lists. user must be member of that list to pass condition
                    for (Object value : (Collection) fieldValue) {
                        allowUser = compareValues(value, userLogged, allowUserInField);

                        if (allowUser == allowUserInField) {
                            break;
                        }
                    }
                } else {
                    allowUser = compareValues(fieldValue, userLogged, allowUserInField);
                }
            }
        } catch (EntityNotFoundException e) {
            log.error("Unable to find user from context", e);
        }

        return allowUser;
    }

    private boolean compareValues(Object fieldValue, User user, boolean allowUserInField) {
        boolean result = !allowUserInField;

        if (fieldValue instanceof String) {
            if (fieldValue.equals(user.toString())) {
                result = allowUserInField;
            }
        } else {
            if (fieldValue.equals(user)) {
                result = allowUserInField;
            }
        }

        return result;
    }
}
