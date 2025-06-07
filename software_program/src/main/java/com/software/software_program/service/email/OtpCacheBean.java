package com.software.software_program.service.email;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.software.software_program.core.configuration.Constants;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@AllArgsConstructor
public class OtpCacheBean {
    @Bean
    public LoadingCache<String, Integer> loadingCache() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(Constants.OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    @NonNull
                    public Integer load(@NonNull String key) {
                        return 0;
                    }
                });
    }
}
