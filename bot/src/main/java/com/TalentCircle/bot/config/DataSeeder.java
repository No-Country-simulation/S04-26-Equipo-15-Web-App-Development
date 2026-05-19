package com.TalentCircle.bot.config;

import com.TalentCircle.bot.user.entity.UserEntity;
import com.TalentCircle.bot.user.enums.Role;
import com.TalentCircle.bot.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

//@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {

        return args -> {

            if (userRepository.findByEmail("admin@talentcircle.com")
                    .isEmpty()) {

                UserEntity admin = new UserEntity(
                        "admin@talentcircle.com",
                        passwordEncoder.encode("123456"),
                        Role.ADMIN
                );

                userRepository.save(admin);

                System.out.println("ADMIN USER CREATED");
            }

            if (userRepository.findByEmail("editor@talentcircle.com")
                    .isEmpty()) {

                UserEntity editor = new UserEntity(
                        "editor@talentcircle.com",
                        passwordEncoder.encode("123456"),
                        Role.EDITOR
                );

                userRepository.save(editor);

                System.out.println("EDITOR USER CREATED");
            }
                        System.out.println("""
            ================================
            DEFAULT USERS CREATED
            admin@talentcircle.com / 123456
            editor@talentcircle.com / 123456
            ================================
            """);
        };
    }
}