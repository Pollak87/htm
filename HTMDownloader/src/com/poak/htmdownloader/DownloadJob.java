/*
 * This software is the confidential and proprietary information of UZEN 
 * Commerce Co.,Ltd., Inc. You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with UZEN Commerce.
 */
package com.poak.htmdownloader;

import java.util.List;

import com.poak.htmdownloader.swing.DownloadListPanel.ProgressManager;

/**
 * DownloadJab
 * <p>
 * description...
 * </p> 
 * @author pollak
 * @since 0.1 2017. 3. 8.
 * @version 0.1
 * 
 */
public class DownloadJob {

    private List<HInfo> hinfos;
    private MItem item;
    private ProgressManager progressManager;

    /**
     * @param hinfos
     * @param item
     */
    public DownloadJob(List<HInfo> hinfos, MItem item) {
        super();
        this.hinfos = hinfos;
        this.item = item;
    }

    /**
     * @return the hinfos
     */
    public synchronized List<HInfo> getHinfos() {
        return hinfos;
    }

    /**
     * @param hinfos
     *            the hinfos to set
     */
    public void setHinfos(List<HInfo> hinfos) {
        this.hinfos = hinfos;
    }

    /**
     * @return the item
     */
    public MItem getItem() {
        return item;
    }

    /**
     * @param item
     *            the item to set
     */
    public void setItem(MItem item) {
        this.item = item;
    }
    
    /**
     * @return the progressManager
     */
    public ProgressManager getProgressManager() {
        return progressManager;
    }

    /**
     * @param progressManager
     *            the progressManager to set
     */
    public void setProgressManager(ProgressManager progressManager) {
        this.progressManager = progressManager;
    }

}
