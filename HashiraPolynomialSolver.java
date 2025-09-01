import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class HashiraPolynomialSolver {

    public static void main(String[] args) throws Exception {
        // Choose input file (you can change to "input2.json" for second test case)
        String filename = "input.json";  

        BufferedReader br = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line.trim());
        }
        br.close();

        String json = sb.toString();

        // --- Extract n and k ---
        int n = getIntBetween(json, "\"n\"", ",");
        int k = getIntBetween(json, "\"k\"", "}");

        // --- Extract roots ---
        List<BigInteger[]> points = new ArrayList<>();
        String[] entries = json.split("\\},");
        for (String entry : entries) {
            if (entry.contains("\"base\"") && entry.contains("\"value\"")) {
                // Extract key (the x-coordinate)
                int keyStart = entry.indexOf("\"");
                int keyEnd = entry.indexOf("\"", keyStart + 1);
                int x = Integer.parseInt(entry.substring(keyStart + 1, keyEnd));

                int base = getIntBetween(entry, "\"base\"", ",");
                String value = getStringBetween(entry, "\"value\"", "}");

                BigInteger y = new BigInteger(value, base);
                points.add(new BigInteger[]{BigInteger.valueOf(x), y});
            }
        }

        // Use first k points
        List<BigInteger[]> subset = points.subList(0, k);

        // Compute constant term
        BigInteger constantTerm = lagrangeAtZero(subset);

        System.out.println("Constant term: " + constantTerm);
    }

    // --- Helpers ---
    private static int getIntBetween(String text, String key, String endChar) {
        int idx = text.indexOf(key);
        if (idx == -1) return -1;
        int colon = text.indexOf(":", idx);
        int end = text.indexOf(endChar, colon);
        if (end == -1) end = text.length();
        String number = text.substring(colon + 1, end).replaceAll("[^0-9]", "").trim();
        if (number.isEmpty()) return -1;
        return Integer.parseInt(number);
    }

    private static String getStringBetween(String text, String key, String endChar) {
        int idx = text.indexOf(key);
        int firstQuote = text.indexOf("\"", idx + key.length());
        int secondQuote = text.indexOf("\"", firstQuote + 1);
        return text.substring(firstQuote + 1, secondQuote);
    }

    private static BigInteger lagrangeAtZero(List<BigInteger[]> pts) {
        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < pts.size(); i++) {
            BigInteger xi = pts.get(i)[0];
            BigInteger yi = pts.get(i)[1];

            BigInteger num = BigInteger.ONE;
            BigInteger den = BigInteger.ONE;

            for (int j = 0; j < pts.size(); j++) {
                if (i == j) continue;
                BigInteger xj = pts.get(j)[0];
                num = num.multiply(xj.negate());     // (0 - xj)
                den = den.multiply(xi.subtract(xj)); // (xi - xj)
            }
            BigInteger term = yi.multiply(num).divide(den);
            result = result.add(term);
        }
        return result;
    }
}

