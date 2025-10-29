package com.ryuqq.application.example.dto.command;

public record ExampleCommand(
    Long id,
    String message
) {

    public static ExampleCommand of(String message){
        return new ExampleCommand(null, message);
    }

    //for Update
    public static ExampleCommand of(Long id, String message){
        return new ExampleCommand(id, message);
    }

}
