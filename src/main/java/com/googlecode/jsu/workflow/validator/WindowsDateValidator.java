package com.googlecode.jsu.workflow.validator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.fields.Field;
import com.googlecode.jsu.annotation.Argument;
import com.googlecode.jsu.util.FieldCollectionsUtils;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.WorkflowException;

/**
 * @author Gustavo Martin
 *
 * This validator compare two datetime fields, and verifies if the first of them,
 * is less than the second plus a number of days.
 * And returning an exception if it doesn't fulfill the condition.
 *
 */
public class WindowsDateValidator extends GenericValidator {
    @Argument("date1Selected")
    private String date1;

    @Argument("date2Selected")
    private String date2;

    @Argument
    private String windowsDays;

    private final ApplicationProperties applicationProperties;

    /**
     * @param fieldCollectionsUtils
     * @param applicationProperties
     */
    public WindowsDateValidator(
            FieldCollectionsUtils fieldCollectionsUtils,
            ApplicationProperties applicationProperties,
            WorkflowUtils workflowUtils
    ) {
        super(fieldCollectionsUtils, workflowUtils);

        this.applicationProperties = applicationProperties;
    }

    /* (non-Javadoc)
     * @see com.googlecode.jsu.workflow.validator.GenericValidator#validate()
     */
    protected void validate() throws InvalidInputException, WorkflowException {
        Field fldDate1 = workflowUtils.getFieldFromKey(date1);
        Field fldDate2 = workflowUtils.getFieldFromKey(date2);

        // Compare Dates.
        if ((fldDate1 != null) && (fldDate2 != null)) {
            checkDatesCondition(fldDate1, fldDate2, windowsDays);
        }
    }

    /**
     * @param fldDate1
     * @param fldDate2
     * @param window
     *
     * It makes the comparison properly this.
     */
    private void checkDatesCondition(Field fldDate1, Field fldDate2, String window) {
        boolean condOK = false;

        Object objDate1 = workflowUtils.getFieldValueFromIssue(getIssue(), fldDate1);
        Object objDate2 = workflowUtils.getFieldValueFromIssue(getIssue(), fldDate2);

        if ((objDate1 != null) && (objDate2 != null)) {
            // It Takes the Locale for inicialize dates.
            Locale locale = applicationProperties.getDefaultLocale();

            Calendar calDate1 = Calendar.getInstance(locale);
            Calendar calDate2 = Calendar.getInstance(locale);
            Calendar calWindowsDate = Calendar.getInstance(locale);

            calDate1.setTime((Date) objDate1);
            calDate2.setTime((Date) objDate2);
            calWindowsDate.setTime((Date) objDate2);
            calWindowsDate.add(Calendar.DATE, Integer.parseInt(window));

            fieldCollectionsUtils.clearCalendarTimePart(calDate1);
            fieldCollectionsUtils.clearCalendarTimePart(calDate2);

            Date date1 = calDate1.getTime();
            Date date2 = calDate2.getTime();
            Date windowsDate = calWindowsDate.getTime();

            int comparacion = date1.compareTo(windowsDate);

            if(comparacion < 0){
                comparacion = date1.compareTo(date2);

                if(comparacion>=0){
                    condOK = true;
                }
            }

            if (!condOK) {
                // Formats date to current locale, for display the Exception.
                SimpleDateFormat defaultFormatter = new SimpleDateFormat(
                        applicationProperties.getDefaultString(APKeys.JIRA_DATE_PICKER_JAVA_FORMAT)
                );
                SimpleDateFormat formatter = new SimpleDateFormat(
                        applicationProperties.getDefaultString(APKeys.JIRA_DATE_PICKER_JAVA_FORMAT), locale
                );

                String errorMsg = "";

                try {
                    errorMsg = " ( Between " + formatter.format(date2) + " and " + formatter.format(windowsDate) +  " )";
                } catch (IllegalArgumentException e) {
                    try {
                        errorMsg = " ( Between " + defaultFormatter.format(date2) + " and " + defaultFormatter.format(windowsDate) +  " )";
                    } catch (Exception e1) {
                        errorMsg = " ( Between " + date2 + " and " + windowsDate +  " )";
                    }
                }

                this.setExceptionMessage(
                        fldDate1,
                        fldDate1.getName() + " is not within " + fldDate2.getName() + ", more " + window + " days. " + errorMsg,
                        fldDate1.getName() + " is not within " + fldDate2.getName() + ", more " + window + " days. " + errorMsg
                );
            }
        } else {
            // If any of fields are null, validates if the field is required. Otherwise, doesn't throws an Exception.
            if (objDate1 == null) {
                validateRequired(fldDate1);
            }

            if (objDate2 == null) {
                validateRequired(fldDate2);
            }
        }
    }

    /**
     * @param fldDate
     *
     * Throws an Exception if the field is null, but it is required.
     */
    private void validateRequired(Field fldDate){
        if (fieldCollectionsUtils.isFieldRequired(getIssue(), fldDate)) {
            this.setExceptionMessage(
                    fldDate,
                    fldDate.getName() + " is required.",
                    fldDate.getName() + " is required."
            );
        }
    }
}
