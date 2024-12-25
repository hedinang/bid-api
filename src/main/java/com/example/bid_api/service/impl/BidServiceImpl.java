package com.example.bid_api.service.impl;

import com.example.bid_api.model.entity.Bid;
import com.example.bid_api.model.entity.Item;
import com.example.bid_api.repository.mongo.BidRepository;
import com.example.bid_api.repository.mongo.ItemRepository;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepository;
    private final ItemRepository itemRepository;

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
                        "CAKEPHP", "ru7ug964i030381l89eoev1u7e"
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

                    bid.setCloned(false);
                    bids.add(bid);
                }

                List<String> bidDetailUrls = bids.stream().map(Bid::getDetailUrl).toList();
                List<Bid> existedBids = bidRepository.findByDetailUrlIn(bids.stream().map(Bid::getDetailUrl).toList());
                List<String> existedDetailUrls = existedBids.stream().map(Bid::getDetailUrl).toList();
                List<Bid> newBids = bids.stream().filter(bid -> !existedDetailUrls.contains(bid.getDetailUrl())).toList();
                bidRepository.deleteByDetailUrlNotIn(bidDetailUrls);
                bidRepository.saveAll(newBids);
                //special

                for (Bid newBid : existedBids) {
                    int totalItem = getTotalItem(newBid.getDetailUrl());
                    if (totalItem == 0) continue;

                    int pages = (int) Math.ceil((double) totalItem / 50);
                    for (int i = 0; i < pages; i++) {
                        syncItem(newBid.getDetailUrl(), i + 1, newBid.getBidId());
                    }
                }


            } finally {
                // Close the browser after usage
                driver.quit();
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    private void syncItem(String clientUrl, int page, String bidId) {
        System.setProperty("webdriver.chrome.driver", "D:/lib/chromedriver-win64/chromedriver.exe");
        // Initialize WebDriver with Chrome options
        ChromeOptions options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(options);
        try {
            driver.manage().window().setSize(new Dimension(2400, 2000));
            driver.get(clientUrl + "&page=" + page);
            // Define the cookies to set (from CakePHP session)
            Map<String, String> cookies = Map.of(
                    "CAKEPHP", "ru7ug964i030381l89eoev1u7e"
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
            List<WebElement> webElements = driver.findElements(By.className("card"));
            List<Item> itemList = webElements.subList(0, 1).stream().map(we -> extractItemDetail(we, bidId)).toList();
            itemRepository.saveAll(itemList);
        } catch (Exception e) {
            log.error(e.toString());
        } finally {
            // Close the browser after usage
            driver.quit();
        }
    }

    private Item extractItemDetail(WebElement webElement, String bidId) {
        Item item = new Item();
        String itemDetailUrl = webElement.findElement(By.tagName("a")).getAttribute("href");
        List<WebElement> basicInfo = webElement.findElements(By.tagName("li"));
        item.setRank(basicInfo.get(0).getText().split("\n")[1]);
        item.setStartPrice(basicInfo.get(1).getText().split("\n")[1]);
        item.setAuctionOrder(basicInfo.get(2).getText().split("\n")[1]);
        item.setItemUrl(itemDetailUrl);
        item.setTitle(webElement.findElement(By.tagName("b")).getText());
        System.setProperty("webdriver.chrome.driver", "D:/lib/chromedriver-win64/chromedriver.exe");
        // Initialize WebDriver with Chrome options
        ChromeOptions options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(options);
        try {
            driver.manage().window().setSize(new Dimension(2400, 2000));
            driver.get(itemDetailUrl);
            // Define the cookies to set (from CakePHP session)
            Map<String, String> cookies = Map.of(
                    "CAKEPHP", "ru7ug964i030381l89eoev1u7e"
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
            List<WebElement> webElements = driver.findElements(By.className("pc-image-area"));
            List<String> itemDetailUrls = webElements.subList(0, 2).stream().map(we ->
                    extractItemDetailUrl(we.getAttribute("style"))
            ).toList();
            item.setDetailUrls(itemDetailUrls);
            item.setBidId(bidId);
            return item;
        } catch (Exception e) {
            log.error(e.toString());
        } finally {
            // Close the browser after usage
            driver.quit();
        }
        return null;
    }

    private String extractItemDetailUrl(String itemDetailUrl) {
        String[] splitter = itemDetailUrl.split("\"");
        if (splitter.length > 1) {
            int questionMarkIndex = splitter[1].indexOf('?');
            // If '?' exists, extract the substring before it
            if (questionMarkIndex != -1) {
                splitter[1] = splitter[1].substring(0, questionMarkIndex);
            }
            return splitter[1];
        }
        return "";
    }

    public int getTotalItem(String clientUrl) {
        System.setProperty("webdriver.chrome.driver", "D:/lib/chromedriver-win64/chromedriver.exe");
        // Initialize WebDriver with Chrome options
        ChromeOptions options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(options);
        try {
            driver.manage().window().setSize(new Dimension(2400, 800));
            driver.get(clientUrl);
            // Define the cookies to set (from CakePHP session)
            Map<String, String> cookies = Map.of(
                    "CAKEPHP", "ru7ug964i030381l89eoev1u7e"
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
            WebElement e = driver.findElement(By.className("form-control-static"));

            return extractTotalItem(e.getText());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the browser after usage
            driver.quit();
        }
        return 0;
    }

    private int extractTotalItem(String text) {
        Pattern pattern = Pattern.compile("Showing\\s([\\d,]+)");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String result = matcher.group(1).replace(",", ""); // Remove commas if necessary
            return Integer.parseInt(result);
        } else {
            return 0;
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
