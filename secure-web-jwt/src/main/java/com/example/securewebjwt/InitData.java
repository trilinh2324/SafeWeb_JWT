package com.example.securewebjwt;

import com.example.securewebjwt.model.Role;
import com.example.securewebjwt.model.User;
import com.example.securewebjwt.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class InitData {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public InitData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener
    public void onReady(ApplicationReadyEvent ev) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("adminpass"))
                    .fullName("Administrator")
                    .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER))
                    .locked(false)
                    .build();
            userRepository.save(admin);
            System.out.println("Admin user created: admin / adminpass");
        }
    }
}
