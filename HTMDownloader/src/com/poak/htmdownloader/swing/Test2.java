/*
 * This software is the confidential and proprietary information of UZEN 
 * Commerce Co.,Ltd., Inc. You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with UZEN Commerce.
 */
package com.poak.htmdownloader.swing;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Test
 * <p>
 * description...
 * </p> 
 * @author pollak
 * @since 0.1 2017. 3. 6.
 * @version 0.1
 * 
 */
public class Test2 {

    private static String baseUrl = "https://ltn.hitomi.la/galleries";

    /**
     * 자세한 설명.
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            for (int j = 0; j < 21; j++) {
                URL url = new URL(baseUrl + j + ".json");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setUseCaches(false);

                String path = Test2.class.getResource("").getPath();
                File file = new File(path, j + ".json");

                RandomAccessFile fileOutput = new RandomAccessFile(file.getAbsolutePath(), "rw");

                int filelength = (int) fileOutput.length();
                fileOutput.seek(filelength);

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

                    boolean[] b = { false, false, false, false, false, false, false, false, false, false, false };

                    if (filelength < lenghtOfFile) {
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
                            filelength += bufferLength;
                            remains -= bufferLength;
                            fileOutput.write(buffer, 0, bufferLength);

                            downloadedSize += bufferLength;

                            int progress = (int) ((downloadedSize * 10) / lenghtOfFile);
                            if (b[progress] == false) {
                                b[progress] = true;
                                System.out.println("file : " + j + " progress : " + progress * 10);
                            }

                        }
                    }
                    fileOutput.close();
                    conn.disconnect();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
