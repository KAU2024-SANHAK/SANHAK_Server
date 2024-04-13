package org.kau.kkoolbeeServer;

import org.kau.kkoolbeeServer.domain.diary.Diary;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class KkoolbeeServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(KkoolbeeServerApplication.class, args);
		TimeZone tz = TimeZone.getDefault();
		System.out.println("현재 시간대: " + tz.getID());

	}

}
