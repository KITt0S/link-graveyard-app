package com.k1ts.app;

import com.k1ts.app.controller.MainController;
import com.k1ts.app.persistence.JsonLinkRepository;
import com.k1ts.app.service.ClipboardMonitor;
import com.k1ts.app.service.LinkService;
import com.k1ts.app.view.MainFrame;

import javax.swing.*;
import java.nio.file.Path;

public class LinkGraveyardApp {

    public static void main(String[] args) {

        JsonLinkRepository repository = new JsonLinkRepository(Path.of("data", "links.json"));

        LinkService linkService = new LinkService(repository);

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            MainController controller = new MainController(linkService, mainFrame);
            ClipboardMonitor clipboardMonitor = new ClipboardMonitor();
            clipboardMonitor.addListener(controller);
            controller.initialize();
            clipboardMonitor.start();
            Runtime.getRuntime().addShutdownHook(new Thread(clipboardMonitor::stop));
            mainFrame.setVisible(true);
        });
    }
}