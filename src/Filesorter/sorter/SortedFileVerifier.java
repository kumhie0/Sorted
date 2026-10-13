package Filesorter.sorter;

import Filesorter.model.AccountRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SortedFileVerifier {


    public static boolean verify(Path file)
            throws IOException {

        RecordComparator comparator =
                new RecordComparator();

        try (BufferedReader reader =
                     Files.newBufferedReader(
                             file,
                             StandardCharsets.US_ASCII)) {

            // Bỏ qua header.
            String header = reader.readLine();

            if (!"account,Account#".equals(header)) {
                System.out.println("CSV header          : INVALID");
                return false;
            }

            AccountRecord previous = null;

            String line;

            long recordCount = 0;

            while ((line = reader.readLine()) != null) {

                /*
                 * Tách CSV thành đúng 2 field.
                 */
                String[] parts =
                        line.split(",", 2);

                if (parts.length != 2) {

                    System.out.println(
                            "Invalid CSV at record: "
                                    + (recordCount + 1)
                    );

                    return false;
                }


                String account =
                        parts[0];

                String accountNumber =
                        parts[1];


                if (account.isEmpty()
                        || accountNumber.isEmpty()) {

                    System.out.println(
                            "Empty field at record: "
                                    + (recordCount + 1)
                    );

                    return false;
                }


                if (account.length() > 32
                        || accountNumber.length() > 32) {

                    System.out.println(
                            "Field exceeds 32 characters "
                                    + "at record: "
                                    + (recordCount + 1)
                    );

                    return false;
                }

                if (!isAscii(account)
                        || !isAscii(accountNumber)) {

                    System.out.println(
                            "Non-ASCII character at record: "
                                    + (recordCount + 1)
                    );

                    return false;
                }


                AccountRecord current =
                        new AccountRecord(
                                account,
                                accountNumber
                        );



                if (previous != null
                        && comparator.compare(
                        previous,
                        current
                ) > 0) {

                    System.out.println();
                    System.out.println("SORT ERROR!");

                    System.out.println(
                            "At record : "
                                    + (recordCount + 1)
                    );

                    System.out.println(
                            "Previous  : " + previous
                    );

                    System.out.println(
                            "Current   : " + current
                    );

                    return false;
                }


                previous = current;

                recordCount++;
            }


            System.out.println();
            System.out.println(
                    "Verification completed."
            );

            System.out.println(
                    "Total records       : "
                            + String.format(
                            "%,d",
                            recordCount
                    )
            );
            System.out.println(
                    "CSV header          : VALID"
            );

            System.out.println(
                    "CSV format          : VALID"
            );

            System.out.println(
                    "Maximum field length: VALID"
            );

            System.out.println(
                    "ASCII data          : VALID"
            );

            System.out.println(
                    "Sort order          : VALID"
            );



            if (recordCount < 1_000_000) {

                System.out.println(
                        "Record count       : INVALID"
                );

                return false;
            }


            System.out.println(
                    "Record count         : VALID"
            );

            return true;
        }
    }



    private static boolean isAscii(String text) {

        for (int i = 0; i < text.length(); i++) {

            if (text.charAt(i) > 127) {
                return false;
            }
        }

        return true;
    }
}