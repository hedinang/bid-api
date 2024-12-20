package com.example.bid_api.service.impl;

import com.example.bid_api.model.entity.Bid;
import com.example.bid_api.service.BidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidServiceImpl implements BidService {

    @Override
    public List<Bid> getList() {
        return List.of();
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
                webElements.forEach(webElement -> {
                    String url = webElement.findElement(By.tagName("img")).getAttribute("src");
                    System.out.println("web url : " + url);
                    List<WebElement> previews = webElement.findElement(By.className("preview-box")).findElements(By.className("box"));
                    System.out.println("start-time : " + previews.get(0).getText());
                    System.out.println("end-time : " + previews.get(1).getText());
                    System.out.println("market-info : " + webElement.findElement(By.className("market-info")).getText());
                    System.out.println("market-title : " + webElement.findElement(By.className("market-title")).getText());
                    System.out.println("date-time : " + webElement.findElement(By.className("datetime")).getText());
                });

                // You can also scrape other elements or perform actions like clicking buttons, etc.
            } finally {
                // Close the browser after usage
                driver.quit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
