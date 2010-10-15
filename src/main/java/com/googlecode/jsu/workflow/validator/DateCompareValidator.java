package com.googlecode.jsu.workflow.validator;

import static com.googlecode.jsu.helpers.ConditionCheckerFactory.DATE;
import static com.googlecode.jsu.helpers.ConditionCheckerFactory.DATE_WITHOUT_TIME;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.fields.Field;
import com.googlecode.jsu.annotation.Argument;
import com.googlecode.jsu.helpers.ComparisonType;
import com.googlecode.jsu.helpers.ConditionChecker;
import com.googlecode.jsu.helpers.ConditionCheckerFactory;
import com.googlecode.jsu.helpers.ConditionType;
import com.googlecode.jsu.util.CommonPluginUtils;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.WorkflowException;

/**
 * This validator compare two datetime fields, using the given comparison type.
 * And returning an exception if it doesn't fulfill the condition.
 */
public class DateCompareValidator extends GenericValidator {
    @Argument("date1Selected")
    private String date1;

    @Argument("date2Selected")
    private String date2;

    @Argument("conditionSelected")
    private String conditionId;

    @Argument("includeTimeSelected")
    private String includeTimeValue;

    private final Logger log = Logger.getLogger(DateCompareValidator.class);
    private final ConditionCheckerFactory conditionCheckerFactory;
    private final ApplicationProperties applicationProperties;

    /**
     * @param conditionCheckerFactory
     */
    public DateCompareValidator(
            ConditionCheckerFactory conditionCheckerFactory,
            ApplicationProperties applicationProperties
    ) {
        this.conditionCheckerFactory = conditionCheckerFactory;
        this.applicationProperties = applicationProperties;
    }

    /* (non-Javadoc)
     * @see com.googlecode.jsu.workflow.validator.GenericValidator#validate()
     */
    protected void validate() throws InvalidInputException, WorkflowException {
        Field field1 = WorkflowUtils.getFieldFromKey(date1);
        Field field2 = WorkflowUtils.getFieldFromKey(date2);

        ConditionType condition = conditionCheckerFactory.findConditionById(conditionId);
        boolean includeTime = (Integer.parseInt(includeTimeValue) == 1) ? true : false;

        // Compare Dates.
        if ((field1 != null) && (field2 != null)) {
            Object objValue1 = WorkflowUtils.getFieldValueFromIssue(getIssue(), field1);
            Object objValue2 = WorkflowUtils.getFieldValueFromIssue(getIssue(), field2);
            Date objDate1, objDate2;

            try {
                objDate1 = (Date) objValue1;
            } catch (ClassCastException e) {
                wrongDataErrorMessage(field1, objValue1);

                return;
            }

            try {
                objDate2 = (Date) objValue2;
            } catch (ClassCastException e) {
                wrongDataErrorMessage(field2, objValue2);

                return;
            }

            if ((objDate1 != null) && (objDate2 != null)) {
                ComparisonType comparison = (includeTime) ? DATE : DATE_WITHOUT_TIME;
                ConditionChecker checker = conditionCheckerFactory.getChecker(comparison, condition);

                Calendar calDate1 = Calendar.getInstance(applicationProperties.getDefaultLocale());
                Calendar calDate2 = Calendar.getInstance(applicationProperties.getDefaultLocale());

                calDate1.setTime((Date) objDate1);
                calDate2.setTime((Date) objDate2);

                boolean result = checker.checkValues(calDate1, calDate2);

                if (log.isDebugEnabled()) {
                    log.debug(
                            "Compare field \"" + field1.getName() +
                            "\" and field \"" + field2.getName() +
                            "\" with values [" + calDate1 +
                            "] and [" + calDate2 +
                            "] with result " + result
                    );
                }

                if (!result) {
                    generateErrorMessage(field1, objDate1, field2, objDate2, condition, includeTime);
                }
            } else {
                // If any of fields are null, validates if the field is required. Otherwise, doesn't throws an Exception.
                if (objDate1 == null) {
                    validateRequired(field1);
                }

                if (objDate2 == null) {
                    validateRequired(field2);
                }
            }
        } else {
            log.error("Unable to find field with ids [" + date1 + "] and [" + date2 + "]");
        }
    }

    /**
     * @param fldDate
     *
     * Throws an Exception if the field is null, but it is required.
     */
    private void validateRequired(Field fldDate){
        if (CommonPluginUtils.isFieldRequired(getIssue(), fldDate)) {
            this.setExceptionMessage(
                    fldDate,
                    fldDate.getName() + " is required.",
                    fldDate.getName() + " is required."
            );
        }
    }

    private void generateErrorMessage(
            Field field1, Object fieldValue1,
            Field field2, Object fieldValue2,
            ConditionType condition, boolean includeTime
    ) {
        // Formats date to current locale to display the Exception.
        SimpleDateFormat formatter = null;
        SimpleDateFormat defaultFormatter = null;

        if (includeTime) {
            defaultFormatter = new SimpleDateFormat(
                    applicationProperties.getDefaultString(APKeys.JIRA_DATE_PICKER_JAVA_FORMAT)
            );
            formatter = new SimpleDateFormat(
                    applicationProperties.getDefaultString(APKeys.JIRA_DATE_PICKER_JAVA_FORMAT),
                    applicationProperties.getDefaultLocale()
            );
        }else{
            defaultFormatter = new SimpleDateFormat(
                    applicationProperties.getDefaultString(APKeys.JIRA_DATE_TIME_PICKER_JAVA_FORMAT)
            );
            formatter = new SimpleDateFormat(
                    applicationProperties.getDefaultString(APKeys.JIRA_DATE_TIME_PICKER_JAVA_FORMAT),
                    applicationProperties.getDefaultLocale()
            );
        }

        String errorMsg = "";

        try{
            errorMsg = " ( " + formatter.format(fieldValue2) + " )";
        } catch (IllegalArgumentException e) {
            try {
                errorMsg = " ( " + defaultFormatter.format(fieldValue2) + " )";
            } catch(Exception e1) {
                errorMsg = " ( " + fieldValue2 + " )";
            }
        }

        this.setExceptionMessage(
                field1,
                field1.getName() + " isn't " + condition.toString() + " " + field2.getName() + errorMsg,
                field1.getName() + " isn't " + condition.toString() + " " + field2.getName() + errorMsg
        );
    }

    private void wrongDataErrorMessage(
            Field field, Object fieldValue
    ) {
        this.setExceptionMessage(
                field,
                field.getName() + " not a date value (" + fieldValue + ")",
                field.getName() + " not a date value (" + fieldValue + ")"
        );
    }
}
