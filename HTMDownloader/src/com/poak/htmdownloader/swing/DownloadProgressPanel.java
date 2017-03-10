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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JButton;
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
    private static String animeUrl = "https://ba.hitomi.la/videos/";
    private List<DownloadJob> jobList = new ArrayList<DownloadJob>();
    private double[] aniProgressList;

    private Timer scheduler;
    private int finishJob = 0;
    private int animeSize;

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
                    if (!"anime".equals(curJob.getItem().getType())) {
                        itemCount = 0;
                        totalCount = curJob.getHinfos().size();
                        for (int i = 0; i < Test.getSpeed(); i++) {
                            if (!"anime".equals(curJob.getItem().getType())) {
                                if (curJob != null && !curJob.getHinfos().isEmpty())
                                    createJobThread().start();
                                else
                                    break;
                            }
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                            }
                        }
                    } else {
                        try {

                            URL url = new URL(animeUrl + curJob.getItem().getVideofilename());
                            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                            conn.setConnectTimeout(10000);
                            conn.setUseCaches(false);
                            int responseCode = conn.getResponseCode();
                            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_PARTIAL) {
                                InputStream inputStream = conn.getInputStream();
                                animeSize = conn.getContentLength();
                                inputStream.close();
                                RandomAccessFile fileOutput = new RandomAccessFile(Test.getDownloadDir() + curJob.getItem().getVideofilename(), "rw");
                                fileOutput.setLength(animeSize);
                            } else {
                                curJob = null;
                                return;
                            }

                        } catch (Exception e) {

                        }
                        aniProgressList = new double[getAniSpeed()];
                        for (int i = 0; i < getAniSpeed(); i++) {
                            createAnimeJobThread(i).start();
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                            }
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

            private JLabel label;
            private JProgressBar jproBar;
            private JButton reTry;
            boolean reTryGo = false;
            HInfo info;

            @Override
            public void run() {

                try {

                    boolean already = info != null;

                    while (info != null || (curJob != null && !curJob.getHinfos().isEmpty() && (info = curJob.getHinfos().remove(0)) != null)) {
                        finishJob++;
                        if (!already) {
                            label = new JLabel(info.getName());
                            label.setPreferredSize(new Dimension(100, 15));
                            jproBar = new JProgressBar();
                            jproBar.setPreferredSize(new Dimension(400, 15));
                            jproBar.setUI(new GradientPalletProgressBarUI());
                            jproBar.setValue(0);
                            reTry = new JButton("ReTry");
                            reTry.setPreferredSize(new Dimension(100, 15));

                            reTry.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    reTryGo = true;
                                }
                            });

                            EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    itemCount++;
                                    main.add(label);
                                    main.add(jproBar);
                                    main.add(reTry);
                                    main.setPreferredSize(new Dimension(600, 50 + (itemCount * 20)));
                                    revalidate();
                                }
                            });
                        }

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
                                    if (reTryGo) {
                                        throw new Exception();
                                    }
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

                            JProgressBar temp = jproBar;

                            EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    temp.setValue(100);
                                }
                            });

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
                                curJob.getProgressManager().setProgress((int) ((((double) (itemCount - finishJob)) / ((double) totalCount)) * 100));
                            continue;
                        }
                        info = null;
                        reTry.setEnabled(false);
                        finishJob--;
                        if (curJob != null)
                            curJob.getProgressManager().setProgress((int) ((((double) (itemCount - finishJob)) / ((double) totalCount)) * 100));
                    }
                    if (finishJob == 0) {
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    ZipUtils.zip(globalPath, globalPath + ".zip");
                                } catch (Exception e) {
                                }
                            }
                        }).start();
                        curJob = null;
                    }

                } catch (Exception ex) {
                    finishJob--;
                    reTryGo = false;
                    if (info != null) {
                        run();
                    }
                }
            }
        });

    }

    private Thread createAnimeJobThread(int count) {

        return new Thread(new Runnable() {

            private JButton reTry;
            private JLabel label;
            private JProgressBar jproBar;
            private boolean reTryGo = false;

            @Override
            public void run() {

                try {

                    if (curJob != null) {
                        finishJob++;
                        if (label == null) {
                            label = new JLabel((count + 1) + "/" + getAniSpeed());
                            label.setPreferredSize(new Dimension(100, 15));
                            jproBar = new JProgressBar();
                            jproBar.setPreferredSize(new Dimension(400, 15));
                            jproBar.setUI(new GradientPalletProgressBarUI());
                            jproBar.setValue(0);

                            reTry = new JButton("ReTry");
                            reTry.setPreferredSize(new Dimension(100, 15));

                            reTry.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    reTryGo = true;
                                }
                            });

                            EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    itemCount++;
                                    main.add(label);
                                    main.add(jproBar);
                                    main.add(reTry);
                                    main.setPreferredSize(new Dimension(600, 50 + (itemCount * 20)));
                                    revalidate();
                                }
                            });
                        }



                        int quota = animeSize / 1024 / getAniSpeed();

                        RandomAccessFile fileOutput = new RandomAccessFile(Test.getDownloadDir() + curJob.getItem().getVideofilename(), "rw");

                        fileOutput.seek(quota * 1024 * count);

                        URL url = new URL(animeUrl + curJob.getItem().getVideofilename());

                        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                        conn.setConnectTimeout(50000);
                        conn.setUseCaches(false);
                        conn.setRequestProperty("Range", "bytes=" + String.valueOf(((quota * 1024) * count) + 0) + '-');
                        int responseCode = conn.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_PARTIAL) {
                            InputStream inputStream = conn.getInputStream();
                            int lenghtOfFile = quota * 1024;
                            int remains = (quota * 1024);
                            int downloadedSize = 0;
                            byte[] buffer = new byte[1024];
                            int bufferLength = 0;

                            int current = 0;
                            while ((bufferLength = inputStream.read(buffer)) > 0) {
                                if (reTryGo) {
                                    throw new Exception();
                                }

                                remains -= bufferLength;

                                fileOutput.write(buffer, 0, bufferLength);
                                downloadedSize += bufferLength;

                                double progress = (double) ((downloadedSize * 100) / lenghtOfFile);

                                if ((current) < ((int) progress)) {
                                    current = (int) progress;
                                    EventQueue.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            jproBar.setValue((int) progress);
                                            aniProgressList[count] = (progress / getAniSpeed());

                                            double progress2 = 0;
                                            for (int i = 0; i < aniProgressList.length; i++) {
                                                progress2 += aniProgressList[i];
                                            }

                                            curJob.getProgressManager().setProgress((int) progress2);
                                        }
                                    });
                                }
                                if ((!(count + 1 == getAniSpeed())) && remains <= 0) {
                                    break;
                                }

                            }

                            EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    jproBar.setValue(100);
                                    aniProgressList[count] = 100 / getAniSpeed();

                                }
                            });
                            reTry.setEnabled(false);
                            fileOutput.close();
                            conn.disconnect();
                        } else if (responseCode == 416) {
                            jproBar.setValue(100);
                            aniProgressList[count] = 100 / getAniSpeed();
                        }

                        finishJob--;
                    }
                    if (finishJob == 0) {
                        curJob = null;
                    }

                } catch (Exception ex) {
                    finishJob--;
                    reTryGo = false;
                    run();
                }
            }
        });

    }

    /**
     * 자세한 설명.
     *
     * @return
     */
    protected int getAniSpeed() {
        return Test.getSpeed();
    }

    private static void combineFile(String nFilePath)
            throws FileNotFoundException, IOException {
        File nFiles = new File(nFilePath);
        String[] files = nFiles.list();

        List<String> str = new ArrayList<String>();

        for (int i = 0; i < files.length; i++) {
            str.add(files[i]);
        }
        Collections.sort(str, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                if (o1.replace(".mp4", "").length() == o2.replace(".mp4", "").length())
                    return o1.compareTo(o2);
                return o1.replace(".mp4", "").length() > o2.replace(".mp4", "").length() ? 1 : -1;
            }
        });

        File nFiles2 = new File(nFilePath + ".mp4");

        RandomAccessFile nFo = new RandomAccessFile(nFiles2.getAbsolutePath(), "rw");
        for (String file : str) {
            FileInputStream nFi = new FileInputStream(nFilePath + "/" + file);
            byte[] buf = new byte[1024];
            int readCnt = 0;
            while ((readCnt = nFi.read(buf)) > -1) {
                nFo.write(buf, 0, readCnt);
            }
        }
        nFo.close();
    }

    /**
     * 자세한 설명.
     *
     */
    public void threadPlus() {
        if (curJob != null && !curJob.getHinfos().isEmpty())
            createJobThread().start();
    }
}
