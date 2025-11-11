//package co.edu.icesi.sidgymicesi.config;
//
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.FilterType;
//import org.springframework.context.annotation.Profile;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//
//@Configuration
//@Profile("postgres")
//@EntityScan(basePackages = {
//        "co.edu.icesi.sidgymicesi.model"
//})
//@EnableJpaRepositories(
//        basePackages = {
//                "co.edu.icesi.sidgymicesi.repository",
//                "co.edu.icesi.sidgymicesi.repository.postgres"
//        },
//        excludeFilters = @ComponentScan.Filter(
//                type = FilterType.REGEX,
//                pattern = "co\\.edu\\.icesi\\.sidgymicesi\\.repository\\.mongo\\..*"
//        )
//)
//public class JpaPostgresConfig {
//}
