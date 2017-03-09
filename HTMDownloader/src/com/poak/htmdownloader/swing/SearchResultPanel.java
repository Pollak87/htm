package com.poak.htmdownloader.swing;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.poak.htmdownloader.DownloadJob;
import com.poak.htmdownloader.HInfo;
import com.poak.htmdownloader.MItem;
import com.poak.htmdownloader.Main;

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
                    listPanel.addJob(new DownloadJob(Main.getImageInfo(mItem.getId()), mItem));
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
                    List<HInfo> imageInfo = Main.getImageInfo(mItem.getItem().getId());
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

    public BufferedImage resizeImage(final URL url, final Dimension size) throws IOException {
        final BufferedImage image = ImageIO.read(url);
        final BufferedImage resized = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = resized.createGraphics();
        g.drawImage(image, 0, 0, size.width, size.height, null);
        g.dispose();
        return resized;
    }
}
