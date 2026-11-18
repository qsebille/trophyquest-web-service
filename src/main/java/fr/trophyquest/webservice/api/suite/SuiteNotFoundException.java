package fr.trophyquest.webservice.api.suite;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SuiteNotFoundException extends RuntimeException {
    public SuiteNotFoundException(UUID suiteId) {
        super("Suite not found by suite id: " + suiteId);
    }
}
