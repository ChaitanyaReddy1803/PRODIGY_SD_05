import java.io.*;
import java.net.*;
import java.util.regex.*;

public class WebScraper {
    public static void main(String[] args) {
        String baseUrl = "http://books.toscrape.com/catalogue/page-";
        String csvFile = "books.csv";

        try (FileWriter writer = new FileWriter(csvFile)) {
            // Write CSV header
            writer.append("Title,Price,Rating\n");

            // Loop through multiple pages
            for (int page = 1; page <= 5; page++) {
                String url = baseUrl + page + ".html";
                String html = fetchHTML(url);

                // Regex patterns
                Pattern productPattern = Pattern.compile(
                    "<article class=\"product_pod\">(.*?)</article>", Pattern.DOTALL);
                Matcher productMatcher = productPattern.matcher(html);

                while (productMatcher.find()) {
                    String product = productMatcher.group(1);

                    // Extract title
                    String title = extractValue(product,
                            "title=\"(.*?)\"");

                    // Extract price
                    String price = extractValue(product,
                            "<p class=\"price_color\">(.*?)</p>");

                    // Extract rating
                    String rating = extractValue(product,
                            "<p class=\"star-rating (.*?)\">");

                    // Write to CSV
                    writer.append("\"").append(title).append("\",")
                          .append(price).append(",")
                          .append(rating).append("\n");
                }
            }

            System.out.println("Scraping completed. Data saved to " + csvFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Fetch HTML content from a URL
    private static String fetchHTML(String urlString) throws IOException {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        }
        return result.toString();
    }

    // Extract first match for given regex
    private static String extractValue(String text, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }
}
