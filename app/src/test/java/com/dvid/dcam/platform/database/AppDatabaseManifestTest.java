package com.dvid.dcam.platform.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dvid.dcam.platform.database.entities.UserAuthMethodEntity;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

final class AppDatabaseManifestTest {
    private static final Pattern BLOCK_COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    private static final Pattern LINE_COMMENT = Pattern.compile("//.*");
    private static final Pattern PACKAGE = Pattern.compile("package\\s+([A-Za-z0-9_.]+)\\s*;");
    private static final Pattern CLASS = Pattern.compile("\\bclass\\s+([A-Za-z0-9_]+)\\b");
    private static final Pattern ENTITY_CLASS_LITERAL = Pattern.compile("\\b([A-Za-z0-9_]+)\\.class\\b");

    @Test
    void everyRoomEntityIsRegisteredInAppDatabase() throws IOException {
        Path mainJava = mainJavaRoot();
        Set<String> allEntities = roomEntities(mainJava);
        Set<String> registeredEntities = registeredEntities(mainJava.resolve(
                "com/dvid/dcam/platform/database/AppDatabase.java"));

        assertEquals(allEntities, registeredEntities,
                "Every @Entity must be listed in AppDatabase.entities so the fresh-install schema is explicit.");
    }

    @Test
    void userAuthenticationSchemaCannotRegressToPlaintextPasswords() {
        Set<String> fields = new TreeSet<>();
        for (Field field : UserAuthMethodEntity.class.getFields()) fields.add(field.getName());

        assertFalse(fields.contains("passwordText"));
        assertTrue(fields.containsAll(Set.of(
                "credentialAlgorithm",
                "credentialSalt",
                "credentialHash",
                "credentialIterations")));
    }

    private static Path mainJavaRoot() {
        Path moduleRoot = Path.of("src/main/java");
        if (Files.exists(moduleRoot)) return moduleRoot;
        return Path.of("app/src/main/java");
    }

    private static Set<String> roomEntities(Path mainJava) throws IOException {
        Set<String> entities = new TreeSet<>();
        try (Stream<Path> files = Files.walk(mainJava)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".java")).toList()) {
                String source = codeOnly(Files.readString(file, StandardCharsets.UTF_8));
                if (!source.contains("@Entity")) continue;
                entities.add(simpleClassName(source));
            }
        }
        return entities;
    }

    private static Set<String> registeredEntities(Path appDatabaseJava) throws IOException {
        String source = codeOnly(Files.readString(appDatabaseJava, StandardCharsets.UTF_8));
        Set<String> entities = new TreeSet<>();
        Matcher matcher = ENTITY_CLASS_LITERAL.matcher(databaseEntitiesBlock(source));
        while (matcher.find()) {
            entities.add(matcher.group(1));
        }
        return entities;
    }

    private static String databaseEntitiesBlock(String source) {
        int entitiesStart = source.indexOf("entities");
        int blockStart = source.indexOf('{', entitiesStart);
        int blockEnd = source.indexOf('}', blockStart);
        if (entitiesStart < 0 || blockStart < 0 || blockEnd < 0) {
            throw new IllegalStateException("AppDatabase @Database annotation must declare entities = {...}");
        }
        return source.substring(blockStart, blockEnd + 1);
    }

    private static String codeOnly(String source) {
        return LINE_COMMENT.matcher(BLOCK_COMMENT.matcher(source).replaceAll("")).replaceAll("");
    }

    private static String simpleClassName(String source) {
        Matcher packageMatcher = PACKAGE.matcher(source);
        if (!packageMatcher.find()) {
            throw new IllegalStateException("Entity file is missing a package declaration");
        }
        Matcher classMatcher = CLASS.matcher(source);
        if (!classMatcher.find()) {
            throw new IllegalStateException("Entity file is missing a class declaration in "
                    + packageMatcher.group(1));
        }
        return classMatcher.group(1);
    }
}
