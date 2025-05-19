package com.imaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.imaging.entity.User;
import com.imaging.repository.UserRepo;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class Init {

	@Autowired
	private UserRepo userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(Init.class, args);
	}

	@PostConstruct
	public void loadUsers() {
		User user1 = new User();
		user1.setId(1L);
		user1.setUsername("user1");
		user1.setPassword(passwordEncoder.encode("password"));

		User user2 = new User();
		user2.setId(2L);
		user2.setUsername("user2");
		user2.setPassword(passwordEncoder.encode("password"));

		userRepository.save(user1);
		userRepository.save(user2);
	}
}
