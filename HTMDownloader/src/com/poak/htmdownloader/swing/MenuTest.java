package com.poak.htmdownloader.swing;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.poak.htmdownloader.MItem;

public class MenuTest extends JFrame {

    private static final long serialVersionUID = 4479002929952819844L;

    private DownloadProgressPanel progressPanel = new DownloadProgressPanel();
    private DownloadListPanel listPanel = new DownloadListPanel(progressPanel);
    private SearchConditionPanel conditionPanel = new SearchConditionPanel();
    private SearchResultPanel resultPanel = new SearchResultPanel(listPanel);
    private InitDownloadProgressPanel initPanel = new InitDownloadProgressPanel();

    public MenuTest(ActionListener searchListener) {

        setTitle("HTM Download");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(150, 150, 350, 350);
        setSize(700, 770);
        setVisible(true);

        // 메뉴 바생성
        JMenuBar mu = new JMenuBar();
        JMenu menu1 = new JMenu("Menu(P)");
        menu1.setMnemonic('p');

        JMenuItem item1 = new JMenuItem("Init Based Data");
        item1.setMnemonic('o');
        item1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                JOptionPane.showMessageDialog(MenuTest.this, "Start update", "Alert", JOptionPane.INFORMATION_MESSAGE);
                initPanel.initJson();

            }
        });
        menu1.add(item1);

        JMenuItem item2 = new JMenuItem("Save Directory");
        item2.setMnemonic('s');
        item2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                String s = (String) JOptionPane.showInputDialog(
                        MenuTest.this,
                        "Please input the path\n"
                                + "the files will be saved.",
                        "Save Directory",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        Test.getDownloadDir());
                if (s != null) {
                    Test.setDownloadDir(s);
                }

            }
        });
        menu1.add(item2);

        mu.add(menu1);

        // 프레임에 부착
        setJMenuBar(mu);

        JTabbedPane tPane = new JTabbedPane();

        JPanel searchPanel = new JPanel();
        tPane.addTab("Search", searchPanel);
        searchPanel.setLayout(new FlowLayout());

        searchPanel.add(conditionPanel);
        searchPanel.add(resultPanel);

        conditionPanel.setSearchListener(searchListener);

        JPanel downloadPanel = new JPanel();
        tPane.addTab("Download", downloadPanel);
        downloadPanel.setLayout(new FlowLayout());

        downloadPanel.add(listPanel);
        downloadPanel.add(progressPanel);

        JPanel settingPanel = new JPanel();
        tPane.addTab("Settings", settingPanel);

        settingPanel.add(initPanel);

        getContentPane().add(tPane);
    }

    public List<ConditionField> getConditions() {
        return conditionPanel.getConditionFields();
    }

    /**
     * 자세한 설명.
     *
     * @param allItems
     */
    public void setItemList(Collection<MItem> collection) {
        resultPanel.setiItemList(new ArrayList<MItem>(collection));
    }

    /**
     * 자세한 설명.
     *
     * @param allItems
     */
    public void setItemList(List<MItem> collection) {
        resultPanel.clearItems();
        resultPanel.setiItemList(collection);
    }

}