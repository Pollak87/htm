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
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

/**
 * DownloadProgressPanel
 * <p>
 * description...
 * </p>
 * 
 * @author pollak
 * @since 0.1 2017. 3. 8.
 * @version 0.1
 */
public class InitDownloadProgressPanel extends JScrollPane {

    private static final long serialVersionUID = 357621937481426495L;
    private JPanel main;
    private static String baseUrl = "https://ltn.hitomi.la/galleries";

    public InitDownloadProgressPanel() {
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

    private int threadCount = 7;

    public void initJson() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 20; i++) {
                    while (threadCount == 0) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                        }
                    }
                    threadCount--;
                    createJobThread(i).start();
                }
            }
        }).start();
    }

    int totalCount = 0;
    int itemCount = 0;

    private Thread createJobThread(int count) {

        return new Thread(new Runnable() {

            @Override
            public void run() {

                try {

                    JLabel label = new JLabel(count + ".json");
                    label.setPreferredSize(new Dimension(100, 15));
                    JProgressBar jproBar = new JProgressBar();
                    jproBar.setPreferredSize(new Dimension(500, 15));
                    jproBar.setUI(new GradientPalletProgressBarUI());
                    jproBar.setValue(0);

                    EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            itemCount++;
                            main.add(label);
                            main.add(jproBar);
                            main.setPreferredSize(new Dimension(600, 50 + (itemCount * 20)));
                            revalidate();
                        }
                    });

                    String path = InitDownloadProgressPanel.class.getResource("").getPath();
                    File file = new File(path, count + ".json");
                    if (!file.exists()) {
                        File file2 = new File(path);
                        file2.mkdirs();
                    }
                    FileOutputStream fileOutput = new FileOutputStream(file);

                    URL url = new URL(baseUrl + count + ".json");
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = conn.getInputStream();
                        int lenghtOfFile = conn.getContentLength();
                        int downloadedSize = 0;
                        byte[] buffer = new byte[1024];
                        int bufferLength = 0;

                        int current = 0;
                        while ((bufferLength = inputStream.read(buffer)) > 0) {

                            fileOutput.write(buffer, 0, bufferLength);
                            downloadedSize += bufferLength;

                            int progress = (int) ((downloadedSize * 100) / lenghtOfFile);

                            if ((current) < progress) {
                                current = progress;
                                EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        jproBar.setValue(progress);
                                    }
                                });
                            }

                        }

                        fileOutput.close();
                        conn.disconnect();
                    } else {
                        threadCount++;
                        return;
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    threadCount++;
                }
                if (threadCount == 5) {
                    Test.initJsonBind();
                }
            }
        });

    }

}
