package com.ryuqq.domain.example;

public record ExampleId(
    Long id
) {

    public static ExampleId of(Long id){
        return new ExampleId(id);
    }

}
