package com.gs.api.api.exceptions;

import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class ApiErrors {

    private final List<String> errors;

    public ApiErrors(BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
    }

    public ApiErrors(String message) {
        this.errors = new ArrayList<>();
        this.errors.add(message);
    }

    public ApiErrors(ResponseStatusException e) {
        this.errors = Collections.singletonList(e.getReason());
    }
}
