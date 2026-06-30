package com.grayunited.payments.config;

import com.grayunited.payments.entity.Course;
import com.grayunited.payments.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    public CommandLineRunner seedCourses(CourseRepository repository) {
        return args -> {
            // Check if the database is empty before inserting
            if (repository.count() == 0) {
                logger.info("Database is empty. Seeding initial course data...");

                repository.save(new Course("soc-analyst-pro", "SOC Analyst Pro", 89900L, "Defensive Security Training"));
                repository.save(new Course("red-team-mastery", "Red Team Mastery", 149900L, "Offensive Security Training"));
                repository.save(new Course("ai-threat-intel", "AI Threat Intelligence", 129900L, "Advanced AI Security"));

                logger.info("Course data seeded successfully!");
            } else {
                logger.info("Course data already exists. Skipping seeder.");
            }
        };
    }
}