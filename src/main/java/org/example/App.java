package org.example;

import java.util.*;

record PriceData(String hour, int price) {
}

public class App {
    static List<PriceData> data = new ArrayList<>() {{
        add(new PriceData("00-01", 0));
        add(new PriceData("01-02", 0));
        add(new PriceData("02-03", 0));
        add(new PriceData("03-04", 0));
        add(new PriceData("04-05", 0));
        add(new PriceData("05-06", 0));
        add(new PriceData("06-07", 0));
        add(new PriceData("07-08", 0));
        add(new PriceData("08-09", 0));
        add(new PriceData("09-10", 0));
        add(new PriceData("10-11", 0));
        add(new PriceData("11-12", 0));
        add(new PriceData("12-13", 0));
        add(new PriceData("13-14", 0));
        add(new PriceData("14-15", 0));
        add(new PriceData("15-16", 0));
        add(new PriceData("16-17", 0));
        add(new PriceData("17-18", 0));
        add(new PriceData("18-19", 0));
        add(new PriceData("19-20", 0));
        add(new PriceData("20-21", 0));
        add(new PriceData("21-22", 0));
        add(new PriceData("22-23", 0));
        add(new PriceData("23-24", 0));
    }};

    public static void main(String[] args) {
        Locale.setDefault(Locale.of("sv", "SE"));
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            System.out.print("""
                    Elpriser
                    ========
                    1. Inmatning
                    2. Min, Max och Medel
                    3. Sortera
                    4. Bästa Laddningstid (4h)
                    5. Visualisering
                    e. Avsluta
                    """);
            String action = scanner.nextLine();

            switch (action) {
                case "e", "E" -> exit = true;
                case "1" -> addData(scanner);
                case "2" -> minMaxAverage();
                case "3" -> sortData();
                case "4" -> bestCharge();
                case "5" -> visualizeData();
            }
        }
    }

    public static void addData(Scanner scanner) {
        data.replaceAll(priceData -> new PriceData(priceData.hour(), scanner.nextInt()));
        scanner.nextLine();
    }

    public static void minMaxAverage() {
        PriceData min = min();
        PriceData max = max();
        System.out.printf("""
                Lägsta pris: %s, %d öre/kWh
                Högsta pris: %s, %d öre/kWh
                Medelpris: %.2f öre/kWh
                """, min.hour(), min.price(), max.hour(), max.price(), average(data));
    }

    public static void sortData() {
        List<PriceData> sorted = sortValues();
        for (PriceData data : sorted) {
            System.out.print(data.hour() + " " + data.price() + " öre\n");
        }
    }

    public static void bestCharge() {
        System.out.print(bestAveragePriceSpan(data, 4));
    }

    public static void visualizeData() {
        final int MAX = max().price();
        final int MIN = min().price();

        final int ROW_COUNT = 6;
        final int COLUMN_COUNT = data.size();
        final float DIFFERENCE = (MAX - MIN) / (ROW_COUNT - 1f);
        int MAX_LENGTH = Integer.toString(MAX).length();
        int MIN_LENGTH = Integer.toString(MIN).length();
        int longest = Math.max(MAX_LENGTH, MIN_LENGTH);

        for (int row = ROW_COUNT; row > 0; row--) {
            StringBuilder output = new StringBuilder();
            int lowerBound = (row == 1) ? MIN : (int) (MAX - (ROW_COUNT - row) * DIFFERENCE);

            if (row == ROW_COUNT) {
                String spaces = MAX_LENGTH < longest ? addSpaces(longest - MAX_LENGTH) : "";
                output.append(spaces).append(MAX).append("|");
            } else if (row == 1) {
                String spaces = MIN_LENGTH < longest ? addSpaces(longest - MIN_LENGTH) : "";
                output.append(spaces).append(MIN).append("|");
            } else {
                output.append(addSpaces(longest)).append("|");
            }

            for (int column = 0; column < COLUMN_COUNT; column++) {
                int currentPrice = data.get(column).price();
                if (currentPrice >= lowerBound) {
                    output.append("  x");
                } else {
                    output.append("   ");
                }
            }
            System.out.print(output + "\n");
        }

        System.out.print(addSpaces(longest) + "|------------------------------------------------------------------------\n");
        System.out.print(addSpaces(longest) + "| 00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23\n");
    }

    // Util methods

    static PriceData max() {
        return Collections.max(data, Comparator.comparingInt(PriceData::price));
    }

    static PriceData min() {
        return Collections.min(data, Comparator.comparingInt(PriceData::price));
    }

    static List<PriceData> sortValues() {
        List<PriceData> sorted = new ArrayList<>(data);
        sorted.sort(Comparator.comparingInt(PriceData::price).reversed());
        return sorted;
    }

    static String bestAveragePriceSpan(List<PriceData> data, int span) {
        double minAverage = Double.MAX_VALUE;
        String startTime = data.getFirst().hour();

        double sum = 0;
        for (int i = 0; i < span; i++) {
            sum += data.get(i).price();
        }

        for (int i = 0; i < data.size() - span; i++) {
            if (sum / span < minAverage) {
                minAverage = sum / span;
                startTime = data.get(i).hour();
            }
            sum = sum - data.get(i).price() + data.get(i + span).price();
        }

        return String.format("""
                Påbörja laddning klockan %s
                Medelpris 4h: %.1f öre/kWh
                """, startTime.substring(0, 2), minAverage);
    }

    static float average(List<PriceData> data) {
        float sum = data.stream().mapToInt(PriceData::price).sum();
        return sum / data.size();
    }

    static String addSpaces(int amount) {
        return " ".repeat(amount);
    }
}