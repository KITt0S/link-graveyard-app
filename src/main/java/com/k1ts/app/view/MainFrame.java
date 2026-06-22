package com.k1ts.app.view;

import com.k1ts.app.model.DomainScore;
import com.k1ts.app.model.Link;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class MainFrame extends JFrame {

    private final JLabel totalLinksLabel;
    private final JLabel openedLinksLabel;
    private final JLabel ignoredLinksLabel;
    private final JLabel openRateLabel;
    private final JLabel forgottenLinksLabel;

    private final JButton refreshButton;

    private final JTable linksTable;
    private final LinksTableModel tableModel;

    private final DefaultListModel<Link> shameListModel;
    private final JList<Link> shameList;

    private final DefaultListModel<String> bestDomainsModel;
    private final DefaultListModel<String> worstDomainsModel;

    private final JList<String> bestDomainsList;
    private final JList<String> worstDomainsList;

    public MainFrame() {
        setTitle("Link Graveyard");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1500, 800);
        setLocationRelativeTo(null);

        totalLinksLabel = new JLabel("Saved: 0");
        openedLinksLabel = new JLabel("Opened: 0");
        ignoredLinksLabel = new JLabel("Ignored: 0");
        openRateLabel = new JLabel("Open rate: 0%");
        forgottenLinksLabel = new JLabel("Forgotten: 0");

        refreshButton = new JButton("Refresh");

        tableModel = new LinksTableModel();
        linksTable = new JTable(tableModel);

        shameListModel = new DefaultListModel<>();
        shameList = new JList<>(shameListModel);

        shameList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.getDomain() + " — " + value.getUrl());

            if (isSelected) {
                label.setOpaque(true);
                label.setBackground(list.getSelectionBackground());
            }

            return label;
        });

        bestDomainsModel = new DefaultListModel<>();
        worstDomainsModel = new DefaultListModel<>();

        bestDomainsList = new JList<>(bestDomainsModel);

        worstDomainsList = new JList<>(worstDomainsModel);

        JPanel root = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        topPanel.add(totalLinksLabel);
        topPanel.add(openedLinksLabel);
        topPanel.add(ignoredLinksLabel);
        topPanel.add(openRateLabel);
        topPanel.add(forgottenLinksLabel);
        topPanel.add(refreshButton);

        root.add(topPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane();

        splitPane.setDividerLocation(950);

        splitPane.setLeftComponent(new JScrollPane(linksTable));

        JPanel rightPanel = new JPanel(new GridLayout(3, 1));

        JPanel shamePanel = new JPanel(new BorderLayout());
        shamePanel.add(new JLabel("🔥 Daily Shame List"), BorderLayout.NORTH);
        shamePanel.add(new JScrollPane(shameList), BorderLayout.CENTER);

        JPanel bestPanel = new JPanel(new BorderLayout());
        bestPanel.add(new JLabel("🏆 Best Domains"), BorderLayout.NORTH);
        bestPanel.add(new JScrollPane(bestDomainsList), BorderLayout.CENTER);

        JPanel worstPanel = new JPanel(new BorderLayout());

        worstPanel.add(new JLabel("💀 Worst Domains"), BorderLayout.NORTH);

        worstPanel.add(new JScrollPane(worstDomainsList), BorderLayout.CENTER);

        rightPanel.add(shamePanel);
        rightPanel.add(bestPanel);
        rightPanel.add(worstPanel);

        splitPane.setRightComponent(rightPanel);

        root.add(splitPane, BorderLayout.CENTER);

        setContentPane(root);
    }

    public void setStatistics(
            int total,
            int opened,
            int ignored,
            double openRate,
            int forgotten) {

        totalLinksLabel.setText("Saved: " + total);
        openedLinksLabel.setText("Opened: " + opened);
        ignoredLinksLabel.setText("Ignored: " + ignored);

        openRateLabel.setText(String.format("Open rate: %.1f%%", openRate * 100)
        );

        forgottenLinksLabel.setText("Forgotten: " + forgotten);
    }

    public void setLinks(List<Link> links) {
        tableModel.setLinks(links);
    }

    public void setShameList(List<Link> links) {
        shameListModel.clear();
        links.forEach(shameListModel::addElement);
    }

    public void setDomainScores(List<DomainScore> scores) {
        bestDomainsModel.clear();
        worstDomainsModel.clear();

        scores.stream()
                .limit(5)
                .forEach(score -> bestDomainsModel.addElement(
                        String.format(
                                "%s (%.0f%%)",
                                score.getDomain(),
                                score.getScore() * 100)));

        scores
                .stream()
                .sorted(Comparator.comparing(DomainScore::getScore))
                .limit(5)
                .forEach(score -> worstDomainsModel.addElement(
                        String.format(
                                "%s (%.0f%%)",
                                score.getDomain(),
                                score.getScore() * 100)));
    }

    public Link getSelectedLink() {
        int row = linksTable.getSelectedRow();

        if (row < 0) {
            return null;
        }

        return tableModel.getLinkAt(row);
    }

    public void addRefreshListener(Runnable listener) {
        refreshButton.addActionListener(e -> listener.run());
    }

    public void addOpenLinkListener(Runnable listener) {

        linksTable.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e) {

                        if (e.getClickCount() == 2) {
                            listener.run();
                        }
                    }
                }
        );
    }

    public void addShameListDoubleClickListener(Consumer<Link> listener) {

        shameList.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {

                        if (e.getClickCount() == 2) {

                            int index = shameList.locationToIndex(e.getPoint());

                            if (index >= 0) {
                                listener.accept(shameListModel.get(index));
                            }
                        }
                    }
                }
        );
    }
}