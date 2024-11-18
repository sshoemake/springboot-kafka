package com.shoemake.springboot_kafka.controller;

import java.io.InputStream;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.shoemake.springboot_kafka.model.Result;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/v1/TestResults")
@Slf4j
public class TestResultsRestController {

    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.template.default-topic}")
    private String defaultTopic;

    public TestResultsRestController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public Result publish(@RequestBody String requestStr) throws JsonProcessingException {
        log.info("Request Json String: " + requestStr);
        InputStream schemaAsStream = TestResultsRestController.class.getClassLoader().getResourceAsStream("model/testresults.schema.json");
        JsonSchema schema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(schemaAsStream);

        ObjectMapper om = new ObjectMapper();
        // om.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
        JsonNode jsonNode = om.readTree(requestStr);

        Set<ValidationMessage> errors = schema.validate(jsonNode);
        String errorsCombined = "";
        for (ValidationMessage error : errors) {
            log.error("Validation Error: {}", error);
            errorsCombined += error.toString() + "\n";
        }

        if (errors.size() > 0)
            throw new RuntimeException("Please fix your json! " + errorsCombined);

        Result request = om.readValue(requestStr, Result.class);
        log.info("Return this request: {}", request);

        kafkaTemplate.send(defaultTopic, requestStr);

        return request;
    }

}
