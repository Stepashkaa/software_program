package com.software.software_program.core.eror;

public class NotFoundException extends RuntimeException {
    public <T> NotFoundException(Class<T> classT, Long id) {
        super(String.format("%s with id [%s] is not found or not exists", classT.getSimpleName(), id));
    }
}
