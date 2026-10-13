package Filesorter.sorter;

import Filesorter.model.AccountRecord;

import java.util.Comparator;

public class RecordComparator
        implements Comparator<AccountRecord> {

    @Override
    public int compare(
            AccountRecord a,
            AccountRecord b) {
        int result =
                a.getAccount()
                        .compareTo(
                                b.getAccount()
                        );

        if (result != 0) {
            return result;
        }

        return a.getAccountNumber()
                .compareTo(
                        b.getAccountNumber()
                );
    }
}