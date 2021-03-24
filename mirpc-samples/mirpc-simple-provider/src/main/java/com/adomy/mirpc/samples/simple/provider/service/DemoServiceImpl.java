/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.samples.simple.provider.service;

import com.adomy.mirpc.samples.simple.api.DemoDTO;
import com.adomy.mirpc.samples.simple.api.DemoService;

/**
 * @author adomyzhao
 * @version $Id: DemoServiceImpl.java, v 0.1 2021年03月24日 5:31 PM adomyzhao Exp $
 */
public class DemoServiceImpl implements DemoService {

    /**
     * test
     * 
     * @param word
     * @return
     */
    @Override
    public DemoDTO test(String word) {
        DemoDTO dto = new DemoDTO();
        dto.setMark("test");
        dto.setWord(word);

        return dto;
    }
}