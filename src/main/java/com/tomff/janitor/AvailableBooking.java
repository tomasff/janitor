package com.tomff.janitor;

import com.tomff.janitor.selenium.WebTable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.*;
import java.util.stream.Collectors;

public record AvailableBooking(WebElement selection, String time, String room) {
    public AvailableBooking {
        Objects.requireNonNull(selection);
        Objects.requireNonNull(time);
        Objects.requireNonNull(room);
    }

    public boolean compatibleWith(RoomBooking booking) {
        return time.equals(booking.start() + "-" + booking.end()) &&
                booking.rooms().contains(room);
    }

    public static Collection<AvailableBooking> fromTable(WebElement tableRoot) {
        return WebTable.getRows(tableRoot)
                .stream()
                .skip(1)
                .map(row -> new AvailableBooking(
                        row.get(0),
                        row.get(1).getText(),
                        row.get(3).getText()
                    )
                ).collect(Collectors.toList());
    }
}
