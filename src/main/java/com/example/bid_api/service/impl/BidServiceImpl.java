package com.example.bid_api.service.impl;

import com.example.bid_api.model.entity.Bid;
import com.example.bid_api.model.entity.Item;
import com.example.bid_api.model.request.BidRequest;
import com.example.bid_api.model.request.DeleteBidRequest;
import com.example.bid_api.repository.mongo.BidRepository;
import com.example.bid_api.repository.mongo.ItemRepository;
import com.example.bid_api.service.BidService;
import com.example.bid_api.util.HtmlUtil;
import com.example.bid_api.util.StringUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepository;
    private final ItemRepository itemRepository;

    private final HtmlUtil htmlUtil;

    private final Map<String, Thread> threadMap = new HashMap<>();

    @Value("${bid-email-address}")
    String bidEmailAddress;

    @Value("${bid-email-password}")
    String bidEmailPass;

    @Override
    public List<Bid> getList() {
        return bidRepository.findByClosed(false);
    }

    @Override
    public Bid getBid(BidRequest bidRequest) {
        return bidRepository.findByBidIdAndBidStatus(bidRequest.getBidId(), bidRequest.getBidStatus());
    }

    @Override
    @Transactional
    public void storeBid() {
        String html = htmlUtil.cloneHtml("https://www.ecoauc.com/client");

        if (html == null || html.isEmpty()) {
            return;
        }

        List<Bid> bids = extractAuctionItems(html);

        List<Bid> existedBids = bidRepository.findByDetailUrlIn(bids.stream().map(Bid::getDetailUrl).toList());
        Map<String, Bid> existedMap = existedBids.stream().collect(Collectors.toMap(Bid::getDetailUrl, bid -> bid, (a, b) -> a));

        List<String> existedDetailUrls = existedBids.stream().map(Bid::getDetailUrl).toList();
        List<String> bidIds = bids.stream().map(Bid::getBidId).toList();
        List<Bid> closedBids = bidRepository.findByClosedAndBidIdNotIn(false, bidIds);
        closedBids.forEach(closedBid -> closedBid.setClosed(true));

        List<Bid> needingStoreBids = new ArrayList<>(bids.stream().map(bid -> {
            // temporarily store &master_item_categories%5B0%5D=3&master_item_categories%5B1%5D=4
            int totalItem = getTotalItem(bid.getDetailUrl() + "&master_item_categories%5B0%5D=3&master_item_categories%5B1%5D=4");

            if (existedDetailUrls.contains(bid.getDetailUrl())) {
                Bid existedBid = existedMap.get(bid.getDetailUrl());
                int pages = (int) Math.ceil((double) totalItem / 50);
                int oldPages = (int) Math.ceil((double) existedBid.getTotalItem() / 50);
                existedBid.setDonePage(existedBid.getDonePage() + pages - oldPages);
                existedBid.setTotalItem(totalItem);
                return existedBid;
            }

            bid.setTotalItem(totalItem);

            int pages = (int) Math.ceil((double) totalItem / 50);
            bid.setDonePage(pages);
            return bid;
        }).toList());

        needingStoreBids.addAll(closedBids);
        bidRepository.saveAll(needingStoreBids);
    }

    public static List<Bid> extractAuctionItems(String html) {
        List<Bid> result = new ArrayList<>();

        Pattern cardPattern = Pattern.compile(
                "<div\\s*>\\s*" +
                        "<a\\s+href=\"([^\"]+)\"\\s+class=\"all-link\"></a>" +
                        "(.*?)" +
                        "<div class=\"top-auction-card-detail\">(.*?)</div>\\s*" +
                        "<div class=\"auction-text\"></div>\\s*</div>",
                Pattern.DOTALL
        );

        Matcher cardMatcher = cardPattern.matcher(html);

        while (cardMatcher.find()) {
            String link = cleanHtml(cardMatcher.group(1));
            String beforeDetail = cardMatcher.group(2);
            String card = cardMatcher.group(3);

            Bid bid = new Bid();

            bid.setDetailUrl(toAbsoluteUrl(link));

            bid.setBidId(extractAuctionId(bid.getDetailUrl()));

            bid.setTimeStatus(extract(
                    card,
                    "<span>\\s*(Realtime|Time-limited|リアルタイム|タイムリミット)\\s*</span>"
            ));

            bid.setBidStatus(extract(
                    beforeDetail,
                    "<span[^>]*>\\s*(Preview possible|In session|下見可能|開催中)\\s*</span>"
            ));

            bid.setStartPreviewTime(
                    StringUtil.convertEcoDate(
                            extract(
                                    card,
                                    "Preview：?\\s*</span>\\s*<div class=\"box\">\\s*<span>\\s*(.*?)\\s*〜"
                            )));

            bid.setEndPreviewTime(StringUtil.convertEcoDate(extract(
                    card,
                    "Preview：?\\s*</span>\\s*<div class=\"box\">\\s*<span>.*?〜\\s*</span>\\s*<span>\\s*(.*?)\\s*</span>"
            )));

            bid.setOpenTime(StringUtil.convertEcoDate(extract(
                    card,
                    "<span class=\"datetime\">\\s*(.*?)\\s*</span>"
            )));

            result.add(bid);
        }

        return result;
    }

    private static String extract(String text, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        if (!matcher.find()) return "";

        return cleanHtml(matcher.group(1));
    }

    private static String cleanHtml(String value) {
        if (value == null) return "";

        return value
                .replaceAll("<[^>]+>", "")
                .replace("&amp;", "&")
                .replace("&yen;", "¥")
                .replace("&nbsp;", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static String toAbsoluteUrl(String link) {
        if (link == null || link.isBlank()) return "";

        link = link.replace("&amp;", "&");

        if (link.startsWith("http")) {
            return link;
        }

        return "https://www.ecoauc.com" + link;
    }

    public static String extractAuctionId(String url) {

        Pattern pattern =
                Pattern.compile("[?&]auctions=(\\d+)");

        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "0";
    }

    public static int extractTotal(String text) {
        Pattern pattern = Pattern.compile("Showing\\s+([\\d,]+)\\s+of");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return Integer.parseInt(
                    matcher.group(1).replace(",", "")
            );
        }

        return 0;
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

                for (int page = bid.getDonePage(); page > 0; page--) {
                    List<String> itemUrls = syncItem(bid.getDetailUrl(), 1);
                    List<Item> itemList = itemUrls.stream().map(url -> {
                        Item item = new Item();
                        item.setBidId(bid.getBidId());
                        item.setBidStatus(bid.getBidStatus());
                        item.setItemUrl(url);

                        String html = htmlUtil.cloneHtml(url);

                        if (html != null && !html.isEmpty()) {
                            item.setDetailUrls(extractImageUrls(html));
                            item.setItemId(extractItemNo(html));
                            item.setTitle(extractTitle(html));
                            item.setDescription(extractNotes(html));
                            item.setRank(extractRank(html));
                            item.setBrand(extractBrand(html));
                            item.setStartPrice(extractStartingPrice(html));
                            item.setCategory(extractCategory(html));
                        }

                        return item;
                    }).toList();

                    List<String> existedItemIds = itemRepository.findByBidIdIn(itemList.stream().map(Item::getBidId).toList()).stream().map(Item::getBidId).toList();
                    List<Item> newItems = itemList.stream().filter(i -> !existedItemIds.contains(i.getItemId())).toList();
                    itemRepository.saveAll(newItems);
                    bid.setDonePage(page - 1);
                    bidRepository.save(bid);
                }

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

    private List<String> syncItem(String clientUrl, int page) {
        try {
            String html = htmlUtil.cloneHtml(clientUrl + "&master_item_categories%5B0%5D=3&master_item_categories%5B1%5D=4" + "&page=" + page + "&tableType=list");

            if (html == null || html.isEmpty()) {
                return new ArrayList<>();
            }

            List<String> itemUrls = extractItemDetailUrls(html).stream().distinct().toList();
            return itemUrls;
        } catch (Exception e) {
            log.error(e.toString());
            return new ArrayList<>();
        }
    }

    public static List<String> extractItemDetailUrls(String html) {
        List<String> urls = new ArrayList<>();

        Pattern pattern = Pattern.compile(
                "<a\\s+href=\"([^\"]*/client/auction-items/view/\\d+/[^\"]+)\"",
                Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            String url = matcher.group(1)
                    .replace("&amp;", "&")
                    .trim();

            if (!url.startsWith("http")) {
                url = "https://www.ecoauc.com" + url;
            }

            urls.add(url);
        }

        return urls;
    }

    public int getTotalItem(String clientUrl) {
        String html = htmlUtil.cloneHtml(clientUrl);

        if (html == null || html.isEmpty()) {
            return 0;
        }

        return extractTotal(html);
    }

    @Override
    public void deleteBid(DeleteBidRequest deleteBidRequest) {
        bidRepository.deleteByUniqueId(deleteBidRequest.getUniqueId());
    }

    /* detail */
    private List<String> extractImageUrls(String html) {
        Set<String> urls = new LinkedHashSet<>();

        Pattern pattern = Pattern.compile(
                "https://resize\\.ecoauc\\.com/images/item/[^\"')?]+\\.jpg"
        );

        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            urls.add(matcher.group());
        }

        return new ArrayList<>(urls);
    }

    private String extractItemNo(String html) {

        Pattern pattern = Pattern.compile(
                "<small>\\s*No\\.(\\d+)\\s*</small>",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    private String extractNotes(String html) {

        Pattern pattern = Pattern.compile(
                "<p>\\s*\\[Notes\\]\\s*(.*?)\\s*</p>",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            return matcher.group(1)
                    .replaceAll("<[^>]+>", "")
                    .trim();
        }

        return "";
    }

    public static String extractTitle(String html) {

        Pattern pattern = Pattern.compile(
                "<small\\s+class=\"show\">.*?</small>\\s*([^<]+)</h2>",
                Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return "";
    }

    public static String extractRank(String html) {

        Pattern pattern = Pattern.compile(
                "<span\\s+class=\"circle-text\">\\s*([^<]+?)\\s*<small>\\s*Rank\\s*</small>",
                Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return "";
    }

    public static String extractBrand(String html) {

        Pattern pattern = Pattern.compile(
                "<small\\s+class=\"show\">\\s*(.*?)\\s*</small>",
                Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return "";
    }

    public static String extractStartingPrice(String html) {

        Pattern pattern = Pattern.compile(
                "<dt>\\s*Starting price\\s*</dt>\\s*<dd>\\s*<big[^>]*>\\s*&yen;\\s*([\\d,]+)",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            return matcher.group(1).replace(",", "");
        }

        return "0";
    }

    public static String extractCategory(String html) {

        Pattern pattern = Pattern.compile(
                "<dt>\\s*Category\\s*</dt>\\s*<dd>(.*?)</dd>",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            return matcher.group(1)
                    .replaceAll("<[^>]+>", "")
                    .trim();
        }

        return "";
    }
}
