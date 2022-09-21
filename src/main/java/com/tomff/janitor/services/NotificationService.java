package com.tomff.janitor.services;

import com.tomff.janitor.BookingConfirmation;
import com.tomff.janitor.RoomBooking;

public interface NotificationService {
    void notifyEngaged();
    void notifyVersionChange(String newVersion);
    void notifyFailedBooking(RoomBooking booking);
    void notifyBookedSuccessfully(RoomBooking booking, BookingConfirmation confirmation);
}
