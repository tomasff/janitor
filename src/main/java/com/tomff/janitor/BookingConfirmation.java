package com.tomff.janitor;

import com.tomff.janitor.selenium.WebTable;
import org.openqa.selenium.WebElement;

import java.util.Objects;
import java.util.Optional;

public record BookingConfirmation(String room, String reference) {
    public BookingConfirmation {
        Objects.requireNonNull(room);
        Objects.requireNonNull(reference);
    }

    public static Optional<BookingConfirmation> fromTable(WebElement tableRoot) {
        return WebTable.getRowsUsingHeader(tableRoot)
                .stream()
                .map(row -> new BookingConfirmation(
                                row.get(0).getText(),
                                row.get(1).getText()
                        )
                )
                .findFirst();
    }
}
