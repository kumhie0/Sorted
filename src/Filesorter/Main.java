package Filesorter;

import Filesorter.generator.DataGenerator;
import Filesorter.sorter.ExternalMergeSorter;
import Filesorter.sorter.SortedFileVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    private static final Path INPUT_FILE =
            Paths.get("Data/input.csv");

    private static final Path OUTPUT_FILE =
            Paths.get("Data/sorted.csv");

    private static final Path TEMP_DIRECTORY =
            Paths.get("Temp");

    private static final long MIN_RECORDS = 1_000_000;


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            printMainMenu();

            int choice = readChoice(scanner);

            switch (choice) {

                case 1:
                    generateData(scanner);
                    break;

                case 2:
                    sortData();
                    break;

                case 3:
                    verifyData();
                    break;

                case 0:
                    System.out.println();
                    System.out.println(
                            "Program terminated."
                    );

                    scanner.close();
                    return;

                default:
                    System.out.println();
                    System.out.println(
                            "Invalid choice. Please choose 0 - 3."
                    );
            }
        }
    }

    private static void printMainMenu() {

        System.out.println();
        System.out.println(
                "=================================================="
        );
        System.out.println(
                "                   FILE SORTER"
        );
        System.out.println(
                "            External Merge Sort System"
        );
        System.out.println(
                "=================================================="
        );
        System.out.println(
                "  [1] Generate Random Data"
        );
        System.out.println(
                "  [2] Sort Data"
        );
        System.out.println(
                "  [3] Verify Sorted File"
        );
        System.out.println(
                "  [0] Exit"
        );
        System.out.println(
                "=================================================="
        );
        System.out.print(
                "Choose an option: "
        );
    }

    private static int readChoice(Scanner scanner) {

        if (!scanner.hasNextInt()) {

            scanner.nextLine();

            return -1;
        }

        int choice = scanner.nextInt();

        scanner.nextLine();

        return choice;
    }

    private static void generateData(Scanner scanner) {

        System.out.println();
        System.out.println(
                "=================================================="
        );
        System.out.println(
                "                  GENERATE DATA"
        );
        System.out.println(
                "=================================================="
        );

        System.out.print(
                "Number of records (minimum 1,000,000): "
        );

        if (!scanner.hasNextLong()) {

            System.out.println();
            System.out.println(
                    "[ERROR] Invalid number."
            );

            scanner.nextLine();
            return;
        }

        long numberOfRecords =
                scanner.nextLong();

        scanner.nextLine();


        if (numberOfRecords < MIN_RECORDS) {

            System.out.println();
            System.out.println(
                    "[ERROR] Number of records must be "
                            + "at least 1,000,000."
            );

            return;
        }


        System.out.println();
        System.out.println(
                "[PROCESSING] Generating random data..."
        );


        long start =
                System.currentTimeMillis();


        try {

            DataGenerator.generate(
                    INPUT_FILE.toString(),
                    numberOfRecords
            );


            long end =
                    System.currentTimeMillis();


            long fileSize =
                    Files.size(INPUT_FILE);


            System.out.println();
            System.out.println(
                    "[SUCCESS] Generation completed."
            );

            System.out.println();
            printSeparator();

            System.out.println(
                    "Output file     : "
                            + INPUT_FILE
            );

            System.out.println(
                    "Total records   : "
                            + String.format(
                            "%,d",
                            numberOfRecords
                    )
            );

            System.out.println(
                    "File size       : "
                            + formatFileSize(fileSize)
            );

            System.out.println(
                    "Generation time : "
                            + (end - start) / 1000.0
                            + " seconds"
            );

            printSeparator();


        } catch (IOException e) {

            System.out.println();
            System.out.println(
                    "[ERROR] Cannot generate data."
            );

            System.out.println(
                    "Reason: " + e.getMessage()
            );
        }
    }


    private static void sortData() {

        System.out.println();
        System.out.println(
                "=================================================="
        );
        System.out.println(
                "                    SORT DATA"
        );
        System.out.println(
                "               External Merge Sort"
        );
        System.out.println(
                "=================================================="
        );


        if (!Files.exists(INPUT_FILE)) {

            System.out.println();
            System.out.println(
                    "[ERROR] Input file does not exist."
            );

            System.out.println(
                    "Please generate data first."
            );

            return;
        }


        System.out.println();
        System.out.println(
                "Input : " + INPUT_FILE
        );

        System.out.println(
                "Output: " + OUTPUT_FILE
        );


        System.out.println();
        System.out.println(
                "[1/3] Creating sorted chunks "
                        + "and performing K-way merge..."
        );


        long sortStart =
                System.currentTimeMillis();


        try {

            ExternalMergeSorter.sort(
                    INPUT_FILE,
                    OUTPUT_FILE,
                    TEMP_DIRECTORY
            );


            long sortEnd =
                    System.currentTimeMillis();


            System.out.println();
            System.out.println(
                    "[2/3] Sorting completed."
            );


            System.out.println();
            System.out.println(
                    "[3/3] Verifying output..."
            );


            long verifyStart =
                    System.currentTimeMillis();


            boolean valid =
                    SortedFileVerifier.verify(
                            OUTPUT_FILE
                    );


            long verifyEnd =
                    System.currentTimeMillis();


            long inputSize =
                    Files.size(INPUT_FILE);

            long outputSize =
                    Files.size(OUTPUT_FILE);


            System.out.println();
            System.out.println(
                    "=================================================="
            );

            System.out.println(
                    "                     RESULT"
            );

            System.out.println(
                    "=================================================="
            );


            System.out.println(
                    "Sorted correctly  : "
                            + (valid ? "YES" : "NO")
            );


            System.out.println(
                    "Sort time         : "
                            + (sortEnd - sortStart)
                            / 1000.0
                            + " seconds"
            );


            System.out.println(
                    "Verification time : "
                            + (verifyEnd - verifyStart)
                            / 1000.0
                            + " seconds"
            );


            System.out.println(
                    "Input size        : "
                            + formatFileSize(inputSize)
            );


            System.out.println(
                    "Output size       : "
                            + formatFileSize(outputSize)
            );


            System.out.println(
                    "Input file        : "
                            + INPUT_FILE
            );


            System.out.println(
                    "Output file       : "
                            + OUTPUT_FILE
            );


            printSeparator();


            if (valid) {

                System.out.println(
                        "[SUCCESS] SORT COMPLETED SUCCESSFULLY"
                );

            } else {

                System.out.println(
                        "[FAILED] OUTPUT VERIFICATION FAILED"
                );
            }


        } catch (IOException e) {

            System.out.println();
            System.out.println(
                    "[ERROR] Sorting failed."
            );

            System.out.println(
                    "Reason: " + e.getMessage()
            );
        }
    }


    private static void verifyData() {

        System.out.println();
        System.out.println(
                "=================================================="
        );
        System.out.println(
                "                 VERIFY SORTED FILE"
        );
        System.out.println(
                "=================================================="
        );


        if (!Files.exists(OUTPUT_FILE)) {

            System.out.println();
            System.out.println(
                    "[ERROR] Sorted file does not exist."
            );

            System.out.println(
                    "Please sort the data first."
            );

            return;
        }


        System.out.println();
        System.out.println(
                "File: " + OUTPUT_FILE
        );

        System.out.println();
        System.out.println(
                "[PROCESSING] Verifying output..."
        );


        long start =
                System.currentTimeMillis();


        try {

            boolean valid =
                    SortedFileVerifier.verify(
                            OUTPUT_FILE
                    );


            long end =
                    System.currentTimeMillis();


            System.out.println();
            printSeparator();

            System.out.println(
                    "Verification time : "
                            + (end - start) / 1000.0
                            + " seconds"
            );

            System.out.println(
                    "File size         : "
                            + formatFileSize(
                            Files.size(OUTPUT_FILE)
                    )
            );

            System.out.println(
                    "Final result      : "
                            + (valid ? "VALID" : "INVALID")
            );

            printSeparator();


        } catch (IOException e) {

            System.out.println();
            System.out.println(
                    "[ERROR] Verification failed."
            );

            System.out.println(
                    "Reason: " + e.getMessage()
            );
        }
    }

    private static String formatFileSize(long bytes) {

        double megabytes =
                bytes / (1024.0 * 1024.0);

        return String.format(
                "%.2f MB (%,d bytes)",
                megabytes,
                bytes
        );
    }

    private static void printSeparator() {

        System.out.println(
                "--------------------------------------------------"
        );
    }
}