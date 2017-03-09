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
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import com.poak.htmdownloader.DownloadJob;
import com.poak.htmdownloader.HInfo;

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
public class DownloadProgressPanel extends JScrollPane {

    private static final long serialVersionUID = 357621937481426495L;
    private JPanel main;
    private DownloadJob curJob;
    private static String baseUrl = "https://ba.hitomi.la/galleries/";
    private List<DownloadJob> jobList = new ArrayList<DownloadJob>();

    private Timer scheduler;
    private int finishJob = 0;

    public DownloadProgressPanel() {
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

        scheduler = new Timer();

        TimerTask job = new TimerTask() {

            @Override
            public void run() {
                if (curJob == null && !jobList.isEmpty() && finishJob >= 0) {
                    finishJob = 0;
                    main.removeAll();
                    curJob = jobList.remove(0);

                    itemCount = 0;
                    totalCount = curJob.getHinfos().size();
                    for (int i = 0; i < Test.getSpeed(); i++) {
                        if (curJob != null && !curJob.getHinfos().isEmpty())
                            createJobThread().start();
                        else
                            break;
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        };

        scheduler.schedule(job, 1000, 1000);
    }

    public void addJob(DownloadJob job) {
        this.jobList.add(job);
    }

    int totalCount = 0;
    int itemCount = 0;

    private String globalPath;

    private Thread createJobThread() {

        return new Thread(new Runnable() {

            @Override
            public void run() {

                try {

                    HInfo info = null;
                    while (curJob != null && !curJob.getHinfos().isEmpty() && (info = curJob.getHinfos().remove(0)) != null) {
                        finishJob++;
                        JLabel label = new JLabel(info.getName());
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

                        String name = info.getName();

                        String path = Test.getDownloadDir() + curJob.getItem().getN().replaceAll("\\W+", "_") + "_" + curJob.getItem().getId();
                        globalPath = path;
                        File file = new File(path, name);
                        if (!file.exists()) {
                            File file2 = new File(path);
                            file2.mkdirs();
                        }
                        RandomAccessFile fileOutput = new RandomAccessFile(file.getAbsolutePath(), "rw");

                        int filelength = (int) fileOutput.length();
                        fileOutput.seek(filelength);

                        URL url = new URL(baseUrl + curJob.getItem().getId() + "/" + name);
                        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                        conn.setConnectTimeout(10000);
                        conn.setUseCaches(false);
                        if (filelength != 0) {
                            conn.setRequestProperty("Range", "bytes=" + String.valueOf(filelength) + '-');
                        }
                        int responseCode = conn.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_PARTIAL) {
                            InputStream inputStream = conn.getInputStream();
                            int remains = conn.getContentLength();
                            int lenghtOfFile = remains + filelength;
                            int downloadedSize = filelength;
                            byte[] buffer = new byte[1024];
                            int bufferLength = 0;


                            int current = 0;
                            if (filelength < lenghtOfFile) {
                                while ((bufferLength = inputStream.read(buffer)) > 0) {
                                    filelength += bufferLength;
                                    remains -= bufferLength;

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
                            } else {
                                EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        jproBar.setValue(100);
                                    }
                                });
                            }
                            fileOutput.close();
                            conn.disconnect();
                        } else if (responseCode == 416) {
                            finishJob--;
                            EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    jproBar.setValue(100);
                                }
                            });
                            if (curJob != null)
                                curJob.getProgressManager().setProgress((int) ((((double) itemCount) / ((double) totalCount)) * 100));
                            continue;
                        }

                        finishJob--;
                        if (curJob != null)
                            curJob.getProgressManager().setProgress((int) ((((double) itemCount) / ((double) totalCount)) * 100));
                    }
                    if (finishJob == 0) {
                        ZipUtils.zip(globalPath, globalPath + ".zip");
                        curJob = null;
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

}
