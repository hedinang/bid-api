package com.example.bid_api.util;

import com.example.bid_api.model.dto.EcoSession;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HtmlUtil {
    @Value("${bid-email-address}")
    private String bidEmailAddress;

    @Value("${bid-email-password}")
    private String bidEmailPass;

    public String cloneHtml(String url) {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Cookie", "CAKEPHP=" + getToken())
                .header("User-Agent", "Mozilla/5.0")
                .header("Accept-Language", "en-US,en;q=0.9")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
            return response.body();
        } catch (Exception e) {
            return "";
        }
    }

    public EcoSession getEcoSession() throws Exception {

        String signInUrl =
                "https://www.ecoauc.com/client/users/sign-in";

        String loginPostUrl =
                "https://www.ecoauc.com/client/users/post-sign-in";

        Connection.Response loginPage =
                Jsoup.connect(signInUrl)
                        .method(Connection.Method.GET)
                        .execute();

        String csrfToken =
                loginPage.cookie("csrfToken");

        Connection.Response loginResponse =
                Jsoup.connect(loginPostUrl)
                        .cookies(loginPage.cookies())
                        .data("_csrfToken", csrfToken)
                        .data("email_address", bidEmailAddress)
                        .data("password", bidEmailPass)
                        .data("remember-me", "remember-me")
                        .method(Connection.Method.POST)
                        .execute();

        String cakePhp =
                loginResponse.cookie("CAKEPHP");

        Map<String, String> cookies = new HashMap<>();
        cookies.putAll(loginPage.cookies());
        cookies.putAll(loginResponse.cookies());

        return new EcoSession(
                cookies.get("CAKEPHP"),
                cookies.get("csrfToken"),
                cookies.get("AWSALB"),
                cookies.get("AWSALBCORS"),
                cookies.get("EcoAuc_browser_id")
        );
    }

//    public String postApi(String token, String url, Map<String, String> formData) {
//        try {
//            String boundary = "----FormBoundary" + System.currentTimeMillis();
//
//            StringBuilder bodyBuilder = new StringBuilder();
//
//            for (Map.Entry<String, String> entry : formData.entrySet()) {
//                bodyBuilder.append("--").append(boundary).append("\r\n");
//                bodyBuilder.append("Content-Disposition: form-data; name=\"")
//                        .append(entry.getKey())
//                        .append("\"\r\n\r\n");
//                bodyBuilder.append(entry.getValue()).append("\r\n");
//            }
//
//            bodyBuilder.append("--").append(boundary).append("--\r\n");
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(url))
//                    .header("Cookie", "CAKEPHP=" + token)
//                    .header("User-Agent", "Mozilla/5.0")
//                    .header("Accept-Language", "en-US,en;q=0.9")
//                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
//                    .POST(HttpRequest.BodyPublishers.ofString(bodyBuilder.toString()))
//                    .build();
//
//            HttpClient client = HttpClient.newHttpClient();
//
//            HttpResponse<String> response = client.send(
//                    request,
//                    HttpResponse.BodyHandlers.ofString()
//            );
//
//            return response.body();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
//    }

//    public String postApi(String token, String url, Map<String, String> formData, String csrfToken) {
//        try {
//            formData.put("_csrfToken", csrfToken);
//
//            String body = formData.entrySet()
//                    .stream()
//                    .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)
//                            + "=" +
//                            URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
//                    .collect(Collectors.joining("&"));
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(url))
//                    .header("Cookie", "CAKEPHP=" + token + "; csrfToken=" + csrfToken)
//                    .header("User-Agent", "Mozilla/5.0")
//                    .header("Accept-Language", "en-US,en;q=0.9")
//                    .header("Content-Type", "application/x-www-form-urlencoded")
//                    .header("X-CSRF-Token", csrfToken)
//                    .header("Origin", "https://www.ecoauc.com")
//                    .header("Referer", "https://www.ecoauc.com/client/mylist?is_bid=1")
//                    .POST(HttpRequest.BodyPublishers.ofString(body))
//                    .build();
//
//            HttpClient client = HttpClient.newHttpClient();
//
//            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
//    }

    public String postPreBid(
            String CAKEPHP,
            String csrftoken,
            String userId,
            String auctionItemId,
            String bidPrice,
            String referer
    ) {
        try {
            String body =
                    "user_id=" + URLEncoder.encode(userId, StandardCharsets.UTF_8) +
                            "&auction_item_id=" + URLEncoder.encode(auctionItemId, StandardCharsets.UTF_8) +
                            "&bid_price=" + URLEncoder.encode(bidPrice, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.ecoauc.com/client/pre-bids/add"))
                    .header("Cookie", "CAKEPHP=" + CAKEPHP + "; csrfToken=" + csrftoken)
                    .header("User-Agent", "Mozilla/5.0")
                    .header("Accept", "*/*")
                    .header("Accept-Language", "en-GB,en;q=0.9,vi-VN;q=0.8,vi;q=0.7,en-US;q=0.4")
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .header("Origin", "https://www.ecoauc.com")
//                    .header("Referer", referer)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getToken() {
        try {
            String loginUrl = "https://www.ecoauc.com/client/users/post-sign-in";
            Connection.Response loginResponse = Jsoup.connect(loginUrl)
                    .cookie("csrfToken", "bfb9dce0e28af1531cee77027f2d6c6ef072ad499ceccac44484b24909cfa94688723585a66072a423b18a7f8c3beb023400032fb7dad89ffc32d13f842ab14b")
                    .data("_csrfToken", "bfb9dce0e28af1531cee77027f2d6c6ef072ad499ceccac44484b24909cfa94688723585a66072a423b18a7f8c3beb023400032fb7dad89ffc32d13f842ab14b")
                    .data("email_address", bidEmailAddress)
                    .data("password", bidEmailPass)
                    .data("remember-me", "remember-me")
                    .method(Connection.Method.POST)
                    .execute();

            Map<String, String> cookies = loginResponse.cookies();
            return cookies.get("CAKEPHP");
        } catch (Exception ex) {
            log.error(ex.toString());
        }
        return "rrb8pmigiak49frf1j2n15rqqa";
    }

    public Map<String, String> getA() {
        try {
            String loginUrl = "https://www.ecoauc.com/client/users/post-sign-in";
            Connection.Response loginResponse = Jsoup.connect(loginUrl)
                    .cookie("csrfToken", "bfb9dce0e28af1531cee77027f2d6c6ef072ad499ceccac44484b24909cfa94688723585a66072a423b18a7f8c3beb023400032fb7dad89ffc32d13f842ab14b")
                    .data("_csrfToken", "bfb9dce0e28af1531cee77027f2d6c6ef072ad499ceccac44484b24909cfa94688723585a66072a423b18a7f8c3beb023400032fb7dad89ffc32d13f842ab14b")
                    .data("email_address", bidEmailAddress)
                    .data("password", bidEmailPass)
                    .data("remember-me", "remember-me")
                    .method(Connection.Method.POST)
                    .execute();

            return loginResponse.cookies();
        } catch (Exception ex) {
            log.error(ex.toString());
        }
        return null;
    }

    public Map<String, String> getB() {
        String loginUrl = "https://www.ecoauc.com/client/users/sign-in";
        try {

        Connection.Response loginPage = Jsoup.connect(loginUrl)
                .method(Connection.Method.GET)
                .execute();

        Map<String, String> cookies = new HashMap<>(loginPage.cookies());

        String csrfToken = cookies.get("csrfToken");

        Connection.Response loginResponse = Jsoup.connect("https://www.ecoauc.com/client/users/post-sign-in")
                .cookies(cookies)
                .data("_csrfToken", csrfToken)
                .data("email_address", bidEmailAddress)
                .data("password", bidEmailPass)
                .data("remember-me", "remember-me")
                .method(Connection.Method.POST)
                .execute();

        cookies.putAll(loginResponse.cookies());
        return cookies;

//        String cakephp = cookies.get("CAKEPHP");
//        csrfToken = cookies.get("csrfToken");
        } catch (Exception ex) {
            log.error(ex.toString());
        }
        return null;
    }




    public static String extractCsrfToken(String html) {

        Pattern pattern = Pattern.compile(
                "name=\"_csrfToken\"[^>]*value=\"([^\"]+)\"",
                Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(html);

        return matcher.find() ? matcher.group(1) : "";
    }


    public String preBid(
            EcoSession session,
            String userId,
            String auctionItemId,
            long bidPrice
    ) {

        try {

            String body =
                    "user_id=" + URLEncoder.encode(userId, StandardCharsets.UTF_8)
                            + "&auction_item_id=" + URLEncoder.encode(auctionItemId, StandardCharsets.UTF_8)
                            + "&bid_price=" + URLEncoder.encode(String.valueOf(bidPrice), StandardCharsets.UTF_8)
                            + "&_csrfToken=" + URLEncoder.encode(session.getCsrfToken(), StandardCharsets.UTF_8);

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create("https://www.ecoauc.com/client/timelimit-auctions/bid"))
                            .header(
                                    "Cookie",
                                    "CAKEPHP=" + session.getCakePhp()
                                            + "; csrfToken=" + session.getCsrfToken()
                            )
                            .header(
                                    "Content-Type",
                                    "application/x-www-form-urlencoded; charset=UTF-8"
                            )
                            .header(
                                    "Accept",
                                    "application/json, text/javascript, */*; q=0.01"
                            )
                            .header(
                                    "X-Requested-With",
                                    "XMLHttpRequest"
                            )
                            .header(
                                    "Origin",
                                    "https://www.ecoauc.com"
                            )
                            .header(
                                    "Referer",
                                    "https://www.ecoauc.com/client/mylist?is_bid=1&sortKey=1&limit=50&q=&master_item_ranks=&auction_lane_id=&tableType=list"
                            )
                            .POST(HttpRequest.BodyPublishers.ofString(body))
                            .build();

            HttpClient client =
                    HttpClient.newBuilder()
                            .followRedirects(HttpClient.Redirect.NORMAL)
                            .build();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String bidTimelimit(
            String userId,
            String auctionItemId,
            long bidPrice,
            EcoSession session
    ) {
        try {
            Map<String, String> cookies = new HashMap<>();

            cookies.put("AWSALB", session.getAwsalb());
            cookies.put("AWSALBCORS", session.getAwsalbcors());
            cookies.put("CAKEPHP", session.getCakePhp());
            cookies.put("csrfToken", session.getCsrfToken());

            if (session.getBrowserId() != null) {
                cookies.put("EcoAuc_browser_id", session.getBrowserId());
            }

            Connection.Response res = Jsoup.connect(
                            "https://www.ecoauc.com/client/timelimit-auctions/bid"
                    )
                    .cookies(cookies)
                    .ignoreContentType(true)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "*/*")
                    .header("Origin", "https://www.ecoauc.com")
                    .referrer("https://www.ecoauc.com/client/mylist?is_bid=1&sortKey=1&limit=50&q=&master_item_ranks=&auction_lane_id=&tableType=list")
                    .data("user_id", userId)
                    .data("auction_item_id", auctionItemId)
                    .data("bid_price", String.valueOf(bidPrice))
                    .data("_csrfToken", session.getCsrfToken())
                    .method(Connection.Method.POST)
                    .execute();

            return res.body();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }








    private final CookieManager cookieManager = new CookieManager();
    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(cookieManager)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    private static final String UA =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36";

    public String getSignInHtml() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.ecoauc.com/client/users/sign-in"))
                .header("User-Agent", UA)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    public void login() throws Exception {
        String html = getSignInHtml();
        String csrfToken = extractCsrfToken(html);

        String body =
                "_csrfToken=" + URLEncoder.encode(csrfToken, StandardCharsets.UTF_8) +
                        "&email_address=" + URLEncoder.encode(bidEmailAddress, StandardCharsets.UTF_8) +
                        "&password=" + URLEncoder.encode(bidEmailPass, StandardCharsets.UTF_8) +
                        "&remember-me=remember-me";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.ecoauc.com/client/users/post-sign-in"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("User-Agent", UA)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Origin", "https://www.ecoauc.com")
                .header("Referer", "https://www.ecoauc.com/client/users/sign-in")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("login status = " + response.statusCode());
        System.out.println("cookies = " + cookieManager.getCookieStore().getCookies());
    }

    public String bidTimelimit(String userId, String auctionItemId, long bidPrice) throws Exception {
        String csrfToken = getCookieValue("csrfToken");

        String body =
                "user_id=" + URLEncoder.encode(userId, StandardCharsets.UTF_8) +
                        "&auction_item_id=" + URLEncoder.encode(auctionItemId, StandardCharsets.UTF_8) +
                        "&bid_price=" + URLEncoder.encode(String.valueOf(bidPrice), StandardCharsets.UTF_8) +
                        "&_csrfToken=" + URLEncoder.encode(csrfToken, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.ecoauc.com/client/timelimit-auctions/bid"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("User-Agent", UA)
                .header("Accept", "*/*")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("X-Requested-With", "XMLHttpRequest")
                .header("Origin", "https://www.ecoauc.com")
                .header("Referer", "https://www.ecoauc.com/client/mylist?is_bid=1&sortKey=1&limit=50&q=&master_item_ranks=&auction_lane_id=&tableType=list")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("bid status = " + response.statusCode());
        System.out.println("bid body = " + response.body());

        return response.body();
    }

    private String getCookieValue(String name) {
        return cookieManager.getCookieStore()
                .getCookies()
                .stream()
                .filter(c -> c.getName().equals(name))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElse("");
    }

//    public static String extractCsrfToken(String html) {
//        Matcher matcher = Pattern.compile(
//                "name=\"_csrfToken\"[^>]*value=\"([^\"]+)\"",
//                Pattern.DOTALL
//        ).matcher(html);
//
//        return matcher.find() ? matcher.group(1) : "";
//    }
}
