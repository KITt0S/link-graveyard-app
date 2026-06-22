package com.k1ts.app.service;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClipboardMonitor {

    private final List<ClipboardListener> listeners;

    private final ScheduledExecutorService executor;

    private String lastClipboardValue;

    public ClipboardMonitor() {

        this.listeners = new ArrayList<>();

        this.executor = Executors.newSingleThreadScheduledExecutor();

        this.lastClipboardValue = "";
    }

    public void addListener(ClipboardListener listener) {
        listeners.add(listener);
    }

    public void start() {
        executor.scheduleAtFixedRate(
                this::checkClipboard,
                0,
                1,
                TimeUnit.SECONDS
        );
    }

    public void stop() {
        executor.shutdownNow();
    }

    private void checkClipboard() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable transferable = clipboard.getContents(null);

            if (transferable == null) {
                return;
            }

            if (!transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {

                return;
            }

            String text = (String) transferable.getTransferData(
                    DataFlavor.stringFlavor);

            if (text == null || text.isBlank()) {
                return;
            }

            text = text.trim();

            if (text.equals(lastClipboardValue)) {
                return;
            }

            lastClipboardValue = text;

            if (!isUrl(text)) {
                return;
            }

            notifyListeners(text);
        } catch (Exception e) {
            // Clipboard can temporarily be locked
            // by another process.
            e.printStackTrace();
        }
    }

    private void notifyListeners(String url) {
        for (ClipboardListener listener : listeners) {
            listener.onUrlDetected(url);
        }
    }

    private boolean isUrl(String value) {
        try {
            URI uri = URI.create(value);
            return uri.getScheme() != null && uri.getHost() != null;
        } catch (Exception e) {
            return false;
        }
    }
}