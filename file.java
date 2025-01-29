import java.io.*;
import java.util.*;
import org.json.*;

public class ShamirSecretSharing {

    public static void main(String[] args) {
        try {
            // Read JSON file
            String jsonData = readJsonFile("input.json");
            JSONObject jsonObject = new JSONObject(jsonData);

            // Extract test cases
            JSONArray testCases = jsonObject.getJSONArray("test_cases");

            for (int t = 0; t < testCases.length(); t++) {
                JSONObject testCase = testCases.getJSONObject(t);

                // Extract k value
                int k = testCase.getJSONObject("keys").getInt("k");

                // Extract points from JSON
                List<int[]> points = new ArrayList<>();
                JSONObject shares = testCase.getJSONObject("shares");

                for (String key : shares.keySet()) {
                    JSONObject share = shares.getJSONObject(key);
                    int x = Integer.parseInt(key);
                    int base = share.getInt("base");
                    int y = Integer.parseInt(share.getString("value"), base);

                    points.add(new int[]{x, y});
                }

                // Select k points for interpolation
                points.sort(Comparator.comparingInt(a -> a[0]));
                points = points.subList(0, k);

                // Compute the secret (constant term 'c')
                int secret = lagrangeInterpolation(points);
                System.out.println("Test Case " + (t + 1) + " Secret (c): " + secret);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Function to read JSON file
    private static String readJsonFile(String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    // Function to compute Lagrange interpolation at x=0 to get constant term 'c'
    private static int lagrangeInterpolation(List<int[]> points) {
        int result = 0;
        int n = points.size();

        for (int i = 0; i < n; i++) {
            int xi = points.get(i)[0];
            int yi = points.get(i)[1];

            double term = yi;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    int xj = points.get(j)[0];
                    term *= (double) (-xj) / (xi - xj);
                }
            }
            result += term;
        }
        return result;
    }
}
