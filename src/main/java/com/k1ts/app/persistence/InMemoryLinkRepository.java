package com.k1ts.app.persistence;

import com.k1ts.app.model.Link;
import com.k1ts.app.model.LinkRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryLinkRepository implements LinkRepository {

    private final List<Link> links = new ArrayList<>();

    @Override
    public void save(Link link) {
        links.add(link);
    }

    @Override
    public void update(Link link) {
        // same object reference
    }

    @Override
    public List<Link> findAll() {
        return new ArrayList<>(links);
    }

    @Override
    public Optional<Link> findByUrl(String url) {

        return links.stream()
                .filter(link -> link.getUrl().equals(url))
                .findFirst();
    }
}