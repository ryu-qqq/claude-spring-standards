package com.ryuqq.adapter.in.rest.example.dto.response;

public record ExampleApiResponse(
    String message
) {

    public static ExampleApiResponse fromResponse(String message) {
        return new ExampleApiResponse(message);
    }
}
