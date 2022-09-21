package com.tomff.janitor;

import com.tomff.janitor.services.RoomBookingService;
import com.tomff.janitor.services.WebhookNotificationService;

import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class CheckAndBook implements Runnable {
    private static final Logger logger = Logger.getLogger(CheckAndBook.class.getName());

    private final ScheduledExecutorService executor;

    private final RoomBookingService bookingService;
    private final WebhookNotificationService webhookNotificationService;

    private final String currentVersion;
    private final Collection<RoomBooking> bookings;

    public CheckAndBook(ScheduledExecutorService executor, RoomBookingService bookingService, WebhookNotificationService webhookNotificationService, String currentVersion, Collection<RoomBooking> bookings) {
        this.executor = executor;
        this.bookingService = bookingService;
        this.webhookNotificationService = webhookNotificationService;
        this.currentVersion = currentVersion;
        this.bookings = bookings;
    }

    private void stopJanitor() {
        executor.shutdown();

        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();

                if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                    System.err.println("Janitor did not terminate...");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void onVersionUpdate(String version) {
        if (version.equals(currentVersion)) {
            logger.info("Version " + currentVersion + " is not new, sleeping...");
            return;
        }

        logger.info("Version " + version + " is different.");
        logger.info("Notifying of version change...");

        webhookNotificationService.notifyVersionChange(version);

        for (RoomBooking booking : bookings) {
            bookingService.book(booking)
                    .ifPresentOrElse(
                            confirmation -> webhookNotificationService.notifyBookedSuccessfully(booking, confirmation),
                            () -> webhookNotificationService.notifyFailedBooking(booking)
                    );
        }

        stopJanitor();
    }

    @Override
    public void run() {
         bookingService.getRoomBookingVersion().ifPresent(this::onVersionUpdate);
    }
}
