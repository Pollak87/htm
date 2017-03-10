package com.poak.htmdownloader.swing;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/*
 * This software is the confidential and proprietary information of UZEN
 * Commerce Co.,Ltd., Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with UZEN Commerce.
 */

/**
 * SearchConditionPanel
 * <p>
 * description...
 * </p>
 * 
 * @author mia
 * @since 0.1 2017. 3. 7.
 * @version 0.1
 */
public class SearchConditionPanel extends JScrollPane {

    public static final String EQUALS = "Equals";
    public static final String NOT_EQUALS = "NotEquals";
    public static final String CONTAINS = "Contains";
    public static final String NOT_CONTAINS = "NotContains";
    public static final String STARTS_WITH = "startsWith";
    public static final String ENDS_WITH = "endsWith";

    public static final String TITLE = "Title";
    public static final String COMPANY = "Company";
    public static final String SERIES = "Series";
    public static final String CHARACTER = "Character";
    public static final String TAG = "Tag";
    public static final String LANGUAGE = "Language";
    public static final String ID = "Id";
    public static final String TYPE = "Type";

    public static final String AND = "and";
    public static final String OR = "or";

    private static final long serialVersionUID = -2774759745771823730L;
    JComboBox<String> condition = new JComboBox<String>();
    JComboBox<String> keyCondition = new JComboBox<String>();
    JComboBox<String> andOr = new JComboBox<String>();
    JTextField valueCondition = new JTextField();
    JButton addBtn = new JButton();
    JButton searchBtn = new JButton();
    private ActionListener addListener;
    private ActionListener searchListener;
    private JPanel conditionPanel;

    private List<ConditionField> conditionFields = new ArrayList<ConditionField>();

    /**
     * @return the conditionFields
     */
    public List<ConditionField> getConditionFields() {
        return conditionFields;
    }

    /**
     * @param conditionFields
     *            the conditionFields to set
     */
    public void setConditionFields(List<ConditionField> conditionFields) {
        this.conditionFields = conditionFields;
    }

    /**
     * @return the searchListener
     */
    public ActionListener getSearchListener() {
        return searchListener;
    }

    /**
     * @param searchListener
     *            the searchListener to set
     */
    public void setSearchListener(ActionListener searchListener) {
        if (this.searchListener != null)
            searchBtn.removeActionListener(this.searchListener);

        searchBtn.addActionListener(searchListener);
        this.searchListener = searchListener;
    }

    /**
     * @return the addListener
     */
    public ActionListener getAddListener() {
        return addListener;
    }

    /**
     * @param addListener
     *            the addListener to set
     */
    public void setAddListener(ActionListener addListener) {
        if (this.addListener != null)
            addBtn.removeActionListener(this.addListener);

        addBtn.addActionListener(addListener);
        this.addListener = addListener;
    }

    /**
     * 
     */
    public SearchConditionPanel() {

        setOpaque(true);
        setMinimumSize(new Dimension(700, 200));
        setPreferredSize(new Dimension(650, 200));
        setMaximumSize(new Dimension(700, 200));

        JPanel addConditionPanel = new JPanel();

        addConditionPanel.setOpaque(true);
        addConditionPanel.setMinimumSize(new Dimension(650, 35));
        addConditionPanel.setPreferredSize(new Dimension(630, 35));
        addConditionPanel.setMaximumSize(new Dimension(650, 35));

        addConditionPanel.setLayout(new FlowLayout());

        condition.addItem(EQUALS);
        condition.addItem(NOT_EQUALS);
        condition.addItem(CONTAINS);
        condition.addItem(NOT_CONTAINS);
        condition.addItem(STARTS_WITH);
        condition.addItem(ENDS_WITH);

        keyCondition.addItem(TITLE);
        keyCondition.addItem(COMPANY);
        keyCondition.addItem(SERIES);
        keyCondition.addItem(CHARACTER);
        keyCondition.addItem(TAG);
        keyCondition.addItem(LANGUAGE);
        keyCondition.addItem(ID);
        keyCondition.addItem(TYPE);

        andOr.addItem(AND);
        andOr.addItem(OR);

        valueCondition.setPreferredSize(new Dimension(200, 30));

        addBtn.setText("Add");
        searchBtn.setText("Search");

        addConditionPanel.add(andOr);
        addConditionPanel.add(condition);
        addConditionPanel.add(keyCondition);
        addConditionPanel.add(valueCondition);
        addConditionPanel.add(addBtn);
        addConditionPanel.add(searchBtn);

        conditionPanel = new JPanel();
        conditionPanel.setLayout(new FlowLayout());

        conditionPanel.setOpaque(true);
        conditionPanel.setMinimumSize(new Dimension(650, 30));
        conditionPanel.setMaximumSize(new Dimension(650, 500));

        conditionPanel.add(addConditionPanel);

        setViewportView(conditionPanel);

        setAddListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField conditionTf = new JTextField(condition.getSelectedItem().toString());
                conditionTf.setEditable(false);
                JTextField keyConditionTf = new JTextField(keyCondition.getSelectedItem().toString());
                keyConditionTf.setEditable(false);
                JTextField andOrTf = new JTextField(andOr.getSelectedItem().toString());
                andOrTf.setEditable(false);
                JTextField valueConditionTf = new JTextField(valueCondition.getText());
                String value = valueCondition.getText();
                value = value.toLowerCase();
                valueConditionTf.setEditable(false);
                valueCondition.setText("");

                andOrTf.setPreferredSize(new Dimension(50, 30));
                conditionTf.setPreferredSize(new Dimension(100, 30));
                keyConditionTf.setPreferredSize(new Dimension(100, 30));
                valueConditionTf.setPreferredSize(new Dimension(200, 30));

                JButton delBtn = new JButton("Delete");

                JPanel panel = new JPanel();
                panel.setLayout(new FlowLayout());
                panel.setMinimumSize(new Dimension(600, 35));
                panel.setPreferredSize(new Dimension(600, 35));
                panel.setMaximumSize(new Dimension(600, 35));
                panel.add(andOrTf);
                panel.add(conditionTf);
                panel.add(keyConditionTf);
                panel.add(valueConditionTf);
                panel.add(delBtn);

                conditionPanel.add(panel);
                conditionPanel.setMinimumSize(new Dimension(620, (conditionFields.size() * 40) + 80));
                conditionPanel.setPreferredSize(new Dimension(620, (conditionFields.size() * 40) + 80));
                conditionPanel.setMaximumSize(new Dimension(620, (conditionFields.size() * 40) + 80));
                conditionPanel.revalidate();
                ConditionField cf = new ConditionField(panel, condition.getSelectedItem().toString(), keyCondition.getSelectedItem().toString(), andOr.getSelectedItem().toString(), value);
                conditionFields.add(cf);

                delBtn.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        conditionFields.remove(cf);
                        EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                conditionPanel.remove(cf.getPanel());
                                conditionPanel.setMinimumSize(new Dimension(620, (conditionFields.size() * 100) + 50));
                                conditionPanel.setPreferredSize(new Dimension(620, (conditionFields.size() * 100) + 50));
                                conditionPanel.setMaximumSize(new Dimension(620, (conditionFields.size() * 100) + 50));
                                conditionPanel.revalidate();
                                revalidate();
                            }
                        });

                    }
                });
            }
        });

    }

}
