package com.k1ts.app.service;

import com.k1ts.app.model.BiggestLie;
import com.k1ts.app.model.Link;
import com.k1ts.app.persistence.InMemoryLinkRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LinkServiceTest {

    @Test
    void shouldCalculateBiggestLie() {
        InMemoryLinkRepository repository = new InMemoryLinkRepository();
        LinkService service = new LinkService(repository);

        Link youtube1 = new Link(
                "https://youtube.com/a",
                "youtube.com",
                LocalDateTime.now(),
                0,
                1);

        Link youtube2 = new Link(
                "https://youtube.com/b",
                "youtube.com",
                LocalDateTime.now(),
                0,
                1);

        Link youtube3 = new Link(
                "https://youtube.com/c",
                "youtube.com",
                LocalDateTime.now(),
                0,
                1);

        Link github1 = new Link(
                "https://github.com/a",
                "github.com",
                LocalDateTime.now(),
                1,
                1);

        Link github2 = new Link(
                "https://github.com/b",
                "github.com",
                LocalDateTime.now(),
                1,
                1);

        repository.save(youtube1);
        repository.save(youtube2);
        repository.save(youtube3);

        repository.save(github1);
        repository.save(github2);

        BiggestLie lie = service.calculateBiggestLie();

        assertEquals("youtube.com", lie.getDomain());

        assertEquals(3, lie.getSavedCount());
        assertEquals(0, lie.getOpenedCount());
    }

    @Test
    void shouldReturnNoneWhenNoCandidateExists() {
        InMemoryLinkRepository repository = new InMemoryLinkRepository();
        LinkService service = new LinkService(repository);
        BiggestLie lie = service.calculateBiggestLie();
        assertEquals("none", lie.getDomain());
    }
}