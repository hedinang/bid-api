package com.example.bid_api.service.impl;

import com.example.bid_api.model.entity.Bid;
import com.example.bid_api.model.entity.Item;
import com.example.bid_api.repository.mongo.BidRepository;
import com.example.bid_api.repository.mongo.ItemRepository;
import com.example.bid_api.service.BidService;
import com.example.bid_api.util.StringUtil;
import com.example.bid_api.util.date.DateUtil;
import jakarta.transaction.Transactional;
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
    private WebDriver driver;

    @Override
    public List<Bid> getList() {
        return bidRepository.findAll();
    }

    @Override
    public Bid getBid(String bidId) {
        return bidRepository.findByBidId(bidId);
    }

    @Override
    @Transactional
    public void sync() {
        try {
            System.setProperty("webdriver.chrome.driver", "D:/lib/chromedriver-win64/chromedriver.exe");
            String clientUrl = "https://www.ecoauc.com/client";
            // Initialize WebDriver with Chrome options
            ChromeOptions options = new ChromeOptions();
            driver = new ChromeDriver(options);
            driver.manage().window().setSize(new Dimension(2400, 2000));
            driver.get(clientUrl);
            driver.manage().addCookie(new Cookie("CAKEPHP", "ru7ug964i030381l89eoev1u7e"));
            driver.get(clientUrl);
            List<WebElement> webElements = driver.findElements(By.className("slick-slide"));
            List<Bid> bids = new ArrayList<>();

            for (WebElement webElement : webElements) {
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
            for (Bid newBid : newBids) {
                int totalItem = getTotalItem(newBid.getDetailUrl());
                newBid.setTotalItem(totalItem);
            }
            bidRepository.saveAll(newBids);
            for (Bid newBid : newBids.subList(0, 3)) {
                int totalItem = newBid.getTotalItem();
                if (totalItem == 0) continue;
                int pages = (int) Math.ceil((double) totalItem / 50);
                for (int i = 0; i < 1; i++) {
                    syncItem(newBid.getDetailUrl(), i + 1, newBid.getBidId());
                }
            }


        } catch (Exception e) {
            log.error(e.toString());
        }

        driver.quit();
    }

    private void syncItem(String clientUrl, int page, String bidId) {
        try {
            driver.get(clientUrl + "&page=" + page);
            List<WebElement> webElements = driver.findElements(By.className("card"));
            if (webElements.size() < 10) return;
            List<Item> itemList = new ArrayList<>();
            for (WebElement we : webElements.subList(0, 10)) {
                Item item = new Item();
                item.setBidId(bidId);
                String itemDetailUrl = we.findElement(By.tagName("a")).getAttribute("href");
                List<WebElement> basicInfo = we.findElements(By.tagName("li"));
                item.setRank(basicInfo.get(0).getText().split("\n")[1]);
                item.setStartPrice(basicInfo.get(1).getText().split("\n")[1]);
                item.setAuctionOrder(basicInfo.get(2).getText().split("\n")[1]);

                item.setBranch(we.findElement(By.tagName("small")).getText());
                item.setItemUrl(itemDetailUrl);
                item.setTitle(we.findElement(By.tagName("b")).getText());
                item.setItemId(extractItemId(itemDetailUrl));
                itemList.add(item);
            }

            for (Item item : itemList) {
                extractItemDetail(item, item.getItemUrl());
            }

            itemRepository.saveAll(itemList);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }


    private void extractItemDetail(Item item, String itemDetailUrl) {
        try {
            driver.get(itemDetailUrl);
            List<WebElement> webElements = driver.findElements(By.className("pc-image-area"));
            List<String> a = webElements.stream().map(w ->
                    extractItemDetailUrl(w.getAttribute("style"))
            ).toList();
            item.setDetailUrls(a);

            WebElement itemInfo = driver.findElement(By.className("item-info"));
            extractItemId(item, itemInfo);
            extractDescription(item, itemInfo);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    private void extractItemId(Item item, WebElement we) {
        try {
            item.setItemId(we.findElement(By.tagName("small")).getText());
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    private void extractDescription(Item item, WebElement we) {
        try {
            item.setDescription(we.findElements(By.tagName("p")).get(1).getText());
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    private String extractItemId(String itemDetailUrl) {
        Pattern pattern = Pattern.compile("/view/(\\d+)/Auctions");
        Matcher matcher = pattern.matcher(itemDetailUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return itemDetailUrl;
    }

    private String extractItemDetailUrl(String itemDetailUrl) {
        try {
            String[] splitter = itemDetailUrl.split("\"");
            if (splitter.length > 1) {
                int questionMarkIndex = splitter[1].indexOf('?');
                // If '?' exists, extract the substring before it
                if (questionMarkIndex != -1) {
                    splitter[1] = splitter[1].substring(0, questionMarkIndex);
                }
                return splitter[1];
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    public int getTotalItem(String clientUrl) {
        try {
            driver.get(clientUrl);
            WebElement e = driver.findElement(By.className("form-control-static"));
            return extractTotalItem(e.getText());
        } catch (Exception e) {
            log.error(e.toString());
            return 0;
        }
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
