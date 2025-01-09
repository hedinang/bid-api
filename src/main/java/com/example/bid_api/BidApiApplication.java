package com.example.bid_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BidApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BidApiApplication.class, args);
//        try {
//            // Step 1: Set the login URL
//            String loginUrl = "https://www.ecoauc.com/client/users/post-sign-in";
//
//            // Step 2: Execute the POST request
//            Connection.Response loginResponse = Jsoup.connect(loginUrl)
//                    .cookie("csrfToken", "bfb9dce0e28af1531cee77027f2d6c6ef072ad499ceccac44484b24909cfa94688723585a66072a423b18a7f8c3beb023400032fb7dad89ffc32d13f842ab14b")
//                    .cookie("EcoAuc_browser_id", "13393-kk1wiJuUkseTRCNoKTpZ")
//                    .cookie("jp_chatplus_vtoken", "rcr9cqdmqapld7k12d7b8e2e58b6")
//                    .cookie("_gid", "GA1.2.145169696.1734362852")
//                    .cookie("_ga", "GA1.1.951868254.1734362852")
//                    .cookie("AWSALB", "MiJyOF3lNdH7Kf1GO3umaB9PgSkLSku9MrmYraFJwdBGVGX7+rztADuk6vokr/hzDLevSuQpc0eVwHeFvsNtSDYMzsO/czA6mzf4a/mu2fe8l18rJxoSA9FVXYa8")
//                    .cookie("AWSALBCORS", "MiJyOF3lNdH7Kf1GO3umaB9PgSkLSku9MrmYraFJwdBGVGX7+rztADuk6vokr/hzDLevSuQpc0eVwHeFvsNtSDYMzsO/czA6mzf4a/mu2fe8l18rJxoSA9FVXYa8")
//                    .data("_method", "POST")
//                    .data("_csrfToken", "bfb9dce0e28af1531cee77027f2d6c6ef072ad499ceccac44484b24909cfa94688723585a66072a423b18a7f8c3beb023400032fb7dad89ffc32d13f842ab14b")
//                    .data("email_address", "gavip13051995@gmail.com")
//                    .data("password", "Tungduong2024")
//                    .data("remember-me", "remember-me")
//                    .method(Connection.Method.POST)
//                    .execute();
//
//            // Step 3: Get the response cookies
//            Map<String, String> cookies = loginResponse.cookies();

        // Step 4: Check for the CAKEPHP session ID
//            if (!cookies.containsKey("CAKEPHP") || Objects.equals(cookies.get("CAKEPHP"), "deleted")) {
//                cookies.put("CAKEPHP", "j4e1dqcoi0v26mdmjem9kbal0n");
//            }

//            Map<String, String> cookies = new HashMap<>();
//            cookies.put("CAKEPHP", "j4e1dqcoi0v26mdmjem9kbal0n");
//
//            // Define the URL
//            String clientUrl = "https://www.ecoauc.com/client";
//            // Send the GET request with headers and cookies
//            Document clientResponse = Jsoup.connect(clientUrl)
//                    .cookies(cookies)
//                    .get();
//
//            Elements navbarHeader = clientResponse.select(".slick-track");
//            if (!navbarHeader.isEmpty()) {
//                System.out.println("Navbar Header HTML: " + navbarHeader.html());
//            } else {
//                System.out.println("No elements found with class 'navbar-header'.");
//            }
//
//            // Print the response
//            System.out.println("Response: " + clientResponse.body().text());


        // Setup Chrome WebDriver (ensure you have ChromeDriver installed and set path correctly)
//            System.setProperty("webdriver.chrome.driver", "D:/lib/chromedriver-win64/chromedriver.exe");
//
//            // Define the URL
//            String clientUrl = "https://www.ecoauc.com/client";
//
//            // Initialize WebDriver with Chrome options
//            ChromeOptions options = new ChromeOptions();
//            WebDriver driver = new ChromeDriver(options);
//
//            try {
//                // Set the window size (width: 1200px, height: 800px)
//                driver.manage().window().setSize(new Dimension(2400, 800));
//
//                // Open the URL in the browser (driver will handle cookies)
//                driver.get(clientUrl);
//
//                // Define the cookies to set (from CakePHP session)
//                Map<String, String> cookies = Map.of(
//                        "CAKEPHP", "j4e1dqcoi0v26mdmjem9kbal0n",
//                        "csrfToken", "your-csrf-token-here"
//                );
//
//                // Add the cookies to the browser session
//                for (Map.Entry<String, String> entry : cookies.entrySet()) {
//                    Cookie cookie = new Cookie(entry.getKey(), entry.getValue());
//                    driver.manage().addCookie(cookie);
//                }
//
//                // After adding cookies, refresh the page to apply the cookies
//                driver.navigate().refresh();
//
//                // Now that the page is loaded with cookies, you can interact with it
//                // For example, find an element and print its text
//                List<WebElement> webElements = driver.findElements(By.className("slick-slide"));
//                webElements.forEach(webElement -> {
//                    System.out.println("web element : " + webElement.getText());
////                    webElement.findElements(By.className("top-auction-card-detail")).get(0).findElements(By.tagName("img")).get(0).getAttribute("src")
//                    String url = webElement.findElement(By.tagName("img")).getAttribute("src");
//                    System.out.println("web url : " + url);
//                });
//
//                // You can also scrape other elements or perform actions like clicking buttons, etc.
//            } finally {
//                // Close the browser after usage
//                driver.quit();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
