package com.shoemake.springboot_kafka.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private String name;
    private String socialSecurityNumber;
    private int age;
}