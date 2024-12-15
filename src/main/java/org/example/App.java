package org.example;

import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class App {

    private static final int[] priser = new int[24]; // Array for storing electricity prices


    public static void main(String[] args) {
        Locale.setDefault(Locale.of("sv", "SE"));
        boolean running = true;
        Scanner scanner = new Scanner(System.in);

        while (running) {
            System.out.println("""
                Elpriser
                ========
                1. Inmatning
                2. Min, Max och Medel
                3. Sortera
                4. Bästa Laddningstid (4h)
                5. Visualisering
                e. Avsluta
                """);
            System.out.print("Välj alternativ: ");

            String input = scanner.nextLine().trim();

            switch (input.toLowerCase()) {
                case "e":
                    System.out.println("Programmet avslutas.");
                    running = false;
                    break;
                case "1":
                    inmatning(scanner);
                    break;
                case "2":
                    visaMinMaxMedel();
                    break;
                case "3":
                    sorteraPriser();
                    break;
                case "4":
                    bästaLaddningstid();
                    break;
                case "5":
                    visualisering();
                    break;
                default:
                    System.out.println("Ogiltigt val. Försök igen.");
                    break;
            }
        }

        scanner.close();
    }

    public static void inmatning(Scanner scanner) {
        System.out.println("Ange elpriser (öre/kWh) för varje timme, totalt 24 timmar:");
        for (int i = 0; i < priser.length; i++) {
            System.out.print("Timme " + i + ": ");
            while (!scanner.hasNextInt()) {
                System.out.println("Ogiltig inmatning. Endast heltal accepteras.");
                scanner.next();
            }
            priser[i] = scanner.nextInt();
        }
        scanner.nextLine(); // Clear newline
        System.out.println("Inmatning klar.");
    }

    public static void visaMinMaxMedel() {
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE, sum = 0;
        int minHour = 0, maxHour = 0;

        for (int i = 0; i < priser.length; i++) {
            if (priser[i] < min) {
                min = priser[i];
                minHour = i;
            }
            if (priser[i] > max) {
                max = priser[i];
                maxHour = i;
            }
            sum += priser[i];
        }

        double medel = sum / 24.0;

        System.out.printf("Lägsta pris: %02d-%02d, %d öre/kWh\n", minHour, (minHour + 1) % 24, min);
        System.out.printf("Högsta pris: %02d-%02d, %d öre/kWh\n", maxHour, (maxHour + 1) % 24, max);
        System.out.printf("Medelpris: %.2f öre/kWh\n", medel);
    }

    public static void sorteraPriser() {
        int[][] sorteradePriser = new int[24][2];
        for (int i = 0; i < priser.length; i++) {
            sorteradePriser[i][0] = i;         // Spara timmen
            sorteradePriser[i][1] = priser[i]; // Spara priset
        }

        // Sortera efter pris i fallande ordning, och timme i stigande ordning om priserna är lika
        Arrays.sort(sorteradePriser, (a, b) -> {
            if (b[1] != a[1]) {
                return Integer.compare(b[1], a[1]);
            }
            return Integer.compare(a[0], b[0]);
        });

        // Skriv ut de 4 högsta priserna i önskat format
        System.out.println("De 4 högsta priserna:");
        for (int i = 0; i < 4; i++) {
            int[] par = sorteradePriser[i];
            System.out.printf("%02d-%02d %d öre\n", par[0], (par[0] + 1) % 24 == 0 ? 24 : (par[0] + 1) % 24, par[1]);
        }
    }



    public static void bästaLaddningstid() {
        int bestStart = 0;
        int lowestSum = Integer.MAX_VALUE;

        for (int i = 0; i <= priser.length - 4; i++) {
            int sum = 0;
            for (int j = 0; j < 4; j++) {
                sum += priser[i + j];
            }
            if (sum < lowestSum) {
                lowestSum = sum;
                bestStart = i;
            }
        }

        // Adjust the output format to match the test
        System.out.printf("Påbörja laddning klockan %02d\n", bestStart);
        System.out.printf("Medelpris 4h: %.1f öre/kWh\n", lowestSum / 4.0);
    }


    public static void visualisering() {
        int max = Arrays.stream(priser).max().orElse(0);
        int min = Arrays.stream(priser).min().orElse(0);

        System.out.println("Visualisering av elpriser:");
        System.out.printf("%3d |", max);
        for (int pris : priser) {
            System.out.print(pris == max ? "*" : " ");
        }
        System.out.println();

        // Blank rows for intermediate levels
        for (int i = 0; i < 3; i++) {
            System.out.println("     |" + " ".repeat(priser.length));
        }

        System.out.printf("%3d |", min);
        for (int pris : priser) {
            System.out.print(pris == min ? "*" : " ");
        }
        System.out.println();

        System.out.println("     " + "-".repeat(priser.length));
        System.out.println("     00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23");

        // Add 'x' marks dynamically on the lowest row for prices
        System.out.print("     ");
        for (int pris : priser) {
            System.out.print(pris == min ? "x" : " ");
        }
        System.out.println();
    }
}