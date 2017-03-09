package com.poak.htmdownloader.swing;

import javax.swing.JPanel;

/*
 * This software is the confidential and proprietary information of UZEN 
 * Commerce Co.,Ltd., Inc. You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with UZEN Commerce.
 */

/**
 * ConditionField
 * <p>
 * description...
 * </p> 
 * @author mia
 * @since 0.1 2017. 3. 7.
 * @version 0.1
 * 
 */
public class ConditionField {

    private JPanel panel;
    private String condition;
    private String keyCondition;
    private String valueCondition;
    private String andOr;

    /**
     * @param panel
     * @param condition
     * @param keyCondition
     * @param valueCondition
     * @param value
     */
    public ConditionField(JPanel panel, String condition, String keyCondition, String andOr, String valueCondition) {
        super();
        this.panel = panel;
        this.condition = condition;
        this.keyCondition = keyCondition;
        this.valueCondition = valueCondition;
        this.andOr = andOr;
    }

    /**
     * @return the panel
     */
    public JPanel getPanel() {
        return panel;
    }

    /**
     * @param panel
     *            the panel to set
     */
    public void setPanel(JPanel panel) {
        this.panel = panel;
    }

    /**
     * @return the condition
     */
    public String getCondition() {
        return condition;
    }

    /**
     * @param condition
     *            the condition to set
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * @return the keyCondition
     */
    public String getKeyCondition() {
        return keyCondition;
    }

    /**
     * @param keyCondition
     *            the keyCondition to set
     */
    public void setKeyCondition(String keyCondition) {
        this.keyCondition = keyCondition;
    }

    /**
     * @return the valueCondition
     */
    public String getValueCondition() {
        return valueCondition;
    }

    /**
     * @param valueCondition
     *            the valueCondition to set
     */
    public void setValueCondition(String valueCondition) {
        this.valueCondition = valueCondition;
    }

    /**
     * @return the andOr
     */
    public String getAndOr() {
        return andOr;
    }

    /**
     * @param andOr
     *            the andOr to set
     */
    public void setAndOr(String andOr) {
        this.andOr = andOr;
    }

}
