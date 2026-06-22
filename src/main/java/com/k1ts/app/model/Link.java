package com.k1ts.app.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Link {

    private String url;
    private String domain;
    private LocalDateTime createdAt;
    private int openCount;
    private int copyCount;

    public void incrementCopyCount() {
        copyCount++;
    }

    public void incrementOpenCount() {
        openCount++;
    }
}