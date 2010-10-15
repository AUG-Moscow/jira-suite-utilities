package com.googlecode.jsu.workflow;

import static com.googlecode.jsu.helpers.YesNoType.NO;
import static com.googlecode.jsu.helpers.YesNoType.YES;
import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginValidatorFactory;
import com.googlecode.jsu.helpers.ConditionCheckerFactory;
import com.googlecode.jsu.helpers.ConditionType;
import com.googlecode.jsu.helpers.YesNoType;
import com.googlecode.jsu.util.CommonPluginUtils;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;

/**
 * @author Gustavo Martin.
 *
 * This class defines the parameters available for Date Compare Validator.
 *
 */
public class WorkflowDateCompareValidatorPluginFactory extends
        AbstractWorkflowPluginFactory implements WorkflowPluginValidatorFactory {

    private final ConditionCheckerFactory conditionCheckerFactory;

    /**
     * @param conditionCheckerFactory
     */
    public WorkflowDateCompareValidatorPluginFactory(ConditionCheckerFactory conditionCheckerFactory) {
        this.conditionCheckerFactory = conditionCheckerFactory;
    }

    /* (non-Javadoc)
     * @see com.googlecode.jsu.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForInput(java.util.Map)
     */
    protected void getVelocityParamsForInput(Map velocityParams) {
        List<Field> allDateFields = CommonPluginUtils.getAllDateFields();
        List<ConditionType> conditionList = conditionCheckerFactory.getConditionTypes();

        velocityParams.put("val-date1FieldsList", allDateFields);
        velocityParams.put("val-date2FieldsList", allDateFields);
        velocityParams.put("val-conditionList", conditionList);
        velocityParams.put("val-includeTime", asList(YES, NO));

    }

    /* (non-Javadoc)
     * @see com.googlecode.jsu.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForEdit(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
     */
    protected void getVelocityParamsForEdit(Map velocityParams,
            AbstractDescriptor descriptor) {
        getVelocityParamsForInput(velocityParams);

        ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;
        Map args = validatorDescriptor.getArgs();

        String date1 = (String) args.get("date1Selected");
        String date2 = (String) args.get("date2Selected");
        String conditionId = (String) args.get("conditionSelected");
        String includeTime = (String) args.get("includeTimeSelected");

        ConditionType condition = conditionCheckerFactory.findConditionById(conditionId);
        YesNoType ynTime = (Integer.parseInt(includeTime) == 1) ? YES : NO;

        velocityParams.put("val-date1Selected", WorkflowUtils.getFieldFromKey(date1));
        velocityParams.put("val-date2Selected", WorkflowUtils.getFieldFromKey(date2));
        velocityParams.put("val-conditionSelected", condition);
        velocityParams.put("val-includeTimeSelected", ynTime);

    }

    /* (non-Javadoc)
     * @see com.googlecode.jsu.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForView(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
     */
    protected void getVelocityParamsForView(Map velocityParams,
            AbstractDescriptor descriptor) {
        ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;
        Map args = validatorDescriptor.getArgs();

        String date1 = (String) args.get("date1Selected");
        String date2 = (String) args.get("date2Selected");
        String conditionId = (String) args.get("conditionSelected");
        String includeTime = (String) args.get("includeTimeSelected");

        ConditionType condition = conditionCheckerFactory.findConditionById(conditionId);
        YesNoType ynTime = (Integer.parseInt(includeTime) == 1) ? YES : NO;

        velocityParams.put("val-date1Selected", WorkflowUtils.getFieldFromKey(date1));
        velocityParams.put("val-date2Selected", WorkflowUtils.getFieldFromKey(date2));
        velocityParams.put("val-conditionSelected", condition);
        velocityParams.put("val-includeTimeSelected", ynTime);

    }

    /* (non-Javadoc)
     * @see com.googlecode.jsu.workflow.WorkflowPluginFactory#getDescriptorParams(java.util.Map)
     */
    public Map getDescriptorParams(Map validatorParams) {
        Map params = new HashMap();

        try{
            String date1 = extractSingleParam(validatorParams, "date1FieldsList");
            String date2 = extractSingleParam(validatorParams, "date2FieldsList");
            String condition = extractSingleParam(validatorParams, "conditionList");
            String includeTime = extractSingleParam(validatorParams, "includeTimeList");

            params.put("date1Selected", date1);
            params.put("date2Selected", date2);
            params.put("conditionSelected", condition);
            params.put("includeTimeSelected", includeTime);

        }catch(IllegalArgumentException iae){
            // Aggregate so that Transitions can be added.
        }

        return params;
    }

}
