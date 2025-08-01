import java.io.*;
import java.util.*;
import java.math.BigInteger;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

class Result {

    public static BigInteger findConstantC(String filePath) {
        BigInteger c = BigInteger.ZERO;
        try {
            // Parse JSON file
            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject) parser.parse(new FileReader(filePath));

            // Get keys object
            JSONObject keys = (JSONObject) jsonData.get("keys");
            long n = (long) keys.get("n");
            long k = (long) keys.get("k");

            // Store (x, y) pairs
            List<BigInteger> xVals = new ArrayList<>();
            List<BigInteger> yVals = new ArrayList<>();

            for (Object keyObj : jsonData.keySet()) {
                String key = keyObj.toString();
                if (key.equals("keys")) continue;

                // Parse root
                BigInteger x = new BigInteger(key);
                JSONObject rootData = (JSONObject) jsonData.get(key);

                int base = Integer.parseInt(rootData.get("base").toString());
                String value = rootData.get("value").toString();

                // Decode Y value from given base
                BigInteger y = new BigInteger(value, base);

                xVals.add(x);
                yVals.add(y);
            }

            int m = (int) (k - 1); // degree of polynomial
            if (xVals.size() < k) {
                throw new RuntimeException("Not enough roots to solve polynomial.");
            }

            // Use first k points for Lagrange interpolation
            List<BigInteger> xPoints = xVals.subList(0, (int) k);
            List<BigInteger> yPoints = yVals.subList(0, (int) k);

            // Calculate P(0) using Lagrange interpolation formula
            BigInteger numerator;
            BigInteger denominator;
            BigInteger result = BigInteger.ZERO;

            for (int i = 0; i < k; i++) {
                numerator = yPoints.get(i);
                denominator = BigInteger.ONE;

                for (int j = 0; j < k; j++) {
                    if (i != j) {
                        numerator = numerator.multiply(xPoints.get(j).negate());
                        denominator = denominator.multiply(xPoints.get(i).subtract(xPoints.get(j)));
                    }
                }
                // Add fraction numerator/denominator
                result = result.add(divideBigIntegerFraction(numerator, denominator));
            }

            c = result;

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return c;
    }

    // Helper to divide fractions and keep BigInteger exact result (works since denominator divides numerator)
    private static BigInteger divideBigIntegerFraction(BigInteger num, BigInteger den) {
        if (den.equals(BigInteger.ZERO)) {
            throw new ArithmeticException("Denominator is zero in fraction division.");
        }
        if (den.signum() < 0) {
            num = num.negate();
            den = den.negate();
        }
        if (!num.mod(den).equals(BigInteger.ZERO)) {
            // If not divisible, perform rational approximation (this shouldn't happen with valid input)
            return num.divide(den);
        }
        return num.divide(den);
    }
}

public class Solution {
    public static void main(String[] args) throws IOException {
        String filePath = "input2.json"; // Directly use input.json

        BigInteger result = Result.findConstantC(filePath);

        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out));
        bufferedWriter.write(result.toString());
        bufferedWriter.newLine();
        bufferedWriter.close();
    }
}
