package cgg.informatique.abl.webSocket;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebSocketApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(WebSocketApplication.class, args);
	}
	@Override
	public void run(String... args) {}
}
