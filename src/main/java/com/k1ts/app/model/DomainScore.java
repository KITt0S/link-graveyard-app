package com.k1ts.app.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DomainScore {
    private final String domain;
    private final int savedCount;
    private final int openedCount;
    private final double score;
}