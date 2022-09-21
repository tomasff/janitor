package com.tomff.janitor;

import com.tomff.janitor.services.RoomBookingService;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JanitorShutdown extends Thread {
    private final ScheduledExecutorService executor;
    private final RoomBookingService roomBooking;

    public JanitorShutdown(ScheduledExecutorService executor, RoomBookingService roomBooking) {
        this.executor = executor;
        this.roomBooking = roomBooking;
    }

    @Override
    public void run() {
        System.err.println("*** shutting down the Janitor");

        executor.shutdown();
        roomBooking.shutdown();

        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();

                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            e.printStackTrace(System.err);

            Thread.currentThread().interrupt();
        }

        System.err.println("*** Janitor shut down");
    }
}
