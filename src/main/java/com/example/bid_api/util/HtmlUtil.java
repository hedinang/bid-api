package com.example.bid_api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String extractCsrfToken(String html) {

        Pattern pattern = Pattern.compile(
                "name=\"_csrfToken\"[^>]*value=\"([^\"]+)\"",
                Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(html);

        return matcher.find() ? matcher.group(1) : "";
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
    }

    public String bidTimelimit(
            String userId,
            String auctionItemId,
            long bidPrice,
            long maxPrice
    ) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        long currentBid = bidPrice;

//        while (true) {
        long addMore = currentBid >= 500000 ? 5000 : 1000;
        long realBidPrice = currentBid + addMore;

        if (realBidPrice >= maxPrice) {
            return null;
        }

        String csrfToken = getCookieValue("csrfToken");

        ObjectNode bodyJson = mapper.createObjectNode();
        bodyJson.put("user_id", userId);
        bodyJson.put("auction_item_id", Long.parseLong(auctionItemId));
        bodyJson.put("bid_price", realBidPrice);

        String body = mapper.writeValueAsString(bodyJson);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        "https://www.ecoauc.com/client/api/timelimit-auctions/bid"
                ))
                .version(HttpClient.Version.HTTP_1_1)
                .header("User-Agent", UA)
                .header("Accept", "*/*")
                .header("Content-Type", "application/json")
                .header("X-CSRF-Token", csrfToken)
                .header("Origin", "https://www.ecoauc.com")
                .header(
                        "Referer",
                        "https://www.ecoauc.com/client/mylist?is_bid=1&sortKey=1&limit=50&q=&master_item_ranks=&auction_lane_id=&tableType=list"
                )
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "Bid HTTP error: "
                            + response.statusCode()
                            + " - "
                            + response.body()
            );
        }

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
}
