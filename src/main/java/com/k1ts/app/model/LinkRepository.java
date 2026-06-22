package com.k1ts.app.model;

import java.util.List;
import java.util.Optional;

public interface LinkRepository {

    List<Link> findAll();

    Optional<Link> findByUrl(String url);

    void save(Link link);

    void update(Link link);
}