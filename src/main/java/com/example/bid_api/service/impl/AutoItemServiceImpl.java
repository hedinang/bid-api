package com.example.bid_api.service.impl;

import com.example.bid_api.model.dto.Page;
import com.example.bid_api.model.dto.ScanDto;
import com.example.bid_api.model.entity.AutoItem;
import com.example.bid_api.model.request.AutoItemRequest;
import com.example.bid_api.model.request.PageRequest;
import com.example.bid_api.model.request.ScanRequest;
import com.example.bid_api.model.search.AutoItemSearch;
import com.example.bid_api.repository.mongo.AutoItemRepository;
import com.example.bid_api.service.AutoItemService;
import com.example.bid_api.service.BidService;
import com.example.bid_api.service.ResourceService;
import com.example.bid_api.util.HtmlUtil;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoItemServiceImpl implements AutoItemService {
    private final ResourceService resourceService;
    private final AutoItemRepository autoItemRepository;
    private final BidService bidService;

    private final HtmlUtil htmlUtil;

    private long runningMinutes = 3;
    private Instant startTime;
    private Long maxRunningMinutes;
    private final TaskScheduler taskScheduler;
    private final AtomicReference<ScheduledFuture<?>> runningFutureRef = new AtomicReference<>();
    private final AtomicReference<ScheduledFuture<?>> stopFutureRef = new AtomicReference<>();

    public List<AutoItem> extractCsvFile(String resourceId) {
        File csvFile = resourceService.readFile(resourceId);
        List<AutoItem> autoItems = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(csvFile),
                            StandardCharsets.UTF_8
                    )
            );

            reader.readLine();

            try (
                    CSVParser parser = CSVFormat.DEFAULT
                            .builder()
                            .setHeader()
                            .setSkipHeaderRecord(false)
                            .setAllowMissingColumnNames(true)
                            .setTrim(true)
                            .build()
                            .parse(reader)
            ) {
                for (CSVRecord record : parser) {
                    String preBiddingPrice = getValue(record, "Pre-bidding price");

                    // chỉ lấy record có Pre-bidding price khác rỗng và khác 0
                    if (!hasPreBiddingPrice(preBiddingPrice)) {
                        continue;
                    }

                    AutoItem autoItem = new AutoItem();
                    autoItem.setItemId(getValue(record, "Item ID"));
                    autoItem.setItemNumber(getValue(record, "Item number"));
                    autoItem.setItemName(getValue(record, "Item name"));
                    autoItem.setAuctionOrder(getValue(record, "Auction order"));
                    autoItem.setRank(getValue(record, "Rank"));
                    autoItem.setStartingPrice(getValue(record, "Starting price"));
                    autoItem.setPreBiddingPrice(getValue(record, "Pre-bidding price"));
                    autoItem.setStartScheduled(getValue(record, "Start scheduled date"));
                    autoItem.setMaxPrice(Long.parseLong(Objects.requireNonNull(getValue(record, "Max"))));

                    autoItems.add(autoItem);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            log.error("Error reading CSV file: {}", e.getMessage());
            return autoItems;
        }

        List<String> itemIds = autoItems.stream()
                .map(AutoItem::getItemId)
                .toList();

        autoItemRepository.deleteByItemIds(itemIds);
        return autoItemRepository.insert(autoItems);
    }

    public Page<AutoItem> getList(PageRequest<AutoItemSearch> request) {
        Page<AutoItem> result = new Page<>();
        result.setItems(autoItemRepository.getList(request));
        result.setTotalItems(autoItemRepository.countList(request.getSearch()));
        return result;
    }

    @Transactional
    public void delete(String itemId) {
        autoItemRepository.deleteByItemId(itemId);
    }

    @Transactional
    public void edit(AutoItemRequest request){
        AutoItem autoItem = autoItemRepository.findByItemId(request.getItemId());
        if (autoItem != null) {
            autoItem.setMaxPrice(request.getMaxPrice());
            autoItemRepository.save(autoItem);
        }
    }

//    public synchronized void scanAutoItems() {
//        if (scanThread != null && scanThread.isAlive()) {
//            log.info("bid getting is already running.");
//            return;
//        }
//
//        scanThread = new Thread(() -> {
//            try {
//                int totalItem = bidService.getTotalItem("https://www.ecoauc.com/client/mylist?is_bid=1&is_other_bid=1&sortKey=1&limit=50&q=&master_item_ranks=&auction_lane_id=&tableType=list"
//                );
//
//                int pages = (int) Math.ceil((double) totalItem / 50);
//
//                List<BidItem> higherBidItems = new ArrayList<>();
//
//                for (int page = 0; page < pages; page++) {
//                    higherBidItems.addAll(extractHigherBid(page));
//                }
//
//                Map<String, BidItem> higherBidMap = higherBidItems.stream()
////                        .filter(item -> item.haveTheRight)
//                        .collect(Collectors.toMap(
//                                BidItem::getItemNumber,
//                                item -> item,
//                                (oldItem, newItem) -> newItem
//                        ));
//
//                List<AutoItem> autoItems =
//                        autoItemRepository.findByItemNumberIn(new ArrayList<>(higherBidMap.keySet()));
//
//                htmlUtil.login();
//
//                for (AutoItem autoItem : autoItems) {
//                    BidItem bidItem = higherBidMap.get(autoItem.getItemNumber());
//
//                    if (bidItem == null || autoItem.getMaxPrice() == 0 || bidItem.getPrice() > autoItem.getMaxPrice())
//                        continue;
//
////                    Map<String, String> formData = new HashMap<>();
////                    formData.put("user_id", "13393");
////                    formData.put("auction_item_id", autoItem.getItemNumber());
//
//                    long addMore = bidItem.getPrice() >= 500000 ? 5000 : 1000;
//
////                    formData.put("bid_price", String.valueOf(bidItem.getPrice() + addMore));
//
//                    String res = htmlUtil.bidTimelimit(
//                            "13393",
//                            autoItem.getItemId(),
//                            bidItem.getPrice() + addMore
//                    );
//
//                    log.info(res);
//                }
//
//            } catch (Exception e) {
//                log.error("scanAutoItems error", e);
//            } finally {
//                scanThread = null;
//                log.info("autoItem thread has been stopped.");
//            }
//        });
//
//        scanThread.start();
//    }

    public ScanDto executeTrigger(ScanRequest request) {
        stopTrigger();

        this.startTime = Instant.now();
        this.maxRunningMinutes = request.getMaxRunningMinutes();

        ScheduledFuture<?> runningFuture = taskScheduler.scheduleAtFixedRate(
                this::scanAutoItems,
                Duration.ofMinutes(runningMinutes)
        );

        runningFutureRef.set(runningFuture);

        ScheduledFuture<?> stopFuture = taskScheduler.schedule(
                this::stopTrigger,
                Instant.now().plus(Duration.ofMinutes(request.getMaxRunningMinutes()))
        );

        stopFutureRef.set(stopFuture);

        return new ScanDto(startTime.plus(Duration.ofMinutes(maxRunningMinutes)), maxRunningMinutes);
    }

    public void stopTrigger() {
        ScheduledFuture<?> runningFuture = runningFutureRef.getAndSet(null);
        if (runningFuture != null && !runningFuture.isCancelled()) {
            runningFuture.cancel(false);
        }

        ScheduledFuture<?> stopFuture = stopFutureRef.getAndSet(null);
        if (stopFuture != null && !stopFuture.isCancelled()) {
            stopFuture.cancel(false);
        }

        this.startTime = null;
        this.maxRunningMinutes = null;
    }

    public ScanDto checkScan() {
        if (startTime == null || maxRunningMinutes == null) {
            return null;
        } else {
            return new ScanDto(startTime.plus(Duration.ofMinutes(maxRunningMinutes)), maxRunningMinutes);
        }
    }

    public List<BidItem> extractHigherBid(int page) {
        String html = htmlUtil.cloneHtml("https://www.ecoauc.com/client/mylist?limit=50&is_other_bid=1&sortKey=1&tableType=list&q=&low=&high=&master_item_brands=&auction_lane_id=&master_item_categories=&master_item_shapes=&is_bid=1&master_item_ranks=" + "&page=" + page);

        List<String> blocks = extractItemBlocks(html);

        List<BidItem> bidItems = blocks.stream().map(block -> {
            BidItem item = new BidItem();

            item.setItemNumber(extractItemNumber(block));
            item.setPrice(extractPrice(block));
            item.setHighestBidPriceOnLastTime(extractHighestBidPriceOnLastTime(block));
            item.setPreBiddingPrice(extractPreBiddingPrice(block));
            item.setBidPrice(extractBidPrice(block));
//            item.setHaveTheRight(extractHaveTheRight(block));

            return item;
        }).toList();

        return bidItems;
    }

    public static List<String> extractItemBlocks(String html) {
        List<String> blocks = new ArrayList<>();

        String marker = "<div class=\"col-sm-6 col-md-4 col-lg-3 mb-grid-card\">";

        String[] parts = html.split(Pattern.quote(marker));

        for (int i = 1; i < parts.length; i++) {
            String block = marker + parts[i];

            if (block.contains("card-title-block")
                    && block.contains("Bid price")) {
                blocks.add(block);
            }
        }

        return blocks;
    }

    public static String extractItemNumber(String block) {
        Pattern pattern = Pattern.compile(
                "/images/item/\\d+/([A-Za-z0-9]+)-"
        );

        Matcher matcher = pattern.matcher(block);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    public static long extractPrice(String block) {
        return extractMoneyAfterLabel(block, "Price");
    }

    public static long extractHighestBidPriceOnLastTime(String block) {
        return extractMoneyAfterLabel(block, "Highest bid price on last time");
    }

    public static long extractPreBiddingPrice(String block) {
        return extractMoneyAfterLabel(block, "Pre-bidding price");
    }

    public static long extractBidPrice(String block) {
        return extractMoneyAfterLabel(block, "Bid price");
    }

    public static boolean extractHaveTheRight(String block) {
        if (block.contains("Not have the right")) {
            return false;
        }

        return block.contains("Have the right");
    }

    private static long extractMoneyAfterLabel(String block, String label) {
        Pattern pattern = Pattern.compile(
                Pattern.quote(label)
                        + ".*?(?:¥|&yen;)\\s*([\\d,]+)",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(block);

        if (matcher.find()) {
            return Long.parseLong(
                    matcher.group(1).replace(",", "")
            );
        }

        return 0L;
    }

    private boolean hasPreBiddingPrice(String value) {
        if (value == null) return false;

        String trimmed = value.trim();

        if (trimmed.isEmpty()) return false;
        if ("NaN".equalsIgnoreCase(trimmed)) return false;

        try {
            return new BigDecimal(trimmed).compareTo(BigDecimal.ZERO) != 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String getValue(CSVRecord record, String columnName) {
        if (!record.isMapped(columnName)) {
            return null;
        }

        String value = record.get(columnName);
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private void t() {
        log.info("test");
    }

    private void scanAutoItems() {
        try {
            int totalItem = bidService.getTotalItem("https://www.ecoauc.com/client/mylist?is_bid=1&is_other_bid=1&sortKey=1&limit=50&q=&master_item_ranks=&auction_lane_id=&tableType=list"
            );

            int pages = (int) Math.ceil((double) totalItem / 50);

            List<BidItem> higherBidItems = new ArrayList<>();

            for (int page = 0; page < pages; page++) {
                higherBidItems.addAll(extractHigherBid(page));
            }

            Map<String, BidItem> higherBidMap = higherBidItems.stream()
                    .collect(Collectors.toMap(
                            BidItem::getItemNumber,
                            item -> item,
                            (oldItem, newItem) -> newItem
                    ));

            List<AutoItem> autoItems =
                    autoItemRepository.findByItemNumberIn(new ArrayList<>(higherBidMap.keySet()));

            htmlUtil.login();

            for (AutoItem autoItem : autoItems) {
                BidItem bidItem = higherBidMap.get(autoItem.getItemNumber());

                if (bidItem == null || autoItem.getMaxPrice() == 0 || bidItem.getPrice() > autoItem.getMaxPrice())
                    continue;

                long addMore = bidItem.getPrice() >= 500000 ? 5000 : 1000;

                htmlUtil.bidTimelimit(
                        "13393",
                        autoItem.getItemId(),
                        bidItem.getPrice() + addMore
                );
            }
        } catch (Exception e) {
            log.error("scanAutoItems error", e);
        }
    }

    @Data
    public class BidItem {
        private String itemNumber;

        private long price;

        private long highestBidPriceOnLastTime;

        private long preBiddingPrice;

        private long bidPrice;

        private boolean haveTheRight;
    }
}
