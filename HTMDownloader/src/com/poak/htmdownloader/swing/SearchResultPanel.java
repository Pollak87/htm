package com.poak.htmdownloader.swing;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.poak.htmdownloader.DownloadJob;
import com.poak.htmdownloader.HInfo;
import com.poak.htmdownloader.MItem;

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
 * @author mia
 * @since 0.1 2017. 3. 7.
 * @version 0.1
 * 
 */
public class SearchResultPanel extends JScrollPane {

    private static final long serialVersionUID = -6553807303067866716L;

    private static final String thumbnail = "https://btn.hitomi.la/smalltn/";
    private static String imageNamesUrl = "https://hitomi.la/galleries/";

    private List<MItem> htmItems;
    private int startIdx = 0;
    private int endIdx = 30;
    private int totalIdx = 0;

    private JLabel label;

    private JPanel panel;

    private DownloadListPanel listPanel;

    private JButton prev = new JButton("prev");
    private JButton next = new JButton("Next");

    private JPanel btns;

    private JLabel pageInfo;

    /**
     * @param listPanel
     */
    public SearchResultPanel(DownloadListPanel listPanel) {
        this.listPanel = listPanel;
        setOpaque(true);
        setMinimumSize(new Dimension(700, 450));
        setPreferredSize(new Dimension(650, 450));
        setMaximumSize(new Dimension(700, 450));
        label = new JLabel("empty");

        add(label);
        btns = new JPanel();
        btns.setPreferredSize(new Dimension(650, 50));
        btns.setLayout(new FlowLayout());
        prev.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (startIdx > 1) {
                    endIdx -= 30;
                    startIdx -= 30;
                    setiItemList(htmItems);
                    if (startIdx == 0) {
                        prev.setVisible(false);
                    }
                }

            }
        });
        prev.setVisible(false);
        next.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (totalIdx > endIdx) {
                    endIdx += 30;
                    startIdx += 30;
                    setiItemList(htmItems);
                    prev.setVisible(true);
                }
            }
        });
        pageInfo = new JLabel();
        pageInfo.setPreferredSize(new Dimension(100, 40));
        btns.add(prev);
        btns.add(pageInfo);
        btns.add(next);
    }

    int count = 0;

    public void clearItems() {
        startIdx = 0;
        endIdx = 30;
        prev.setVisible(false);
    }

    int thumbnailThreadingCount = 7;

    /**
     * 자세한 설명.
     *
     * @param htmItems
     */
    public void setiItemList(List<MItem> htmItems) {
        count++;
        thumbnailThreadingCount = 7;
        this.htmItems = htmItems;
        totalIdx = htmItems.size();
        if (label.isVisible()) {
            remove(label);
        }
        if (panel == null) {
            panel = new JPanel();
            setViewportView(panel);
        }
        panel.removeAll();

        panel.add(btns);
        int pageSize = (totalIdx / 30) + ((((((float) totalIdx) / 30) - (totalIdx / 30)) > 0) ? 1 : 0);

        next.setVisible(totalIdx > endIdx);
        pageInfo.setText((endIdx / 30) + "/" + pageSize);
        List<SearchResult> list = new ArrayList<SearchResult>();
        int height = 100;
        for (MItem mItem : htmItems.subList(startIdx, (endIdx < htmItems.size() ? endIdx : htmItems.size()))) {
            SearchResult label = new SearchResult(mItem, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    listPanel.addJob(new DownloadJob(getImageInfo(mItem.getId()), mItem));
                    JOptionPane.showMessageDialog(SearchResultPanel.this, "다운로드 대기열에 추가", "Open", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            list.add(label);
            panel.add(label);
            height += 105;
        }

        panel.setMinimumSize(new Dimension(670, height));
        panel.setPreferredSize(new Dimension(630, height));
        panel.setMaximumSize(new Dimension(670, height));

        revalidate();
        int temp = count;
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (SearchResult mItem : list) {
                    if (temp != count) {
                        break;
                    }
                    List<HInfo> imageInfo = getImageInfo(mItem.getItem().getId());
                    String firstName = imageInfo.get(0).getName();
                    String thumbnailImgPath = thumbnail + mItem.getItem().getId() + "/" + firstName + ".jpg";
                    while (thumbnailThreadingCount == 0 || temp != count) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                        }
                    }
                    if (temp != count) {
                        break;
                    }
                    thumbnailThreadingCount--;

                    Runnable runnable = new Runnable() {
                        public void run() {
                            try {
                                URL url = new URL(thumbnailImgPath);
                                BufferedImage image = resizeImage(url, new Dimension(100, 100));
                                JLabel label = new JLabel(new ImageIcon(image));
                                label.setPreferredSize(new Dimension(100, 100));
                                if (temp == count) {
                                    mItem.setImage(label);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (temp == count) {
                                    thumbnailThreadingCount--;
                                }
                            }
                        };
                    };
                    new Thread(runnable).start();

                }
            }
        }).start();

    }

    public List<HInfo> getImageInfo(String id) {
        HttpURLConnection http = null;
        String resultStr = null;

        BufferedReader read = null;

        try {
            URL u = new URL(imageNamesUrl + id + ".js");

            HttpsURLConnection https = (HttpsURLConnection) u.openConnection();
            http = https;
            http.setConnectTimeout(10000);
            http.setDoInput(true);
            http.connect();

            read = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = read.readLine()) != null) {
                sb.append(line);
            }
            resultStr = sb.toString();

            read.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof SSLHandshakeException) {
                JOptionPane.showMessageDialog(SearchResultPanel.this, "SSLHandshakeException", "Alert", JOptionPane.INFORMATION_MESSAGE);
            }
        } finally {
            if (http != null)
                http.disconnect();
        }

        if (resultStr == null) {
            return null;
        }
        resultStr = resultStr.replace("var galleryinfo = ", "");

        Type listType = new TypeToken<List<HInfo>>()
        {
        }.getType();

        Gson g = new Gson();

        List<HInfo> list = g.fromJson(resultStr, listType);

        return list;
    }

    public BufferedImage resizeImage(URL url, Dimension size) throws IOException {
        BufferedImage image = ImageIO.read(url);
        BufferedImage resized = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resized.createGraphics();
        g.drawImage(image, 0, 0, size.width, size.height, null);
        g.dispose();
        return resized;
    }
}
