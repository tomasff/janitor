package com.tomff.janitor.configuration;

import com.tomff.janitor.RoomBooking;

import java.util.Collection;
import java.util.Objects;

public record JanitorConfiguration(String username, String password, String currentVersion, String roomBookingUrl, WebhookNotificationConfiguration webhook, Collection<RoomBooking> bookings) {
    public JanitorConfiguration {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        Objects.requireNonNull(roomBookingUrl);
        Objects.requireNonNull(webhook);
        Objects.requireNonNull(bookings);
    }
}
