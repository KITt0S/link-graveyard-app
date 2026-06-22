package com.k1ts.app.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.k1ts.app.model.Link;
import com.k1ts.app.model.LinkRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonLinkRepository implements LinkRepository {

    private final Path filePath;
    private final ObjectMapper objectMapper;

    public JsonLinkRepository(Path filePath) {
        this.filePath = filePath;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        initializeStorage();
    }

    @Override
    public List<Link> findAll() {
        try {

            if (Files.notExists(filePath)) {
                return new ArrayList<>();
            }

            return objectMapper.readValue(
                    filePath.toFile(),
                    new TypeReference<>() {
                    });

        } catch (IOException e) {
            throw new RuntimeException("Failed to load links", e);
        }
    }

    @Override
    public Optional<Link> findByUrl(String url) {

        return findAll()
                .stream()
                .filter(link -> link.getUrl().equals(url))
                .findFirst();
    }

    @Override
    public void save(Link link) {

        List<Link> links = findAll();

        links.add(link);

        writeAll(links);
    }

    @Override
    public void update(Link updatedLink) {

        List<Link> links = findAll();

        for (int i = 0; i < links.size(); i++) {

            Link current = links.get(i);

            if (current.getUrl().equals(updatedLink.getUrl())) {

                links.set(i, updatedLink);

                break;
            }
        }

        writeAll(links);
    }

    private void writeAll(List<Link> links) {

        try {

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(filePath.toFile(), links);

        } catch (IOException e) {

            throw new RuntimeException("Failed to save links", e);
        }
    }

    private void initializeStorage() {

        try {

            Files.createDirectories(filePath.getParent());

            if (Files.notExists(filePath)) {

                Files.createFile(filePath);

                objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValue(filePath.toFile(), new ArrayList<Link>());
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to initialize storage",
                    e);
        }
    }
}