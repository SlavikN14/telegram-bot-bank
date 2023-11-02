plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("plugin.spring") version "1.9.0"
    kotlin("plugin.allopen") version "1.9.0"
}

dependencies {
    implementation(project(":internal-api"))
    implementation(project(":finance-models"))

    implementation("io.nats:jnats:2.16.14")

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.projectreactor:reactor-core:3.5.11")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")

    testImplementation("io.projectreactor:reactor-test:3.5.11")
    testImplementation("com.willowtreeapps.assertk:assertk:0.27.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}
