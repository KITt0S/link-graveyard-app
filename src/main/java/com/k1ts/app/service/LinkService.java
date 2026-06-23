package com.k1ts.app.service;

import com.k1ts.app.model.*;

import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LinkService {
    private final LinkRepository repository;

    public LinkService(LinkRepository repository) {
        this.repository = repository;
    }

    public void saveCopiedUrl(String url) {
        if (url == null || url.isBlank()) {
            return;
        }

        repository.findByUrl(url)
                .ifPresentOrElse(
                        existingLink -> {
                            existingLink.incrementCopyCount();
                            repository.update(existingLink);
                        },
                        () -> {
                            Link link = new Link(
                                    url,
                                    extractDomain(url),
                                    LocalDateTime.now(),
                                    0,
                                    1);

                            repository.save(link);
                        }
                );
    }

    public List<Link> getAllLinks() {
        return repository.findAll();
    }

    public void openLink(Link link) {
        try {
            String url = link.getUrl();
            new ProcessBuilder(
                    "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
                    "--incognito",
                    url
            ).start();
            link.incrementOpenCount();
            repository.update(link);
        } catch (Exception e) {
            throw new RuntimeException("Failed to open link", e);
        }
    }

    public Statistics calculateStatistics() {
        List<Link> links = repository.findAll();
        Statistics statistics = new Statistics();
        statistics.setTotalLinks(links.size());

        int openedLinks = (int) links
                .stream()
                .filter(l -> l.getOpenCount() > 0)
                .count();

        statistics.setOpenedLinks(openedLinks);

        statistics.setIgnoredLinks(statistics.getTotalLinks() - openedLinks);

        if (statistics.getTotalLinks() > 0) {
            statistics.setOpenRate((double) openedLinks / statistics.getTotalLinks());
        } else {
            statistics.setOpenRate(0.0);
        }

        // 🧠 NEW: Forgotten links logic
        int forgotten = (int) links
                .stream()
                .filter(l -> l.getOpenCount() == 0)
                .filter(l ->
                        ChronoUnit.DAYS.between(
                                l.getCreatedAt(),
                                java.time.LocalDateTime.now()
                        ) >= 7
                )
                .count();

        statistics.setForgottenLinks(forgotten);

        var topDomains = links
                .stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        com.k1ts.app.model.Link::getDomain,
                        java.util.stream.Collectors.summingInt(
                                com.k1ts.app.model.Link::getCopyCount
                        )
                ))
                .entrySet()
                .stream()
                .sorted(java.util.Map.Entry.<String, Integer>
                        comparingByValue(
                        java.util.Comparator.reverseOrder()
                ))
                .limit(10)
                .collect(java.util.stream.Collectors.toMap(
                        java.util.Map.Entry::getKey,
                        java.util.Map.Entry::getValue,
                        (a, b) -> a,
                        java.util.LinkedHashMap::new
                ));

        statistics.setTopDomains(topDomains);

        return statistics;
    }

    private String extractDomain(String url) {

        try {

            URI uri = URI.create(url);

            String host = uri.getHost();

            if (host == null) {
                return "unknown";
            }

            if (host.startsWith("www.")) {
                return host.substring(4);
            }

            return host;

        } catch (Exception e) {

            return "unknown";
        }
    }

    public List<Link> getDailyShameList() {
        List<Link> links = repository.findAll();

        return links
                .stream()
                .filter(l -> l.getOpenCount() == 0)
                .filter(l ->
                        java.time.temporal.ChronoUnit.DAYS.between(
                                l.getCreatedAt(),
                                java.time.LocalDateTime.now()
                        ) >= 7
                )
                .sorted(Comparator.comparing(Link::getCreatedAt))
                .limit(10)
                .toList();
    }

    public List<DomainScore> calculateDomainScores() {
        List<Link> links = repository.findAll();

        Map<String, List<Link>> grouped = links
                .stream()
                .collect(Collectors.groupingBy(Link::getDomain));

        List<DomainScore> scores = new ArrayList<>();

        for (Map.Entry<String, List<Link>> entry : grouped.entrySet()) {
            String domain = entry.getKey();
            List<Link> domainLinks = entry.getValue();

            int savedCount = domainLinks.size();

            int openedCount = (int) domainLinks
                    .stream()
                    .filter(l -> l.getOpenCount() > 0)
                    .count();

            double score = savedCount == 0 ? 0 : (double) openedCount / savedCount;

            scores.add(new DomainScore(domain, savedCount, openedCount, score));
        }

        scores.sort(Comparator.comparing(DomainScore::getScore).reversed());

        return scores;
    }

    public BiggestLie calculateBiggestLie() {
        List<DomainScore> scores = calculateDomainScores();

        return scores
                .stream()
                .filter(score -> score.getSavedCount() >= 3)
                .min(Comparator.comparing(DomainScore::getScore))
                .map(score -> new BiggestLie(
                        score.getDomain(),
                        score.getSavedCount(),
                        score.getOpenedCount(),
                        score.getScore()))
                .orElse(new BiggestLie("none", 0, 0, 0));
    }

    public DigitalPersonality calculatePersonality() {
        Statistics statistics = calculateStatistics();

        double openRate = statistics.getOpenRate();

        if (openRate < 0.10) {
            return new DigitalPersonality("Graveyard Keeper", "You collect links and rarely return to them.");
        }

        if (openRate < 0.25) {
            return new DigitalPersonality("Digital Hoarder", "You save much more than you consume.");
        }

        if (openRate < 0.50) {
            return new DigitalPersonality("Curious Collector", "You explore many ideas and revisit some of them.");
        }

        if (openRate < 0.75) {
            return new DigitalPersonality("Intentional Reader", "Most saved links eventually get your attention.");
        }

        return new DigitalPersonality("Link Assassin", "Very few saved links escape your attention.");
    }
}