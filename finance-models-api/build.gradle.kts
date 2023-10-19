plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("plugin.spring") version "1.9.0"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
}
