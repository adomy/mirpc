/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.samples.simple.api;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * @author adomyzhao
 * @version $Id: DemoDTO.java, v 0.1 2021年03月24日 5:29 PM adomyzhao Exp $
 */
public class DemoDTO implements Serializable {

    private String word;

    private String mark;

    /**
     * Getter method for property word.
     *
     * @return property value of word
     */
    public String getWord() {
        return word;
    }

    /**
     * Setter method for property word.
     *
     * @param word value to be assigned to property word
     */
    public void setWord(String word) {
        this.word = word;
    }

    /**
     * Getter method for property mark.
     *
     * @return property value of mark
     */
    public String getMark() {
        return mark;
    }

    /**
     * Setter method for property mark.
     *
     * @param mark value to be assigned to property mark
     */
    public void setMark(String mark) {
        this.mark = mark;
    }

    /**
     * toString
     * 
     * @return
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", DemoDTO.class.getSimpleName() + "[", "]")
            .add("word='" + word + "'").add("mark='" + mark + "'").toString();
    }
}