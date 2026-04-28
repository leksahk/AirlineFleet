package com.airline.airlineweb;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AirlineWebAutomationTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;

    @BeforeAll
    void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testHomePageLoadsAndChecksTitle() {
        // Бот заходить на головну сторінку
        driver.get("http://localhost:" + port + "/");

        String title = driver.getTitle();
        assertTrue(title.contains("Airline Management") || title.contains("Додати літак") || title.contains("Авіа"),
                "Сторінка не завантажилася або неправильний заголовок!");
    }

    @Test
    void testNavigationToAddPlanePage() {
        driver.get("http://localhost:" + port + "/");

        driver.findElement(By.xpath("//a[@href='/add']")).click();

        assertTrue(driver.getCurrentUrl().contains("/add"), "Не вдалося перейти на форму додавання літака");
    }
}