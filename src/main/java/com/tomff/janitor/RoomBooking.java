package com.tomff.janitor;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public record RoomBooking(String reason, Set<String> rooms, String start, String end, boolean hasExternalSpeaker, int groupSize, int weekDay, Collection<Integer> weeks) {
    public RoomBooking {
        Objects.requireNonNull(reason);
        Objects.requireNonNull(rooms);
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        Objects.requireNonNull(weeks);
    }
}
