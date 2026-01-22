package com.apexpm;
import com.apexpm.domain.entity.ApexUser;
import com.apexpm.domain.enumerator.Role;
import com.apexpm.repository.ApexUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class ApexBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApexBackendApplication.class, args);
	}

	@Bean
	CommandLineRunner initUsers(ApexUserRepository userRepository, PasswordEncoder encoder) {
		return args -> {
			if (!userRepository.existsByUsername("admin")) {
				ApexUser admin = ApexUser.builder()
						.username("admin")
						.password(encoder.encode("admin123"))
						.email("admin@apexpm.com")
						.fullName("Admin User")
						.roles(Set.of(Role.ADMIN))
						.build();
				userRepository.save(admin);
				System.out.println("Admin user created: " + admin.getUsername());
			}
		};
	}

}
