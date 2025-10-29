package com.ryuqq.application.example.dto.response;

public record ExampleResponse(
    Long id,
    String message
) {

    public static ExampleResponse of(Long id, String message){
        return new ExampleResponse(id, message);
    }
}
