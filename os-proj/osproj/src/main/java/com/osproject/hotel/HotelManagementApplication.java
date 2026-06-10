package com.osproject.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling   // <-- NEW: allows background scheduler to run
public class HotelManagementApplication {
	public static void main(String[] args) {
		SpringApplication.run(HotelManagementApplication.class, args);
		System.out.println("🏨 Hotel Management System Started Successfully!");
		System.out.println("🌐 Visit: http://localhost:8080");
	}
}