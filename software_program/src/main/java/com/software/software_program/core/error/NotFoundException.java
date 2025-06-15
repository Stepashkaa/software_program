package com.software.software_program.core.error;

public class NotFoundException extends RuntimeException {
    public <T> NotFoundException(Class<T> classT, Long id) {
        super(String.format("%s with id [%s] is not found or not exists", classT.getSimpleName(), id));
    }
    public NotFoundException(Class<?> entityClass, String message) {
        super(String.format("Entity of type %s not found: %s", entityClass.getSimpleName(), message));
    }
    public NotFoundException(String message) {
        super(message);
    }

}
