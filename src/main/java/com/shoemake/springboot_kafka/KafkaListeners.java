package com.shoemake.springboot_kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    @KafkaListener(
        topics = "foobar",
        groupId = "group-1"
    )
    void listener(String data) {
        System.out.println("Listener received: " + data);
    }
}
