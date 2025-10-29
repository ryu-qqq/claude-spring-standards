package com.ryuqq.domain.example;

public record ExampleContent(
    String message
) {

    public static ExampleContent of(String message){
        return new ExampleContent(message);
    }
}
