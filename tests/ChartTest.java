package vn.khoaikhoai.hocchu;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Compares each occupied chart cell, in order, against the separately stored candidate fixture. */
public class ChartTest {
    private static final class Cell {
        final int row, column, category;
        final String label;
        Cell(int row, int column, int category, String label) {
            this.row = row; this.column = column; this.category = category; this.label = label;
        }
        public String toString() { return "row " + row + ", column " + column + " (" + label + ")"; }
    }

    private static List<Cell> readReference(Path path) throws Exception {
        List<Cell> cells = new ArrayList<>();
        int[] rowCounts = {16,13,14,13,13,13,13,13,13,13,13,13,13,12};
        int row = 0;
        for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] fields = line.split("\\t", -1);
            if (fields.length != 3) throw new AssertionError("Malformed fixture line: " + line);
            row++;
            if (row > rowCounts.length || Integer.parseInt(fields[0]) != row)
                throw new AssertionError("Unexpected fixture row " + fields[0]);
            int category = Integer.parseInt(fields[1]);
            int expectedCategory = row <= 2 ? 0 : row == 3 ? 1 : 2;
            if (category != expectedCategory) throw new AssertionError("Wrong fixture category at row " + row);
            String[] labels = fields[2].split(" ", -1);
            if (labels.length != rowCounts[row - 1]) throw new AssertionError("Wrong fixture width at row " + row);
            for (int i = 0; i < labels.length; i++) {
                if (labels[i].isEmpty()) throw new AssertionError("Empty occupied cell at row " + row);
                cells.add(new Cell(row, i + 1, category, labels[i]));
            }
        }
        if (row != rowCounts.length) throw new AssertionError("Wrong fixture row count: " + row);
        return cells;
    }

    private static List<String> labels(int category) {
        List<String> out = new ArrayList<>();
        for (LearningData.Item item : LearningData.items(category)) out.add(item.label);
        return out;
    }

    private static List<String> compare(List<Cell> reference, List<List<String>> actual) {
        List<String> errors = new ArrayList<>();
        for (int category = 0; category < 3; category++) {
            List<Cell> expected = new ArrayList<>();
            for (Cell cell : reference) if (cell.category == category) expected.add(cell);
            List<String> got = actual.get(category);
            if (expected.size() != got.size())
                errors.add("category " + category + " count expected " + expected.size() + ", actual " + got.size());
            for (int i = 0; i < Math.min(expected.size(), got.size()); i++) {
                Cell cell = expected.get(i);
                if (!cell.label.equals(got.get(i)))
                    errors.add(cell + ": expected " + cell.label + ", got " + got.get(i));
            }
            if (got.size() > expected.size())
                errors.add("category " + category + " extra cells: " + got.subList(expected.size(), got.size()));
            if (expected.size() > got.size())
                errors.add("category " + category + " missing cells: " + expected.subList(got.size(), expected.size()));
        }
        return errors;
    }

    private static List<List<String>> expectedLists(List<Cell> cells) {
        List<List<String>> out = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        for (Cell cell : cells) out.get(cell.category).add(cell.label);
        return out;
    }

    private static void checkMutation(List<Cell> cells, List<List<String>> mutated, String name) {
        if (compare(cells, mutated).isEmpty()) throw new AssertionError("Comparator missed " + name);
    }

    private static void testComparator(List<Cell> cells) {
        List<List<String>> altered = expectedLists(cells);
        altered.get(2).set(0, "án");
        checkMutation(cells, altered, "altered diacritic");
        List<List<String>> missing = expectedLists(cells);
        missing.get(2).remove(0);
        checkMutation(cells, missing, "missing cell");
        List<List<String>> extra = expectedLists(cells);
        extra.get(2).add("extra");
        checkMutation(cells, extra, "extra cell");
        List<List<String>> swapped = expectedLists(cells);
        java.util.Collections.swap(swapped.get(2), 0, 1);
        checkMutation(cells, swapped, "reordered cells");
        List<List<String>> moved = expectedLists(cells);
        moved.get(2).add(0, moved.get(1).remove(moved.get(1).size() - 1));
        checkMutation(cells, moved, "wrong category");
    }

    public static void main(String[] args) throws Exception {
        Path fixture = Paths.get(args.length == 0 ? "tests/chart-reference.tsv" : args[0]);
        List<Cell> reference = readReference(fixture);
        testComparator(reference);
        List<List<String>> actual = Arrays.asList(labels(0), labels(1), labels(2));
        List<String> errors = compare(reference, actual);
        if (!LearningData.alphabet().equals(actual.get(0))) errors.add("alphabet() differs from category 0");
        for (LearningData.Item item : LearningData.items(2))
            if (item.label.equals("ên") && item.example.equals("đèn"))
                errors.add("incorrect example đèn for ên");
        if (!errors.isEmpty()) throw new AssertionError(String.join("\n", errors));
        System.out.println("PASS: compared " + reference.size() + " occupied candidate cells, categories and order; comparator mutations detected");
    }
}
