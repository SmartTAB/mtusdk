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
import java.util.stream.Stream;

public class SoExtractor {
    public static void main(String[] args) {
        new SoExtractor().extract();
    }

    public void extract() {
        Stream.of("libMTUSDKJ.so",
                        "libmtmms.so",
                        "libmtscra.so",
                        "libmtusdk.a")
                .map(getClass()::getResource)
                .filter(Objects::nonNull)
                .forEach(this::copyIfNecessary);
    }

    /**
     * Copies a resource file to the destination if it is absent or different.
     */
    private void copyIfNecessary(final URL resourceUrl) {
        try {
            Path destinationPath = Paths.get(".", new File(resourceUrl.getPath()).getName()); // Target root folder
            if (shouldCopyFile(resourceUrl, destinationPath)) {
                copyFile(resourceUrl, destinationPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
