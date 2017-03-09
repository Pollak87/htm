/*
 * This software is the confidential and proprietary information of UZEN 
 * Commerce Co.,Ltd., Inc. You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with UZEN Commerce.
 */
package com.poak.htmdownloader.swing;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import com.poak.htmdownloader.DownloadJob;

/**
 * DownloadListPanel
 * <p>
 * description...
 * </p> 
 * @author pollak
 * @since 0.1 2017. 3. 8.
 * @version 0.1
 * 
 */
public class DownloadListPanel extends JScrollPane {

    private static final long serialVersionUID = -821665563481707927L;
    private DownloadProgressPanel progressPanel;
    private JPanel main;
    private int count = 0;

    public DownloadListPanel(DownloadProgressPanel progressPanel) {
        this.progressPanel = progressPanel;
        setOpaque(true);
        setMinimumSize(new Dimension(700, 325));
        setPreferredSize(new Dimension(650, 325));
        setMaximumSize(new Dimension(700, 325));

        if (main == null) {
            main = new JPanel();
            main.setMinimumSize(new Dimension(650, 100));
            main.setPreferredSize(new Dimension(600, 100));
            main.setMaximumSize(new Dimension(650, 100));

            main.setLayout(new FlowLayout());
            setViewportView(main);
            revalidate();
        }
    }


    public void addJob(DownloadJob job) {
        count++;

        JLabel label = new JLabel(job.getItem().getN());
        label.setPreferredSize(new Dimension(600, 15));
        JProgressBar jproBar = new JProgressBar();
        jproBar.setUI(new GradientPalletProgressBarUI());
        jproBar.setPreferredSize(new Dimension(600, 15));
        jproBar.setValue(0);
        main.add(label);
        main.add(jproBar);

        job.setProgressManager(new ProgressManager() {

            @Override
            public void setProgress(int progress) {

                EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        jproBar.setValue(progress);
                    }
                });
            }
        });

        main.setPreferredSize(new Dimension(600, 100 + (count * 35)));

        progressPanel.addJob(job);
        revalidate();

    }

    public static interface ProgressManager {
        void setProgress(int progress);
    }
}
