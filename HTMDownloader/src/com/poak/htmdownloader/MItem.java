/*
 * This software is the confidential and proprietary information of UZEN 
 * Commerce Co.,Ltd., Inc. You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with UZEN Commerce.
 */
package com.poak.htmdownloader;

/**
 * MItem
 * <p>
 * description...
 * </p> 
 * @author pollak
 * @since 0.1 2017. 3. 6.
 * @version 0.1
 * 
 */
public class MItem {
    private String n; // 제목
    private String type; // 종류 망가 동인지 애니
    private String l; // 언어
    private String id;
    private String[] p; // 시리즈
    private String[] a; // 제작사
    private String[] c; // 캐릭터
    private String[] t; // 태그

    /**
     * @return the n
     */
    public String getN() {
        return n;
    }

    /**
     * @return the c
     */
    public String[] getC() {
        return c;
    }

    /**
     * @param c
     *            the c to set
     */
    public void setC(String[] c) {
        this.c = c;
    }

    /**
     * @param n
     *            the n to set
     */
    public void setN(String n) {
        this.n = n;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the l
     */
    public String getL() {
        return l;
    }

    /**
     * @param l
     *            the l to set
     */
    public void setL(String l) {
        this.l = l;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the p
     */
    public String[] getP() {
        return p;
    }

    /**
     * @param p
     *            the p to set
     */
    public void setP(String[] p) {
        this.p = p;
    }

    /**
     * @return the a
     */
    public String[] getA() {
        return a;
    }

    /**
     * @param a
     *            the a to set
     */
    public void setA(String[] a) {
        this.a = a;
    }

    /**
     * @return the t
     */
    public String[] getT() {
        return t;
    }

    /**
     * @param t
     *            the t to set
     */
    public void setT(String[] t) {
        this.t = t;
    }

    public String getTagName() {
        StringBuffer sb = new StringBuffer();
        for (String tag : t) {
            sb.append(tag + " ");
        }
        return sb.toString().replace("female:", "♀").replace("male:", "♂");
    }
}
