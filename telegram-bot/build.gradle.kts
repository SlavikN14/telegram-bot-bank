val telegramBotVersion = "6.8.0"

plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("plugin.spring") version "1.9.0"
    kotlin("plugin.allopen") version "1.9.0"
}

dependencies {
    implementation(project(":internal-api"))

    implementation("org.telegram:telegrambots:$telegramBotVersion")
    implementation("org.telegram:telegrambots-spring-boot-starter:$telegramBotVersion")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.nats:jnats:2.16.14")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.kafka:spring-kafka:3.0.12")

    implementation("io.projectreactor.kafka:reactor-kafka:1.3.21")
    implementation("org.apache.kafka:kafka-clients:3.6.0")
    implementation("io.confluent:kafka-protobuf-serializer:7.5.1")

    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
    implementation("io.projectreactor:reactor-core")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
