package Filesorter.generator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                    "abcdefghijklmnopqrstuvwxyz" +
                    "0123456789";

    private static final int MAX_LENGTH = 32;

    public static void generate(String fileName, long numberOfRecords)
            throws IOException {

        Path path = Paths.get(fileName);

        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        try (BufferedWriter writer =
                     Files.newBufferedWriter(
                             path,
                             StandardCharsets.US_ASCII)) {

            // CSV header
            writer.write("account,Account#");
            writer.newLine();

            for (long i = 0; i < numberOfRecords; i++) {

                String account = randomString();
                String accountNumber = randomString();

                writer.write(account);
                writer.write(',');
                writer.write(accountNumber);
                writer.newLine();
            }
        }
    }

    private static String randomString() {

        ThreadLocalRandom random = ThreadLocalRandom.current();

        int length = random.nextInt(1, MAX_LENGTH + 1);

        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {

            int index = random.nextInt(CHARACTERS.length());

            result.append(CHARACTERS.charAt(index));
        }

        return result.toString();
    }
}