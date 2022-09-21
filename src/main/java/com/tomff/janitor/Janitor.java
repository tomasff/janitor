package com.tomff.janitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import com.tomff.janitor.configuration.JanitorConfiguration;
import com.tomff.janitor.configuration.ScientiaConfiguration;
import com.tomff.janitor.services.RoomBookingService;
import com.tomff.janitor.services.WebhookNotificationService;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Janitor {
    private static final ObjectMapper objectMapper = new TomlMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    private final JanitorConfiguration janitorConfiguration;
    private final ScheduledExecutorService executor;
    private final RoomBookingService roomBooking;
    private final WebhookNotificationService webhookNotification;

    public Janitor(JanitorConfiguration janitorConfiguration) {
        this.janitorConfiguration = janitorConfiguration;

        executor = Executors.newScheduledThreadPool(2);

        roomBooking = new RoomBookingService(
                janitorConfiguration.roomBookingUrl(),
                ScientiaConfiguration.WARWICK_TIMETABLING,
                janitorConfiguration
        );

        webhookNotification = new WebhookNotificationService(
                janitorConfiguration.webhook(), executor);
    }

    public void start() {
        webhookNotification.notifyEngaged();

        executor.scheduleAtFixedRate(
                new CheckAndBook(
                        roomBooking,
                        webhookNotification,
                        janitorConfiguration.currentVersion(),
                        janitorConfiguration.bookings()
                ), 0L, 5L, TimeUnit.MINUTES);

        Runtime.getRuntime().addShutdownHook(new JanitorShutdown(executor, roomBooking));
    }

    private static String getConfigPath(String[] args) throws IOException {
        if (args.length == 0) {
            throw new IOException("Invalid config path.");
        }

        return args[0];
    }

    private static JanitorConfiguration loadConfig(String path) throws IOException {
        return objectMapper.readValue(new File(path), JanitorConfiguration.class);
    }

    public static void main(String[] args) throws IOException {
        String configPath = getConfigPath(args);
        JanitorConfiguration config = loadConfig(configPath);

        Janitor automation = new Janitor(config);
        automation.start();
    }
}
