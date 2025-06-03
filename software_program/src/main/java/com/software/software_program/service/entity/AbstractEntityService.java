package com.software.software_program.service.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
    public abstract class AbstractEntityService<T> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected void log(String message) {
        logger.info(message);
    }

    protected void handleException(Exception e) {
        logger.error("An error occurred: ", e);
    }
    protected void validateStringField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be null or empty");
        }
    }

    protected abstract void validate(T entity, boolean uniqueCheck);
}

