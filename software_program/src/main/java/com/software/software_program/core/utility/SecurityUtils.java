package com.software.software_program.core.utility;

import com.software.software_program.model.entity.UserEntity;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.userdetails.User;

import java.util.Set;

@UtilityClass
public class SecurityUtils {
    public static User convert(UserEntity user) {
        return new User(user.getEmail(), user.getPassword(), Set.of(user.getRole()));
    }
}
