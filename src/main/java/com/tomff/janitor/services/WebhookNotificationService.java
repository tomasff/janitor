package com.tomff.janitor.services;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.tomff.janitor.BookingConfirmation;
import com.tomff.janitor.RoomBooking;
import com.tomff.janitor.configuration.WebhookNotificationConfiguration;

import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public class WebhookNotificationService implements NotificationService {
    private static final Logger logger = Logger.getLogger(WebhookNotificationService.class.getName());

    private static final int COLOR_BASE = 0x08121F;
    private static final int COLOR_ERROR = 0xE63B31;
    private static final int COLOR_SUCCESS = 0x24A428;

    private final WebhookNotificationConfiguration configuration;
    private final WebhookClient webhookClient;

    public WebhookNotificationService(WebhookNotificationConfiguration configuration, ScheduledExecutorService executor) {
        this.configuration = configuration;
        this.webhookClient = new WebhookClientBuilder(configuration.url())
                .setExecutorService(executor)
                .setWait(true)
                .build();
    }

    private WebhookMessageBuilder buildMessageBase() {
        return new WebhookMessageBuilder()
                .setUsername(configuration.username())
                .setAvatarUrl(configuration.avatarUrl());
    }

    private String buildCodeblock(String content) {
        return "`" + content + "`";
    }

    @Override
    public void notifyEngaged() {
        logger.info("Sending notification: engaged.");

        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(COLOR_BASE)
                .setTitle(new WebhookEmbed.EmbedTitle("Enabled", null))
                .setDescription(
                        """
                        I am C-3PO, human/cyborg relations and janitor.
                        Anxiously waiting for room bookings...
                        """
                ).build();

        WebhookMessage message = buildMessageBase()
                .addEmbeds(embed)
                .build();

        webhookClient.send(message);
    }

    @Override
    public void notifyVersionChange(String version) {
        logger.info("Sending notification: version changed to " + version);

        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(COLOR_BASE)
                .setTitle(new WebhookEmbed.EmbedTitle("Room Booking", version))
                .addField(new WebhookEmbed.EmbedField(false, "Version", buildCodeblock(version)))
                .setDescription(
                        """
                        It is I, C-3PO, room bookings are now open!
                        I suggest a new strategy, R2: book the rooms.
                        """
                ).build();

        WebhookMessage message = buildMessageBase()
                .setContent(
                        ":rotating_light: <@&" + configuration.roleId() + ">"
                )
                .addEmbeds(embed)
                .build();

        webhookClient.send(message);
    }

    @Override
    public void notifyFailedBooking(RoomBooking booking) {
        logger.warning("Sending notification: failed to secure booking " + booking);

        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(COLOR_ERROR)
                .setTitle(new WebhookEmbed.EmbedTitle("Booking failed", null))
                .addField(new WebhookEmbed.EmbedField(false, "Booking", buildCodeblock(booking.toString())))
                .setDescription("Listen to them, they're dying, R2! Curse my metal body, I wasn't fast enough!")
                .build();

        WebhookMessage message = buildMessageBase()
                .addEmbeds(embed)
                .build();

        webhookClient.send(message);
    }

    @Override
    public void notifyBookedSuccessfully(RoomBooking booking, BookingConfirmation confirmation) {
        logger.info("Sending notification: secured booking " + booking + " with confirmation " + confirmation);

        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(COLOR_SUCCESS)
                .setTitle(new WebhookEmbed.EmbedTitle("Booking confirmed", null))
                .addField(new WebhookEmbed.EmbedField(true, "Booking", buildCodeblock(booking.toString())))
                .addField(new WebhookEmbed.EmbedField(true, "Confirmation", buildCodeblock(confirmation.toString())))
                .setDescription("This isn't the afterlife, is it? Are droids allowed here?")
                .build();

        WebhookMessage message = buildMessageBase()
                .addEmbeds(embed)
                .build();

        webhookClient.send(message);
    }
}
