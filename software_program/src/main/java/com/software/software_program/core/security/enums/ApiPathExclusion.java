package com.software.software_program.core.security.enums;

import com.software.software_program.core.configuration.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiPathExclusion {
    WEBJARS("/webjars/**"),
    STATICS("/static/**"),
    LOGIN(Constants.API_URL + Constants.LOGIN_URL),
    OTP_VERIFICATION(Constants.API_URL + "/verify-otp"),
    OTP_VERIFICATION_DIRECT(Constants.API_URL + "/verify-otp-direct"),
    SEND_OTP(Constants.API_URL + "/send-otp"),
    INVALIDATE_OTP(Constants.API_URL + "/invalidate-otp"),
    REFRESH_TOKEN(Constants.API_URL + "/refresh-token"),
    REFRESH_TOKEN_DIRECT(Constants.API_URL + "/refresh-token-direct"),
    SWAGGER_UI("/swagger-ui/**"),
    SWAGGER_UI_HTML("/swagger-ui.html"),
    API_DOCS("/v3/api-docs/**");

    private final String path;
}
