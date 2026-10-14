# File Sorter – External Merge Sort

A Java console application for generating, sorting, and verifying large CSV files using the **External Merge Sort** algorithm.

The project is designed to process at least **1,000,000 records** without loading the entire dataset into memory.

## Features

- Generate random CSV data with at least 1,000,000 records
- Store records in the format:

```text
account,Account#
```

- Maximum field length: 32 characters
- ASCII data
- Sort records in ascending order by:
  1. `account`
  2. `Account#` when `account` values are equal
- Use External Merge Sort for memory-efficient processing
- Split the input into chunks of 100,000 records
- Sort each chunk individually
- Merge sorted chunks using a `PriorityQueue`
- Verify the final sorted file
- Measure generation, sorting, and verification time

## Project Structure

```text
Sorted
├── Data
│   ├── input.csv
│   └── sorted.csv
│
├── Temp
│   └── temporary chunk files
│
└── src
    └── Filesorter
        ├── Main.java
        │
        ├── generator
        │   └── DataGenerator.java
        │
        ├── model
        │   └── AccountRecord.java
        │
        └── sorter
            ├── ExternalMergeSorter.java
            ├── RecordComparator.java
            └── SortedFileVerifier.java
```

## How It Works

The program uses **External Merge Sort** so that the entire input file does not need to be loaded into RAM.

### Phase 1 – Create Sorted Chunks

The input file is read sequentially.

```text
input.csv
    |
    v
Read up to 100,000 records
    |
    v
Sort records in memory
    |
    v
Write sorted chunk to disk
    |
    v
Clear memory and continue
```

For example:

```text
input.csv
    |
    +--> chunk_0.csv
    +--> chunk_1.csv
    +--> chunk_2.csv
    +--> ...
```

Each chunk is individually sorted before being written to disk.

### Phase 2 – K-Way Merge

After all chunks are sorted, the program performs a **K-way merge**.

A `PriorityQueue` stores the current smallest unmerged record from each active chunk.

```text
Sorted Chunks
     |
     v
PriorityQueue
     |
     v
Select smallest record
     |
     v
Write to sorted.csv
     |
     v
Read next record from the same chunk
     |
     v
Repeat until the queue is empty
```

This produces the final globally sorted file.

## Sorting Order

Records are sorted lexicographically in ascending order.

The program first compares `account`.

If two records have the same `account`, it compares `Account#`.

Example:

```text
A,02
A,05
A,09
B,01
B,03
B,08
C,01
```

Equivalent ordering:

```text
ORDER BY account ASC, Account# ASC
```

## File Verification

After sorting, the program verifies the output file.

The verifier checks:

- Correct CSV header
- Exactly two fields per record
- Non-empty fields
- Maximum field length of 32 characters
- ASCII data
- Correct sorting order
- At least 1,000,000 records

The verification is performed sequentially, so the entire sorted file does not need to be loaded into memory.

## Console Menu

```text
==================================================
                   FILE SORTER
            External Merge Sort System
==================================================
  [1] Generate Random Data
  [2] Sort Data
  [3] Verify Sorted File
  [0] Exit
==================================================
```

### Option 1 – Generate Random Data

Creates:

```text
Data/input.csv
```

The user specifies the number of records, with a minimum of 1,000,000.

### Option 2 – Sort Data

Performs:

```text
input.csv
    |
    v
Create sorted chunks
    |
    v
K-way merge
    |
    v
sorted.csv
    |
    v
Verification
```

### Option 3 – Verify Sorted File

Independently verifies:

```text
Data/sorted.csv
```

and reports whether the file is valid.

## Example Result

A test with 1,000,000 records produced:

```text
Created 10 sorted chunks.

Verification completed.
Total records       : 1,000,000
CSV header          : VALID
CSV format          : VALID
Maximum field length: VALID
ASCII data          : VALID
Sort order          : VALID
Record count        : VALID

Sorted correctly    : YES
```

## Main Classes

| Class | Responsibility |
|---|---|
| `Main` | Console UI and program orchestration |
| `DataGenerator` | Generates random CSV records |
| `AccountRecord` | Represents one CSV record |
| `RecordComparator` | Defines the sorting order |
| `ExternalMergeSorter` | Performs External Merge Sort |
| `SortedFileVerifier` | Validates the final sorted file |

## Technologies

- Java
- Java NIO (`Path`, `Files`)
- `BufferedReader`
- `BufferedWriter`
- `ArrayList`
- `PriorityQueue`
- External Merge Sort

## Requirements

- Java JDK
- IntelliJ IDEA or another Java IDE

No external libraries are required.

## Author

**kumhie0**
