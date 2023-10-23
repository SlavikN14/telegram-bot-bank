import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}
