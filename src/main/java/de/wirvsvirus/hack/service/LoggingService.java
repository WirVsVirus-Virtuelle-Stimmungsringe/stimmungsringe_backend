package de.wirvsvirus.hack.service;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.FileAppender;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class LoggingService {

    public Stream<String> readLogLines() {
        return
                enumerateLogFiles().stream()
                        .sorted(Comparator.comparing(logFile -> {
                            try {
                                return Files.getLastModifiedTime(logFile).toInstant();
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        }))
                        .flatMap(logFile -> {
                            try {
                                return Files.lines(logFile);
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        });
    }


    private List<Path> enumerateLogFiles() {

        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        return
                StreamEx.of(context.getLoggerList())
                        .flatMap(logger -> StreamEx.of(logger.iteratorForAppenders())
                                .filter(enumElement -> enumElement instanceof FileAppender)
                                .map(enumElement -> (FileAppender<?>) enumElement)
                                .map(fileAppender -> Paths.get(fileAppender.getFile())))
                        .collect(Collectors.toList());

    }
}
