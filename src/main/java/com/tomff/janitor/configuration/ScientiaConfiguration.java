package com.tomff.janitor.configuration;

import org.openqa.selenium.By;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public record ScientiaConfiguration(
        String loginPage, List<String> bookingPages, By usernameField, By passwordField,
        By logonButton, By groupSizeField, By multipleDaysButton,
        Function<Integer, By> weekDayMapping, By weeksStartingSelection, By startTimeField,
        By endTimeField, By showRoomsButton, By availableBookingsTable, By showMoreOptionsButton,
        By confirmOptionButton, By isSocietyBookingSelection, By reasonForBookingField,
        By foodAndDrinkField, By furnitureMovedField, By disturbanceField,
        By chargingAttendanceFeeField, By isExternalSpeakerField, By acceptConditionsField,
        By confirmBookingButton, By bookingConfirmationTable) {
    public static final ScientiaConfiguration WARWICK_TIMETABLING = new ScientiaConfiguration(
            "https://timetablingmanagement.warwick.ac.uk/Scientia/Portal/Login.aspx",
            List.of("default.aspx"),
            By.id("ContentPlaceHolder1_user"),
            By.id("ContentPlaceHolder1_password"),
            By.id("ContentPlaceHolder1_logon"),
            By.id("ctl00_Main_Room1_ReqSize"),
            By.id("ctl00_Main_Date1_MultipleDateBtn"),
            day -> By.id("ctl00_Main_Date1_DaysnWeeks1_Days_" + day),
            By.id("ctl00_Main_Date1_DaysnWeeks1_Weeks"),
            By.id("startTimeTemp"),
            By.id("endTimeTemp"),
            By.id("ctl00_Main_ShowOptionsBtn"),
            By.id("ctl00_Main_OptionSelector_OptionsGrid"),
            By.id("ctl00_Main_OptionSelector_ExtendSearchLink"),
            By.id("ctl00_Main_SelectOptionButton"),
            By.id("ctl00_Main_BookingForm1_SocietyClub"),
            By.id("ctl00_Main_BookingForm1_meaningfulName"),
            By.id("ctl00_Main_BookingForm1_FoodDrink"),
            By.id("ctl00_Main_BookingForm1_Layout"),
            By.id("ctl00_Main_BookingForm1_Disturbance"),
            By.id("ctl00_Main_BookingForm1_AttendanceFee"),
            By.id("ctl00_Main_BookingForm1_External-Speaker"),
            By.id("ctl00_Main_BookingForm1_acceptConditions"),
            By.id("ctl00_Main_MakeBookingBtn"),
            By.xpath(
                    "//*[@id=\"ctl00_Main_BookingForm1_BookingCompleted\"]/div/table[1]")
    );

    public ScientiaConfiguration {
        Objects.requireNonNull(loginPage);
        Objects.requireNonNull(bookingPages);
        Objects.requireNonNull(usernameField);
        Objects.requireNonNull(passwordField);
        Objects.requireNonNull(logonButton);
        Objects.requireNonNull(groupSizeField);
        Objects.requireNonNull(multipleDaysButton);
        Objects.requireNonNull(weekDayMapping);
        Objects.requireNonNull(weeksStartingSelection);
        Objects.requireNonNull(startTimeField);
        Objects.requireNonNull(endTimeField);
        Objects.requireNonNull(showRoomsButton);
        Objects.requireNonNull(availableBookingsTable);
        Objects.requireNonNull(showMoreOptionsButton);
        Objects.requireNonNull(confirmOptionButton);
        Objects.requireNonNull(isSocietyBookingSelection);
        Objects.requireNonNull(reasonForBookingField);
        Objects.requireNonNull(foodAndDrinkField);
        Objects.requireNonNull(furnitureMovedField);
        Objects.requireNonNull(disturbanceField);
        Objects.requireNonNull(chargingAttendanceFeeField);
        Objects.requireNonNull(isExternalSpeakerField);
        Objects.requireNonNull(acceptConditionsField);
        Objects.requireNonNull(confirmBookingButton);
        Objects.requireNonNull(bookingConfirmationTable);
    }
}
