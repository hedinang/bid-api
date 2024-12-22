package com.example.bid_api.service.impl;

import com.example.bid_api.model.entity.Bid;
import com.example.bid_api.repository.mongo.BidRepository;
import com.example.bid_api.service.BidService;
import com.example.bid_api.util.StringUtil;
import com.example.bid_api.util.date.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepository;

    @Override
    public List<Bid> getList() {
        return bidRepository.findAll();
    }

    @Override
    public void sync() {
        try {
            System.setProperty("webdriver.chrome.driver", "D:/lib/chromedriver-win64/chromedriver.exe");
            String clientUrl = "https://www.ecoauc.com/client";
            // Initialize WebDriver with Chrome options
            ChromeOptions options = new ChromeOptions();
            WebDriver driver = new ChromeDriver(options);
            try {
                driver.manage().window().setSize(new Dimension(2400, 800));
                driver.get(clientUrl);
                // Define the cookies to set (from CakePHP session)
                Map<String, String> cookies = Map.of(
                        "CAKEPHP", "j4e1dqcoi0v26mdmjem9kbal0n",
                        "csrfToken", "your-csrf-token-here"
                );
                // Add the cookies to the browser session
                for (Map.Entry<String, String> entry : cookies.entrySet()) {
                    Cookie cookie = new Cookie(entry.getKey(), entry.getValue());
                    driver.manage().addCookie(cookie);
                }
                // After adding cookies, refresh the page to apply the cookies
                driver.navigate().refresh();
                // Now that the page is loaded with cookies, you can interact with it
                // For example, find an element and print its text
                List<WebElement> webElements = driver.findElements(By.className("slick-slide"));
                List<Bid> bids = new ArrayList<>();

                for (WebElement webElement : webElements) {
                    System.out.println("data " + webElement.getText());

                    if (extractDateTime(webElement) == null) continue;
                    Bid bid = new Bid();
                    bid.setBidStatus(extractBidStatus(webElement));
                    bid.setHeaderIcon(extractIconUrl(webElement));
                    bid.setTimeStatus(extractTimeStatus(webElement));
                    String detailUrl = extractDetailUrl(webElement);
                    String startPreviewTime = extractStartTime(webElement);
                    String endPreviewTime = extractEndTime(webElement);
                    String openDate = extractDateTime(webElement);

                    URL url = new URL(detailUrl);
                    String query = url.getQuery();
                    Map<String, String> queryParams = StringUtil.getQueryParams(query);
                    String bidId = queryParams.get("auctions");
                    bid.setDetailUrl(detailUrl);
                    bid.setBidId(bidId);

                    if (startPreviewTime != null) {
                        startPreviewTime = startPreviewTime.replace("〜", "").trim();
                        bid.setStartPreviewTime(DateUtil.formatStringToDate(startPreviewTime, "MMM,dd,yyyy HH:mm"));
                    }

                    if (endPreviewTime != null) {
                        endPreviewTime = endPreviewTime.replace("〜", "").trim();
                        bid.setEndPreviewTime(DateUtil.formatStringToDate(endPreviewTime, "MMM,dd,yyyy HH:mm"));
                    }

                    if (openDate != null) {
                        openDate = openDate.replace("〜", "").trim();
                        bid.setOpenTime(DateUtil.formatStringToDate(openDate, "MMM,dd,yyyy HH:mm"));
                    }

                    bids.add(bid);
                }
                bidRepository.saveAll(bids);
                // You can also scrape other elements or perform actions like clicking buttons, etc.
            } finally {
                // Close the browser after usage
                driver.quit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String extractDetailUrl(WebElement element) {
        try {
            return element.findElement(By.tagName("a")).getAttribute("href");
        } catch (Exception e) {
            return null;
        }
    }

    public String extractBidStatus(WebElement element) {
        try {
            return element.findElement(By.className("en")).getText();
        } catch (Exception e) {
            return null;
        }
    }

    public String extractIconUrl(WebElement element) {
        try {
            return element.findElement(By.tagName("img")).getAttribute("src");
        } catch (Exception e) {
            return null;
        }
    }

    public String extractStartTime(WebElement element) {
        try {
            return element.findElement(By.className("preview-box")).findElement(By.className("box")).findElements(By.tagName("span")).get(0).getText();
        } catch (Exception e) {
            return null;
        }
    }

    public String extractEndTime(WebElement element) {
        try {
            return element.findElement(By.className("preview-box")).findElement(By.className("box")).findElements(By.tagName("span")).get(1).getText();
        } catch (Exception e) {
            return null;
        }
    }

    public String extractTimeStatus(WebElement element) {
        try {
            return element.findElement(By.className("market-info")).getText();
        } catch (Exception e) {
            return null;
        }
    }

    public String extractTitle(WebElement element) {
        try {
            return element.findElement(By.className("market-title")).getText();
        } catch (Exception e) {
            return null;
        }
    }

    public String extractDateTime(WebElement element) {
        try {
            return element.findElement(By.className("datetime")).getText();
        } catch (Exception e) {
            return null;
        }
    }
}
