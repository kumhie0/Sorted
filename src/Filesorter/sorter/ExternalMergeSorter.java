package Filesorter.sorter;

import Filesorter.model.AccountRecord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class ExternalMergeSorter {

    private static final int CHUNK_SIZE = 100_000;

    private static final RecordComparator COMPARATOR =
            new RecordComparator();


    public static List<Path> createSortedChunks(
            Path inputFile,
            Path tempDirectory) throws IOException {

        List<Path> chunkFiles = new ArrayList<>();

        Files.createDirectories(tempDirectory);

        try (BufferedReader reader =
                     Files.newBufferedReader(
                             inputFile,
                             StandardCharsets.US_ASCII)) {


            reader.readLine();
            List<AccountRecord> chunk =
                    new ArrayList<>(CHUNK_SIZE);


            String line;

            int chunkNumber = 0;


            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",", 2);

                if (parts.length != 2) {
                    throw new IOException(
                            "Invalid CSV line: " + line
                    );
                }


                AccountRecord record =
                        new AccountRecord(
                                parts[0],
                                parts[1]
                        );


                chunk.add(record);


                if (chunk.size() >= CHUNK_SIZE) {

                    Path chunkFile =
                            writeSortedChunk(
                                    chunk,
                                    tempDirectory,
                                    chunkNumber
                            );

                    chunkFiles.add(chunkFile);

                    chunk.clear();

                    chunkNumber++;
                }
            }


            if (!chunk.isEmpty()) {

                Path chunkFile =
                        writeSortedChunk(
                                chunk,
                                tempDirectory,
                                chunkNumber
                        );

                chunkFiles.add(chunkFile);
            }
        }


        return chunkFiles;
    }


    private static Path writeSortedChunk(
            List<AccountRecord> chunk,
            Path tempDirectory,
            int chunkNumber) throws IOException {
        chunk.sort(COMPARATOR);
        Path chunkFile =
                tempDirectory.resolve(
                        "chunk_" + chunkNumber + ".csv"
                );


        try (BufferedWriter writer =
                     Files.newBufferedWriter(
                             chunkFile,
                             StandardCharsets.US_ASCII)) {


            for (AccountRecord record : chunk) {

                writer.write(record.toString());
                writer.newLine();
            }
        }



        return chunkFile;
    }

    private static AccountRecord parseRecord(String line) {

        String[] parts = line.split(",", 2);

        return new AccountRecord(
                parts[0],
                parts[1]
        );
    }

    private static class MergeEntry {


        private final AccountRecord record;

        private final int chunkIndex;


        public MergeEntry(
                AccountRecord record,
                int chunkIndex) {

            this.record = record;
            this.chunkIndex = chunkIndex;
        }
    }

    public static void mergeChunks(
            List<Path> chunkFiles,
            Path outputFile) throws IOException {

        List<BufferedReader> readers =
                new ArrayList<>();


        PriorityQueue<MergeEntry> queue =
                new PriorityQueue<>(
                        (a, b) -> COMPARATOR.compare(
                                a.record,
                                b.record
                        )
                );

        if (outputFile.getParent() != null) {
            Files.createDirectories(
                    outputFile.getParent()
            );
        }


        try {

            for (int i = 0; i < chunkFiles.size(); i++) {

                BufferedReader reader =
                        Files.newBufferedReader(
                                chunkFiles.get(i),
                                StandardCharsets.US_ASCII
                        );


                readers.add(reader);

                String line = reader.readLine();


                if (line != null) {

                    AccountRecord record =
                            parseRecord(line);

                    queue.add(
                            new MergeEntry(
                                    record,
                                    i
                            )
                    );
                }
            }

            try (BufferedWriter writer =
                         Files.newBufferedWriter(
                                 outputFile,
                                 StandardCharsets.US_ASCII)) {


                writer.write("account,Account#");
                writer.newLine();


                while (!queue.isEmpty()) {

                    MergeEntry smallest =
                            queue.poll();


                    writer.write(
                            smallest.record.toString()
                    );

                    writer.newLine();

                    BufferedReader sourceReader =
                            readers.get(
                                    smallest.chunkIndex
                            );

                    String nextLine =
                            sourceReader.readLine();

                    if (nextLine != null) {

                        AccountRecord nextRecord =
                                parseRecord(nextLine);


                        queue.add(
                                new MergeEntry(
                                        nextRecord,
                                        smallest.chunkIndex
                                )
                        );
                    }
                }
            }

        } finally {

            for (BufferedReader reader : readers) {

                if (reader != null) {
                    reader.close();
                }
            }
        }
    }

    public static void sort(
            Path inputFile,
            Path outputFile,
            Path tempDirectory) throws IOException {

        List<Path> chunkFiles =
                createSortedChunks(
                        inputFile,
                        tempDirectory
                );


        System.out.println(
                "Created "
                        + chunkFiles.size()
                        + " sorted chunks."
        );



        mergeChunks(
                chunkFiles,
                outputFile
        );

        for (Path chunkFile : chunkFiles) {

            Files.deleteIfExists(
                    chunkFile
            );
        }
    }
}