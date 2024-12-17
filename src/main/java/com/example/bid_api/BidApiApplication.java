package com.example.bid_api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BidApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BidApiApplication.class, args);

        //        try {
//            // Step 1: Fetch the CSRF token
//            String loginUrl = "https://www.ecoauc.com/client/users/sign-in";
//            Connection.Response initialResponse = Jsoup.connect(loginUrl)
//                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
//                    .method(Connection.Method.GET)
//                    .execute();
//
//            // Extract cookies
//            Map<String, String> cookies = initialResponse.cookies();
//
//            // Extract CSRF token
//            String csrfToken = cookies.get("CSRFToken"); // Check for CSRFToken in cookies
//            if (csrfToken == null || csrfToken.isEmpty()) {
//                Document document = initialResponse.parse();
//                csrfToken = document.select("meta[name=_csrfToken]").attr("content");
//                if (csrfToken.isEmpty()) {
//                    csrfToken = document.select("input[name=_csrfToken]").attr("value");
//                }
//            }
//
//            System.out.println("Extracted CSRF Token: " + csrfToken);
//
//            // Step 2: Login with CSRF token
//            Connection.Response loginResponse = Jsoup.connect(loginUrl)
//                    .cookies(cookies) // Include cookies from the initial response
//                    .data("email_address", "gavip13051995@gmail.com")
//                    .data("password", "Tungduong2024")
//                    .data("remember-me", "remember-me")
//                    .data("_csrfToken", csrfToken) // Include CSRF token
//                    .header("X-CSRF-Token", csrfToken) // Add CSRF token as a header if required
//                    .method(Connection.Method.POST)
//                    .execute();
//
//            // Update cookies after login
//            cookies.putAll(loginResponse.cookies());
//            System.out.println("loginResponse: " + loginResponse.body());
//
//            // Step 3: Navigate to the desired page after login
////            String dashboardUrl = "https://www.ecoauc.com/client";
////            Document dashboardPage = Jsoup.connect(dashboardUrl)
////                    .cookies(cookies) // Use the session cookies
////                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
////                    .get();
////
////            // Print the content of the dashboard page
////            System.out.println("Dashboard Page: ");
////            System.out.println(dashboardPage.body().text());
//
//            String dashboardUrl = "https://www.ecoauc.com/client";
//            Document dashboardPage = Jsoup.connect(dashboardUrl)
//                    .cookies(cookies) // Pass cookies from the login response
//                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
//                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
//                    .header("Accept-Encoding", "gzip, deflate, br, zstd")
//                    .header("Accept-Language", "en-GB,en;q=0.9,vi-VN;q=0.8,vi;q=0.7,fr-FR;q=0.6,fr;q=0.5,en-US;q=0.4")
//                    .header("Cache-Control", "no-cache")
//                    .header("Pragma", "no-cache")
//                    .header("Referer", "https://www.ecoauc.com/client/users/sign-in?redirect=%2Fclient&prfix=1")
//                    .header("Sec-CH-UA", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"")
//                    .header("Sec-CH-UA-Mobile", "?0")
//                    .header("Sec-CH-UA-Platform", "\"Windows\"")
//                    .header("Sec-Fetch-Dest", "document")
//                    .header("Sec-Fetch-Mode", "navigate")
//                    .header("Sec-Fetch-Site", "same-origin")
//                    .header("Sec-Fetch-User", "?1")
//                    .header("Upgrade-Insecure-Requests", "1")
//                    .method(Connection.Method.GET)
//                    .get();
//
//            // Print the dashboard page content
//            System.out.println("Dashboard Page: " + dashboardPage.body().text());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            // Define the URL
            String url = "https://www.ecoauc.com/client";

            // Define the cookies exactly as in your curl/Postman request
            Map<String, String> cookies = new HashMap<>();
            cookies.put("csrfToken", "bfb9dce0e28af1531cee77027f2d6c6ef072ad499ceccac44484b24909cfa94688723585a66072a423b18a7f8c3beb023400032fb7dad89ffc32d13f842ab14b");
            cookies.put("EcoAuc_browser_id", "13393-kk1wiJuUkseTRCNoKTpZ");
            cookies.put("jp_chatplus_vtoken", "rcr9cqdmqapld7k12d7b8e2e58b6");
            cookies.put("_gid", "GA1.2.145169696.1734362852");
            cookies.put("_ga", "GA1.1.951868254.1734362852");
            cookies.put("_ga_7G9P7X5VFB", "GS1.1.1734359190.1.1.1734363258.0.0.0");
            cookies.put("AWSALB", "6tqGGhsSSy2107IbzekErVXddSpSgBi71xBSIc2eZooQ9fH1gA6QeWdQFyrxajUfX/weGgamIx0XIqp0NsOXGq13TMFSaahEpC/MJW0M65MC0ViJPJrDq3tTzE/N");
            cookies.put("AWSALBCORS", "6tqGGhsSSy2107IbzekErVXddSpSgBi71xBSIc2eZooQ9fH1gA6QeWdQFyrxajUfX/weGgamIx0XIqp0NsOXGq13TMFSaahEpC/MJW0M65MC0ViJPJrDq3tTzE/N");
            cookies.put("CAKEPHP", "lkf9kj2qipu90p5p646url6kqv");

            // Send the GET request with headers and cookies
            Document response = Jsoup.connect(url)
                    .cookies(cookies)
                    .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                    .header("accept-language", "en-GB,en;q=0.9,vi-VN;q=0.8,vi;q=0.7,fr-FR;q=0.6,fr;q=0.5,en-US;q=0.4")
                    .header("cache-control", "no-cache")
                    .header("pragma", "no-cache")
                    .header("referer", "https://www.ecoauc.com/client/users/sign-in?redirect=%2Fclient&prfix=1")
                    .header("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"")
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", "\"Windows\"")
                    .header("sec-fetch-dest", "document")
                    .header("sec-fetch-mode", "navigate")
                    .header("sec-fetch-site", "same-origin")
                    .header("sec-fetch-user", "?1")
                    .header("upgrade-insecure-requests", "1")
                    .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                    .get();

            // Print the response
            System.out.println("Response: " + response.body().text());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
