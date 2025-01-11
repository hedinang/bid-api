package com.example.bid_api.service.impl;

import com.example.bid_api.model.entity.Bid;
import com.example.bid_api.model.entity.Item;
import com.example.bid_api.model.request.BidRequest;
import com.example.bid_api.repository.mongo.BidRepository;
import com.example.bid_api.repository.mongo.ItemRepository;
import com.example.bid_api.service.BidService;
import com.example.bid_api.util.StringUtil;
import com.example.bid_api.util.date.DateUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepository;
    private final ItemRepository itemRepository;
    private final Map<String, Thread> threadMap = new HashMap<>();
    private WebDriver bidDriver = null;
    private WebDriver itemDriver = null;

    @Value("${chrome-driver}")
    String chromeDriver;

    @Override
    public List<Bid> getList() {
        return bidRepository.findByClosed(false);
    }

    @Override
    public Bid getBid(BidRequest bidRequest) {
        return bidRepository.findByBidIdAndBidStatus(bidRequest.getBidId(), bidRequest.getBidStatus());
    }

//    @Override
//    @Transactional
//    public void storeBid() {
//        if (threadMap.containsKey("store-bid")) {
//            System.out.println("bid getting is already running.");
//            return;
//        }
//
//        Thread thread = new Thread(() -> {
//            try {
//                while (!Thread.currentThread().isInterrupted()) {
//                    try {
//                        System.setProperty("webdriver.chrome.driver", chromeDriver);
//                        String clientUrl = "https://www.ecoauc.com/client";
//                        // Initialize WebDriver with Chrome options
//                        ChromeOptions options = new ChromeOptions();
//                        driver = new ChromeDriver(options);
//                        driver.manage().window().setSize(new Dimension(2400, 2000));
//                        driver.get(clientUrl);
//                        driver.manage().addCookie(new Cookie("CAKEPHP", getToken());
//                        driver.get(clientUrl);
//                        List<WebElement> webElements = driver.findElements(By.className("slick-slide"));
//                        List<Bid> bids = new ArrayList<>();
//
//                        for (WebElement webElement : webElements) {
//                            if (extractDateTime(webElement) == null) continue;
//                            Bid bid = new Bid();
//                            bid.setBidStatus(extractBidStatus(webElement));
//                            bid.setHeaderIcon(extractIconUrl(webElement));
//                            bid.setTimeStatus(extractTimeStatus(webElement));
//                            String detailUrl = extractDetailUrl(webElement);
//                            String startPreviewTime = extractStartTime(webElement);
//                            String endPreviewTime = extractEndTime(webElement);
//                            String openDate = extractDateTime(webElement);
//
//                            URL url = new URL(detailUrl);
//                            String query = url.getQuery();
//                            Map<String, String> queryParams = StringUtil.getQueryParams(query);
//                            String bidId = queryParams.get("auctions");
//                            bid.setDetailUrl(detailUrl);
//                            bid.setBidId(bidId);
//
//                            if (startPreviewTime != null) {
//                                startPreviewTime = startPreviewTime.replace("〜", "").trim();
//                                bid.setStartPreviewTime(DateUtil.formatStringToDate(startPreviewTime, "MMM,dd,yyyy HH:mm"));
//                            }
//
//                            if (endPreviewTime != null) {
//                                endPreviewTime = endPreviewTime.replace("〜", "").trim();
//                                bid.setEndPreviewTime(DateUtil.formatStringToDate(endPreviewTime, "MMM,dd,yyyy HH:mm"));
//                            }
//
//                            if (openDate != null) {
//                                openDate = openDate.replace("〜", "").trim();
//                                bid.setOpenTime(DateUtil.formatStringToDate(openDate, "MMM,dd,yyyy HH:mm"));
//                            }
//
//                            bid.setDonePage(0);
//                            bid.setSynchronizing(false);
//                            bids.add(bid);
//                        }
//
//                        List<Bid> existedBids = bidRepository.findByDetailUrlIn(bids.stream().map(Bid::getDetailUrl).toList());
//                        List<String> existedDetailUrls = existedBids.stream().map(Bid::getDetailUrl).toList();
//                        List<String> bidIds = bids.stream().map(Bid::getBidId).toList();
//                        List<Bid> closedBids = bidRepository.findByClosedAndBidIdNotIn(false, bidIds);
//                        closedBids.forEach(closedBid -> closedBid.setClosed(true));
//                        bidRepository.saveAll(closedBids);
//
//                        List<Bid> newBids = bids.stream().filter(bid -> !existedDetailUrls.contains(bid.getDetailUrl())).toList();
//
//                        if (newBids.isEmpty()) {
//                            return;
//                        }
//
//                        for (Bid newBid : newBids) {
//                            int totalItem = getTotalItem(newBid.getDetailUrl());
//                            newBid.setTotalItem(totalItem);
//                        }
//                        bidRepository.saveAll(newBids);
//                    } catch (Exception e) {
//                        log.error(e.toString());
//                    }
//
//                    driver.quit();
//                    log.info("Get and store successfully bid");
//                    Thread.sleep(1000); // Simulate work
//                }
//            } catch (InterruptedException e) {
//                log.error("bid getting was interrupted.");
//            }
//        });
//
//        thread.setName("store-bid");
//        threadMap.put("store-bid", thread);
//        thread.start();
//    }

    @Override
    @Transactional
    public void storeBid() {
        try {
            System.setProperty("webdriver.chrome.driver", chromeDriver);
            String clientUrl = "https://www.ecoauc.com/client";
            // Initialize WebDriver with Chrome options
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");
            options.addArguments("--disable-gpu", "--remote-allow-origins=*");
            bidDriver = new ChromeDriver(options);
            bidDriver.manage().window().setSize(new Dimension(2400, 2000));
            bidDriver.get(clientUrl);
            bidDriver.manage().addCookie(new Cookie("CAKEPHP", getToken()));
            bidDriver.get(clientUrl);
            List<WebElement> webElements = bidDriver.findElements(By.className("slick-slide"));
            List<Bid> bids = new ArrayList<>();

            for (WebElement webElement : webElements) {
                if (extractDateTime(webElement) == null) continue;
                Bid bid = new Bid();
                bid.setBidStatus(extractBidStatus(webElement));
//                bid.setHeaderIcon(extractIconUrl(webElement));
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

                bid.setDonePage(0);
                bid.setSynchronizing(false);
                bids.add(bid);
            }

            List<Bid> existedBids = bidRepository.findByDetailUrlIn(bids.stream().map(Bid::getDetailUrl).toList());
            List<String> existedDetailUrls = existedBids.stream().map(Bid::getDetailUrl).toList();
            List<String> bidIds = bids.stream().map(Bid::getBidId).toList();
            List<Bid> closedBids = bidRepository.findByClosedAndBidIdNotIn(false, bidIds);
            closedBids.forEach(closedBid -> closedBid.setClosed(true));
            bidRepository.saveAll(closedBids);

            List<Bid> newBids = bids.stream().filter(bid -> !existedDetailUrls.contains(bid.getDetailUrl())).toList();

            if (newBids.isEmpty()) {
                return;
            }

            for (Bid newBid : newBids) {
                int totalItem = getTotalItem(newBid.getDetailUrl());
                newBid.setTotalItem(totalItem);
            }
            bidRepository.saveAll(newBids);
        } catch (Exception e) {
            log.error(e.toString());
        }

        bidDriver.quit();
        log.info("Get and store successfully bid");
    }

    @Override
    @Transactional
    public void stopThread(String threadName) {
        Thread thread = threadMap.get(threadName);
        if (thread != null) {
            thread.interrupt(); // Interrupt the thread
            threadMap.remove(threadName);
            log.info("{} has been stopped.", threadName);
        } else {
            log.error("No task found with name {}", threadName);
        }
    }

    public void syncBid(BidRequest bidRequest) {
        if (threadMap.containsKey("bid-" + bidRequest.getBidId() + "-" + bidRequest.getBidStatus())) {
            System.out.println("bid getting is already running.");
            return;
        }

        Thread thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Bid bid = bidRepository.findByBidIdAndBidStatus(bidRequest.getBidId(), bidRequest.getBidStatus());
                bid.setSynchronizing(true);
                bidRepository.save(bid);
                int totalItem = bid.getTotalItem();
                if (totalItem == 0) {
                    stopThread("bid-" + bidRequest.getBidId() + "-" + bidRequest.getBidStatus());
                    return;
                }
                int pages = (int) Math.ceil((double) totalItem / 50);
                if (bid.getDonePage() > pages) {
                    stopThread("bid-" + bidRequest.getBidId() + "-" + bidRequest.getBidStatus());
                    return;
                }

                System.setProperty("webdriver.chrome.driver", chromeDriver);
                // Initialize WebDriver with Chrome options
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");
                options.addArguments("--disable-gpu", "--remote-allow-origins=*");
                itemDriver = new ChromeDriver(options);
                itemDriver.manage().window().setSize(new Dimension(2400, 9000));
                itemDriver.get("https://www.ecoauc.com/client");
                itemDriver.manage().addCookie(new Cookie("CAKEPHP", getToken()));
                for (int i = bid.getDonePage() + 1; i < pages + 2; i++) {
                    syncItem(bid.getDetailUrl(), i + 1, bid);
                }
                itemDriver.quit();
                log.info("Sync complete bid: {}-{}", bidRequest.getBidId(), bidRequest.getBidStatus());

                Bid syncBid = bidRepository.findByBidIdAndBidStatus(bidRequest.getBidId(), bidRequest.getBidStatus());
                syncBid.setSynchronizing(false);
                bidRepository.save(bid);
                stopThread("bid-" + bidRequest.getBidId() + "-" + bidRequest.getBidStatus());
                return;
            }
        });

        thread.setName("bid-" + bidRequest.getBidId() + "-" + bidRequest.getBidStatus());
        threadMap.put("bid-" + bidRequest.getBidId() + "-" + bidRequest.getBidStatus(), thread);
        thread.start();
    }

    public Set<String> listThread() {
        return threadMap.keySet();
    }

    private void syncItem(String clientUrl, int page, Bid bid) {
        try {
            itemDriver.get(clientUrl + "&page=" + page);
            List<WebElement> webElements = itemDriver.findElements(By.className("card"));
            List<Item> itemList = new ArrayList<>();
            for (WebElement we : webElements) {
                Item item = new Item();
                item.setBidId(bid.getBidId());
                item.setBidStatus(bid.getBidStatus());

                String itemDetailUrl = we.findElement(By.tagName("a")).getAttribute("href");
                List<WebElement> basicInfo = we.findElements(By.tagName("li"));
                item.setRank(basicInfo.get(0).getText().split("\n")[1]);
                item.setStartPrice(basicInfo.get(1).getText().split("\n")[1]);
                item.setAuctionOrder(basicInfo.get(2).getText().split("\n")[1]);

                item.setBranch(we.findElement(By.tagName("small")).getText());
                item.setItemUrl(itemDetailUrl);
                item.setTitle(we.findElement(By.tagName("b")).getText());
//                item.setItemId(extractItemId(itemDetailUrl));
                itemList.add(item);
            }

            for (Item item : itemList) {
                extractItemDetail(item, item.getItemUrl());
            }

            List<String> existedItemIds = itemRepository.findByBidIdIn(itemList.stream().map(Item::getBidId).toList()).stream().map(Item::getBidId).toList();
            List<Item> newItems = itemList.stream().filter(i -> !existedItemIds.contains(i.getItemId())).toList();
            itemRepository.saveAll(newItems);
            bid.setDonePage(page);
            bidRepository.save(bid);
        } catch (Exception e) {
            log.error(e.toString());
        }

    }


    private void extractItemDetail(Item item, String itemDetailUrl) {
        try {
            itemDriver.get(itemDetailUrl);
            List<WebElement> webElements = itemDriver.findElements(By.className("pc-image-area"));
            List<String> a = webElements.stream().map(w -> extractItemDetailUrl(w.getAttribute("style"))).toList();
            item.setDetailUrls(a);

            WebElement itemInfo = itemDriver.findElement(By.className("item-info"));
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
            itemDriver.get(clientUrl);
            WebElement e = itemDriver.findElement(By.className("form-control-static"));
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

    private String getToken() {
        try {
            String loginUrl = "https://www.ecoauc.com/client/users/post-sign-in";
            Connection.Response loginResponse = Jsoup.connect(loginUrl)
                    .cookie("csrfToken", "bfb9dce0e28af1531cee77027f2d6c6ef072ad499ceccac44484b24909cfa94688723585a66072a423b18a7f8c3beb023400032fb7dad89ffc32d13f842ab14b")
                    .data("_csrfToken", "bfb9dce0e28af1531cee77027f2d6c6ef072ad499ceccac44484b24909cfa94688723585a66072a423b18a7f8c3beb023400032fb7dad89ffc32d13f842ab14b")
                    .data("email_address", "gavip13051995@gmail.com")
                    .data("password", "Tungduong2024")
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

    // delete expired bid
    @Scheduled(cron = "0 0 23 * * ?") // Executes every 5 seconds
    public void closeExpiredBid() {
        log.info("start to clean expired bid");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd");
        List<Bid> bids = bidRepository.findByClosed(false);
        List<Bid> expiredBids = bids.stream().filter(bid -> {
            LocalDateTime openTime = LocalDateTime.parse(bid.getOpenTime(), formatter);
            // Compare with the current time
            return openTime.isBefore(LocalDateTime.now());
        }).map(bid -> {
            bid.setClosed(true);
            stopThread(bid.getBidId() + "-" + bid.getBidStatus());
            return bid;
        }).toList();
        bidRepository.saveAll(expiredBids);
        // clear running thread belong to expired bids
        storeBid();
    }
}
