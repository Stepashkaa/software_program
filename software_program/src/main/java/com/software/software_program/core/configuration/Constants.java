package com.software.software_program.core.configuration;

public class Constants {
    public static final String API_URL = "/api/v1";

    public static final String ADMIN_PREFIX = "/admin";

    public static final String LOGIN_URL = "/login";

    public static final String LOGOUT_URL = "/logout";

    public static final String DEFAULT_PAGE_SIZE = "5";

    public static final String PHONE_PATTERN = "^((8|\\+374|\\+994|\\+995|\\+375|\\+7|\\+380|\\+38|\\+996|\\+998|\\+993)[\\- ]?)?\\(?\\d{3,5}\\)?[\\- ]?\\d{1}[\\- ]?\\d{1}[\\- ]?\\d{1}[\\- ]?\\d{1}[\\- ]?\\d{1}(([\\- ]?\\d{1})?[\\- ]?\\d{1})?$";

    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[!@#\\$%\\^&\\*\\(\\)\\-_+=;:,\\./\\?\\\\|`~\\[\\]\\{\\}])(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z!@#\\$%\\^&\\*\\(\\)\\-_+=;:,\\./\\?\\\\|`~\\[\\]\\{\\}]{8,60}$";

    public static final String NAME_PATTERN = "^[A-Za-zА-Яа-яЁё\\-]{1,20}$";

    private Constants() {
    }
}