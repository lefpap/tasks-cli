package com.lefpap.taskscli.lib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class CsvUtils {

    private final String delimiter;

    private CsvUtils(String delimiter) {
        this.delimiter = delimiter;
    }

    public static CsvUtils create(String delimiter) {
        return new CsvUtils(delimiter);
    }

    public List<String[]> readCsv(Path filePath) throws IOException {
        if (Files.notExists(filePath)) {
            return List.of();
        }

        try (var lines = Files.lines(filePath)) {
            return lines.map(line -> line.split(delimiter))
                .toList();
        }
    }

    public void writeCsv(Path filePath, List<String[]> data) throws IOException {
        List<String> lines = data.stream()
            .map(row -> String.join(delimiter, row))
            .toList();

        Files.write(filePath, lines);
    }
}
