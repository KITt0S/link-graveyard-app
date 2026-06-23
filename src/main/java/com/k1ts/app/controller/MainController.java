package com.k1ts.app.controller;

import com.k1ts.app.model.DomainScore;
import com.k1ts.app.model.Link;
import com.k1ts.app.model.Statistics;
import com.k1ts.app.service.ClipboardListener;
import com.k1ts.app.service.LinkService;
import com.k1ts.app.view.MainFrame;

import javax.swing.SwingUtilities;
import java.util.List;

public class MainController implements ClipboardListener {

    private final LinkService linkService;
    private final MainFrame mainFrame;

    public MainController(LinkService linkService, MainFrame mainFrame) {
        this.linkService = linkService;
        this.mainFrame = mainFrame;
    }

    public void initialize() {
        mainFrame.addRefreshListener(this::refreshView);
        mainFrame.addOpenLinkListener(this::openSelectedLink);
        mainFrame.addShameListDoubleClickListener(link -> {
            if (link != null) {
                linkService.openLink(link);
                refreshView();
            }
        });
        refreshView();
    }

    @Override
    public void onUrlDetected(String url) {
        linkService.saveCopiedUrl(url);
        refreshView();
    }

    private void openSelectedLink() {
        Link selectedLink = mainFrame.getSelectedLink();

        if (selectedLink == null) {
            return;
        }

        linkService.openLink(selectedLink);
        refreshView();
    }

    private void refreshView() {
        SwingUtilities.invokeLater(() -> {
            Statistics statistics = linkService.calculateStatistics();

            mainFrame.setStatistics(
                    statistics.getTotalLinks(),
                    statistics.getOpenedLinks(),
                    statistics.getIgnoredLinks(),
                    statistics.getOpenRate(),
                    statistics.getForgottenLinks());

            mainFrame.setLinks(linkService.getAllLinks());
            mainFrame.setShameList(linkService.getDailyShameList());
            mainFrame.setDomainScores(linkService.calculateDomainScores());
            mainFrame.setBiggestLie(linkService.calculateBiggestLie());
        });
    }
}