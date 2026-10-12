package fr.trophyquest.backend.api.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CognitoUserInfo(
        String sub,
        String email,
        @JsonProperty("preferred_username")
        String preferredUsername
) {
}