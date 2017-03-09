package com.poak.htmdownloader.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.poak.htmdownloader.MItem;

/*
 * This software is the confidential and proprietary information of UZEN 
 * Commerce Co.,Ltd., Inc. You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with UZEN Commerce.
 */

/**
 * SearchResult
 * <p>
 * description...
 * </p> 
 * @author mia
 * @since 0.1 2017. 3. 7.
 * @version 0.1
 * 
 */
public class SearchResult extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -8778441094924598312L;
    private MItem item;
    private JLabel label;
    
    /**
     * @param actionListener
     */
    public SearchResult(MItem item, ActionListener actionListener) {

        this.item = item;

        setMinimumSize(new Dimension(700, 100));
        setPreferredSize(new Dimension(650, 100));
        setMaximumSize(new Dimension(700, 100));

        setLayout(new BorderLayout());

        JPanel content = new JPanel();

        label = new JLabel("empty");

        label.setPreferredSize(new Dimension(100, 100));

        add(label, BorderLayout.WEST);

        content.setMinimumSize(new Dimension(500, 100));
        content.setPreferredSize(new Dimension(450, 100));
        content.setMaximumSize(new Dimension(500, 100));

        JLabel label = new JLabel("Title : " + item.getN());
        content.add(label);

        JLabel id = new JLabel("ID : " + item.getId());
        content.add(id);
        if (item.getT() != null) {
            JLabel tag = new JLabel(item.getTagName());

            Font font = tag.getFont().deriveFont(Font.PLAIN);
            tag.setFont(font);

            content.add(tag);
        }


        JButton download = new JButton("Download");

        download.addActionListener(actionListener);

        add(download, BorderLayout.EAST);

        add(content, BorderLayout.CENTER);
    }

    /**
     * 자세한 설명.
     */
    public void setImage(JLabel jLabel2) {

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    remove(label);
                    add(jLabel2, BorderLayout.WEST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
            // String path =
            // "http://img.babathe.com/upload/display/corner/DC_MAIN_MID_BNR/170224_Tilbury_springpresale_sb.jpg";

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
}
