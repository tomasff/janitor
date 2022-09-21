package com.tomff.janitor.services;

import com.tomff.janitor.AvailableBooking;
import com.tomff.janitor.BookingConfirmation;
import com.tomff.janitor.RoomBooking;
import com.tomff.janitor.configuration.JanitorConfiguration;
import com.tomff.janitor.configuration.ScientiaConfiguration;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

public class RoomBookingService {
    private static final Logger logger = Logger.getLogger(RoomBookingService.class.getName());

    private static final String YES = "Yes";
    private static final String NO = "No";

    private final String roomBookingUrl;
    private final ScientiaConfiguration scientiaConfig;
    private final JanitorConfiguration janitorConfig;

    private final WebDriver driver;
    private final OkHttpClient httpClient;

    public RoomBookingService(String roomBookingUrl, ScientiaConfiguration scientiaConfig, JanitorConfiguration janitorConfig) {
        this.roomBookingUrl = roomBookingUrl;
        this.scientiaConfig = scientiaConfig;
        this.janitorConfig = janitorConfig;

        this.httpClient = new OkHttpClient.Builder()
                .followRedirects(false)
                .cache(null)
                .build();

        this.driver = new ChromeDriver(
                new ChromeOptions()
                    .addArguments("--headless")
        );

        this.driver.manage()
                .timeouts()
                .implicitlyWait(Duration.ofSeconds(30));
    }

    public Optional<String> getRoomBookingVersion() {
        Request request = new Request.Builder().url(roomBookingUrl).build();

        try (Response response = httpClient.newCall(request).execute()) {
            return Optional.ofNullable(response.header("Location", ""));
        } catch (IOException e) {
            logger.warning("Failed to get room booking version: " + e.getMessage());

            return Optional.empty();
        }
    }

    private void authenticate() {
        logger.info("Authenticating...");
        driver.findElement(scientiaConfig.usernameField()).sendKeys(janitorConfig.username());
        driver.findElement(scientiaConfig.passwordField()).click();
        driver.findElement(scientiaConfig.passwordField()).sendKeys(janitorConfig.password());

        driver.findElement(scientiaConfig.logonButton()).click();
    }

    private boolean isInLoginPage() {
        return driver.getCurrentUrl().startsWith(scientiaConfig.loginPage());
    }

    private void selectGroupSizeDateAndTime(RoomBooking booking) {
        logger.info("Selecting group size, date, time and weeks for " + booking);

        // Select group size
        Select groupSizeSelect = new Select(driver.findElement(scientiaConfig.groupSizeField()));
        groupSizeSelect.selectByValue(String.valueOf(booking.groupSize()));

        // Book for multiple days
        driver.findElement(scientiaConfig.multipleDaysButton()).click();

        // Select day of the week
        driver.findElement(scientiaConfig.weekDayMapping().apply(booking.weekDay())).click();

        // Select weeks
        Select weeksStarting = new Select(driver.findElement(scientiaConfig.weeksStartingSelection()));

        for (int week : booking.weeks()) {
            weeksStarting.selectByIndex(week);
        }

        // Select start time
        Select startTime = new Select(driver.findElement(scientiaConfig.startTimeField()));
        startTime.selectByVisibleText(booking.start());

        // Select end time
        Select endTime = new Select(driver.findElement(scientiaConfig.endTimeField()));
        endTime.selectByVisibleText(booking.end());
    }

    private boolean selectCompatibleRoomBooking(RoomBooking booking) {
        logger.info("Attempting to select compatible room bookings " + booking);

        if (driver.findElements(scientiaConfig.availableBookingsTable()).size() == 0) {
            logger.warning("Table for available bookings not found for booking " + booking);
            return false;
        }

        // Show ALL options
        driver.findElement(scientiaConfig.showMoreOptionsButton()).click();

        Collection<AvailableBooking> availableBookings = AvailableBooking.fromTable(driver.findElement(scientiaConfig.availableBookingsTable()));

        Optional<AvailableBooking> compatibleBooking = availableBookings.stream()
                .filter(availableBooking -> availableBooking.compatibleWith(booking))
                .findAny();

        if (compatibleBooking.isEmpty()) {
            return false;
        }

        logger.info("Available booking found for for booking " + booking + ": " + compatibleBooking);

        // Select available compatible booking
        compatibleBooking.ifPresent(availableBooking -> availableBooking.selection().click());

        return true;
    }

    private void confirmBookingDetails(RoomBooking booking) {
        logger.info("Confirming booking details for booking " + booking);
        Select isBookingForSociety = new Select(driver.findElement(scientiaConfig.isSocietyBookingSelection()));
        isBookingForSociety.selectByValue(YES);

        driver.findElement(scientiaConfig.reasonForBookingField()).sendKeys(booking.reason());

        Select foodAndDrinkTakenToRoom = new Select(driver.findElement(scientiaConfig.foodAndDrinkField()));
        foodAndDrinkTakenToRoom.selectByValue(YES);

        Select furnitureMoving = new Select(driver.findElement(scientiaConfig.furnitureMovedField()));
        furnitureMoving.selectByValue(YES);

        Select disturbance = new Select(driver.findElement(scientiaConfig.disturbanceField()));
        disturbance.selectByValue(NO);

        Select attendanceFee = new Select(driver.findElement(scientiaConfig.chargingAttendanceFeeField()));
        attendanceFee.selectByValue(NO);

        Select externalSpeaker = new Select(driver.findElement(scientiaConfig.isExternalSpeakerField()));
        externalSpeaker.selectByValue(booking.hasExternalSpeaker() ? YES : NO);

        Select acceptConditions = new Select(driver.findElement(scientiaConfig.acceptConditionsField()));
        acceptConditions.selectByValue(YES);
    }

    public Optional<BookingConfirmation> getBookingConfirmation(RoomBooking booking) {
        logger.info("Getting booking confirmation for " + booking);

        if (driver.findElements(scientiaConfig.bookingConfirmationTable()).size() == 0) {
            logger.warning("Failed to find confirmation for booking " + booking);
            return Optional.empty();
        }

        return BookingConfirmation.fromTable(
                driver.findElement(scientiaConfig.bookingConfirmationTable()));
    }

    public Optional<BookingConfirmation> book(RoomBooking booking) {
        logger.info("Attempting to book " + booking);

        driver.get(roomBookingUrl);

        if (isInLoginPage()) {
            authenticate();
        }

        selectGroupSizeDateAndTime(booking);

        // Show rooms available
        driver.findElement(scientiaConfig.showRoomsButton()).click();

        boolean selectedCompatibleBooking = selectCompatibleRoomBooking(booking);

        if (!selectedCompatibleBooking) {
            return Optional.empty();
        }

        // Confirm selection
        driver.findElement(scientiaConfig.confirmOptionButton()).click();

        confirmBookingDetails(booking);

        // Final confirmation
        driver.findElement(scientiaConfig.confirmBookingButton()).click();

        return getBookingConfirmation(booking);
    }

    public void shutdown() {
        driver.quit();
    }
}
