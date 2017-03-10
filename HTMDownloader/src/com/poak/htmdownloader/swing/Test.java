package com.poak.htmdownloader.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.poak.htmdownloader.MItem;

/*
 * This software is the confidential and proprietary information of UZEN
 * Commerce Co.,Ltd., Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with UZEN Commerce.
 */

/**
 * Test
 * <p>
 * description...
 * </p>
 * 
 * @author mia
 * @since 0.1 2017. 3. 7.
 * @version 0.1
 */
public class Test {

    private static List<MItem> allItems = new ArrayList<MItem>();
    private static MenuTest view;
    private static String downloadDir = "d:\\download\\";
    private static int speed = 10;

    private static String proxyList =
            "[{\"IP\":\"207.154.205.235\",\"PORT\":3128,\"latest_check\":1489108810,\"ping\":16,\"connection_delay\":0,\"country\":\"UNITED STATES\",\"down_speed\":0,\"up_speed\":0,\"proxiescol\":null,\"anonymity\":\"Transparent\",\"type\":\"http\",\"google_proxy\":0},{\"IP\":\"207.154.225.89\",\"PORT\":3128,\"latest_check\":1489108810,\"ping\":0,\"connection_delay\":0,\"country\":\"UNITED STATES\",\"down_speed\":0,\"up_speed\":0,\"proxiescol\":null,\"anonymity\":\"Transparent\",\"type\":\"http\",\"google_proxy\":0},{\"IP\":\"207.99.118.74\",\"PORT\":8080,\"latest_check\":1489108810,\"ping\":78,\"connection_delay\":0,\"country\":\"UNITED STATES\",\"down_speed\":0,\"up_speed\":0,\"proxiescol\":null,\"anonymity\":\"Transparent\",\"type\":\"http\",\"google_proxy\":0},{\"IP\":\"208.109.176.11\",\"PORT\":80,\"latest_check\":1489108810,\"ping\":172,\"connection_delay\":0,\"country\":\"UNITED STATES\",\"down_speed\":0,\"up_speed\":0,\"proxiescol\":null,\"anonymity\":\"Elite\",\"type\":\"http\",\"google_proxy\":0},{\"IP\":\"208.110.80.178\",\"PORT\":3128,\"latest_check\":1489108810,\"ping\":109,\"connection_delay\":0,\"country\":\"UNITED STATES\",\"down_speed\":0,\"up_speed\":0,\"proxiescol\":null,\"anonymity\":\"Transparent\",\"type\":\"http\",\"google_proxy\":0},{\"IP\":\"208.115.238.162\",\"PORT\":3128,\"latest_check\":1489108810,\"ping\":110,\"connection_delay\":0,\"country\":\"UNITED STATES\",\"down_speed\":0,\"up_speed\":0,\"proxiescol\":null,\"anonymity\":\"Transparent\",\"type\":\"http\",\"google_proxy\":0},{\"IP\":\"208.92.94.132\",\"PORT\":1080,\"latest_check\":1489108810,\"ping\":157,\"connection_delay\":0,\"country\":\"UNITED STATES\",\"down_speed\":0,\"up_speed\":0,\"proxiescol\":null,\"anonymity\":\"Elite\",\"type\":\"http\",\"google_proxy\":1},{\"IP\":\"208.92.94.133\",\"PORT\":1080,\"latest_check\":1489108810,\"ping\":156,\"connection_delay\":0,\"country\":\"UNITED STATES\",\"down_speed\":0,\"up_speed\":0,\"proxiescol\":null,\"anonymity\":\"Elite\",\"type\":\"http\",\"google_proxy\":1}]";

    /**
     * @return the speed
     */
    public static int getSpeed() {
        return speed;
    }

    /**
     * @param speed
     *            the speed to set
     */
    public static void setSpeed(int speed) {
        Test.speed = speed;
    }

    /**
     * @return the downloadDir
     */
    public static String getDownloadDir() {
        return downloadDir;
    }

    /**
     * @param downloadDir
     *            the downloadDir to set
     */
    public static void setDownloadDir(String downloadDir) {
        Test.downloadDir = downloadDir;
    }

    private static String getText(String name)
    {
        try {

            String path = Test.getDownloadDir() + "MData(Need)/";
            File file = new File(path, name);
            String sql = IOUtils.toString(new FileInputStream(file));
            return sql;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        initJsonBind();

        view = new MenuTest(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List<ConditionField> conditions = view.getConditions();

                if (conditions.isEmpty()) {
                    view.setItemList(allItems);
                    return;
                }

                List<MItem> resultList = new ArrayList<MItem>();

                for (MItem item : allItems) {
                    boolean success = true;

                    for (ConditionField cond : conditions) {
                        String[] values = getConditionValue(item, cond.getKeyCondition());
                        if (values == null) {
                            success = false;
                            break;
                        }


                        boolean mSuccess = false;
                        switch (cond.getCondition()) {
                        case SearchConditionPanel.EQUALS:
                            for (String value : values) {
                                if (cond.getValueCondition().equals(value)) {
                                    mSuccess = true;
                                    break;
                                }
                            }
                            if (!mSuccess)
                                success = false;
                            break;
                        case SearchConditionPanel.NOT_EQUALS:
                            for (String value : values) {
                                if (cond.getValueCondition().equals(value)) {
                                    success = false;
                                    break;
                                }
                            }
                            break;
                        case SearchConditionPanel.CONTAINS:
                            for (String value : values) {
                                if (value != null && !value.isEmpty() && value.contains(cond.getValueCondition())) {
                                    mSuccess = true;
                                    break;
                                }
                            }
                            if (!mSuccess)
                                success = false;
                            break;
                        case SearchConditionPanel.NOT_CONTAINS:
                            for (String value : values) {
                                if (value != null && !value.isEmpty() && value.contains(cond.getValueCondition())) {
                                    success = false;
                                    break;
                                }
                                break;
                            }
                            break;
                        case SearchConditionPanel.STARTS_WITH:
                            for (String value : values) {
                                if (value != null && !value.isEmpty() && value.startsWith(cond.getValueCondition())) {
                                    mSuccess = true;
                                    break;
                                }
                            }
                            if (!mSuccess)
                                success = false;
                            break;
                        case SearchConditionPanel.ENDS_WITH:
                            for (String value : values) {
                                if (value != null && !value.isEmpty() && value.endsWith(cond.getValueCondition())) {
                                    mSuccess = true;
                                    resultList.add(item);
                                    break;
                                }
                            }
                            if (!mSuccess)
                                success = false;
                            break;

                        default:
                            break;
                        }
                    }
                    if (success) {
                        resultList.add(item);
                    }
                }

                view.setItemList(resultList);
            }
        });
    }

    /**
     * 자세한 설명.
     *
     */
    public static synchronized void initJsonBind() {
        new Thread(new Runnable() {


            @Override
            public void run() {

                Gson g1 = new Gson();

                allItems.clear();
                long time = System.currentTimeMillis();
                Type listType = new TypeToken<List<MItem>>()
                {
                }.getType();
                for (int i = 0; i < 19; i++) {
                    Gson g = new Gson();
                    String data = getText(i + ".json");
                    try {
                        allItems.addAll((Collection<? extends MItem>) g.fromJson(data, listType));
                    } catch (Exception e) {
                    }

                }
                System.out.println(System.currentTimeMillis() - time);
                System.out.println(allItems.size());
            }
        }).start();
    }

    /**
     * 자세한 설명.
     * 
     * @param item
     * @param keyCondition
     * @return
     */
    protected static String[] getConditionValue(MItem item, String keyCondition) {

        String[] returnvalue = null;

        switch (keyCondition) {
        case SearchConditionPanel.TITLE:
            returnvalue = new String[] { item.getN() };
            break;
        case SearchConditionPanel.COMPANY:
            returnvalue = item.getA();
            break;
        case SearchConditionPanel.SERIES:
            returnvalue = item.getP();
            break;
        case SearchConditionPanel.CHARACTER:
            returnvalue = item.getC();
            break;
        case SearchConditionPanel.TAG:
            returnvalue = item.getT();
            break;
        case SearchConditionPanel.LANGUAGE:
            returnvalue = new String[] { item.getL() };
            break;
        case SearchConditionPanel.ID:
            returnvalue = new String[] { item.getId() };
            break;
        case SearchConditionPanel.TYPE:
            returnvalue = new String[] { item.getType() };
            break;
        default:
            break;
        }
        if (returnvalue != null) {
            for (int i = 0; i < returnvalue.length; i++) {
                try {
                    if (returnvalue[i] != null)
                        returnvalue[i] = returnvalue[i].toLowerCase();
                } catch (Exception e) {

                }
            }
        }

        return returnvalue;
    }

}
