package com.k1ts.app.model;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class Statistics {
    private int totalLinks;
    private int openedLinks;
    private int ignoredLinks;
    private double openRate;
    private int forgottenLinks;
    private Map<String, Integer> topDomains = new LinkedHashMap<>();
}