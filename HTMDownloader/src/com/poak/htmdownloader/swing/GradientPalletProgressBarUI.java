/*
 * This software is the confidential and proprietary information of UZEN 
 * Commerce Co.,Ltd., Inc. You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with UZEN Commerce.
 */
package com.poak.htmdownloader.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicProgressBarUI;

/**
 * GradientPalletProgressBarUI
 * <p>
 * description...
 * </p> 
 * @author pollak
 * @since 0.1 2017. 3. 8.
 * @version 0.1
 * 
 */
class GradientPalletProgressBarUI extends BasicProgressBarUI {
    private final int[] pallet;

    public GradientPalletProgressBarUI() {
        super();
        this.pallet = makeGradientPallet();
    }

    private static int[] makeGradientPallet() {
        BufferedImage image = new BufferedImage(100, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        Point2D start = new Point2D.Float(0f, 0f);
        Point2D end = new Point2D.Float(99f, 0f);
        float[] dist = { 0f, .5f, 1f };
        Color[] colors = { Color.RED, Color.YELLOW, Color.GREEN };
        g2.setPaint(new LinearGradientPaint(start, end, dist, colors));
        g2.fillRect(0, 0, 100, 1);
        g2.dispose();
        int width = image.getWidth(null);
        int[] pallet = new int[width];
        PixelGrabber pg = new PixelGrabber(image, 0, 0, width, 1, pallet, 0, width);
        try {
            pg.grabPixels();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return pallet;
    }

    private static Color getColorFromPallet(int[] pallet, float x) {
        if (x < 0d || x > 1d) {
            throw new IllegalArgumentException("Parameter outside of expected range");
        }
        int i = (int) (pallet.length * x);
        int max = pallet.length - 1;
        int index = i < 0 ? 0 : i > max ? max : i;
        return new Color(pallet[index] & 0x00ffffff);
        // translucent
        // int pix = pallet[index] & 0x00ffffff | (0x64 << 24);
        // return new Color(pix), true);
    }

    @Override
    public void paintDeterminate(Graphics g, JComponent c) {
        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth = progressBar.getWidth() - b.right - b.left;
        int barRectHeight = progressBar.getHeight() - b.top - b.bottom;
        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return;
        }
        // int cellLength = getCellLength();
        // int cellSpacing = getCellSpacing();
        // amount of progress to draw
        int amountFull = getAmountFull(b, barRectWidth, barRectHeight);

        // draw the cells
        if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
            float x = amountFull / (float) barRectWidth;
            g.setColor(getColorFromPallet(pallet, x));
            g.fillRect(b.left, b.top, amountFull, barRectHeight);
        } else { // VERTICAL
            float y = amountFull / (float) barRectHeight;
            g.setColor(getColorFromPallet(pallet, y));
            g.fillRect(b.left, barRectHeight + b.bottom - amountFull, barRectWidth, amountFull);
        }

        // Deal with possible text painting
        if (progressBar.isStringPainted()) {
            paintString(g, b.left, b.top, barRectWidth, barRectHeight, amountFull, b);
        }
    }
}
