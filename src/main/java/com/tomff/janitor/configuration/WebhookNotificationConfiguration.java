package com.tomff.janitor.configuration;

import java.util.Objects;

public record WebhookNotificationConfiguration(String roleId, String url, String username, String avatarUrl) {
    public WebhookNotificationConfiguration {
        Objects.requireNonNull(roleId);
        Objects.requireNonNull(url);
        Objects.requireNonNull(username);
        Objects.requireNonNull(avatarUrl);
    }
}
