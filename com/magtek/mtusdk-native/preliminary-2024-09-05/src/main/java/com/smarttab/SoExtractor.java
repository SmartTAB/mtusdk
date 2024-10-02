package com.smarttab;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SoExtractor {

    private final static String NAME_MT_USDK_J = "libMTUSDKJ.so";
    private final static String NAME_MT_MMS = "libmtmms.so";
    private final static String NAME_MT_SCRA = "libmtscra.so";

    public static void main(String[] args) {
        System.out.println(new SoExtractor().extract());
    }

    public Set<Path> extract() {
        return Stream.of(NAME_MT_USDK_J,
                        NAME_MT_MMS,
                        NAME_MT_SCRA)
                .map(getClass()::getResource)
                .filter(Objects::nonNull)
                .map(this::copyIfNecessary)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Copies a resource file to the destination if it is absent or different.
     */
    private Path copyIfNecessary(final URL resourceUrl) {
        Path destinationPath = null;
        try {
            destinationPath = resolveDestinationPath(resourceUrl); // Target root folder
            if (shouldCopyFile(resourceUrl, destinationPath)) {
                copyFile(resourceUrl, destinationPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destinationPath;
    }

    private Path resolveDestinationPath(final URL resourceUrl) {
        final Path fileName = Paths.get(new File(resourceUrl.getPath()).getName()).normalize();
        if (NAME_MT_USDK_J.equals(fileName.toString())) {
            final Path rootFolder = Paths.get(new File(SoExtractor.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath()).normalize();
            return (rootFolder.getFileName().toString().contains(".jar") ? rootFolder.getParent() : rootFolder).resolve(fileName);
        } else if (NAME_MT_MMS.equals(fileName.toString()) || NAME_MT_SCRA.equals(fileName.toString())) {
            return Paths.get(".", new File(resourceUrl.getPath()).getName()); // Target root folder
        }
        throw new IllegalArgumentException("Unsupported file name:" + fileName.toString());
    }

    /**
     * Determines if the file should be copied by checking if it is absent or different.
     */
    private boolean shouldCopyFile(URL resourceUrl, Path destinationPath) throws IOException {
        if (!Files.exists(destinationPath)) {
            return true; // File doesn't exist, should be copied
        }

        // Compare file sizes (as a simple check for difference)
        long existingFileSize = Files.size(destinationPath);
        long resourceFileSize = resourceUrl.openConnection().getContentLengthLong();

        return existingFileSize != resourceFileSize; // If sizes differ, copy the file
    }

    /**
     * Copies the file from the resource URL to the destination directory.
     */
    private void copyFile(URL resourceUrl, Path destinationPath) throws IOException {
        System.out.println("Copying file: " + destinationPath.toAbsolutePath());
        // Ensure the destination directory exists
        Files.createDirectories(destinationPath.getParent());

        // Copy the file from the resource to the destination
        try (InputStream is = resourceUrl.openStream()) {
            Files.copy(is, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}