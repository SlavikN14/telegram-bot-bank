plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("plugin.spring") version "1.9.0"
    kotlin("plugin.allopen") version "1.9.0"
}

dependencies {
    implementation(project(":internal-api"))
    implementation(project(":finance-service:finance"))

    implementation("io.nats:jnats:2.16.14")

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")

    testImplementation("io.projectreactor:reactor-test:3.5.11")
    testImplementation("com.willowtreeapps.assertk:assertk:0.27.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

subprojects {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.allopen")

    dependencies {
        implementation(project(":internal-api"))
        implementation("io.nats:jnats:2.16.14")

        implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")

        testImplementation("io.projectreactor:reactor-test:3.5.11")
        testImplementation("com.willowtreeapps.assertk:assertk:0.27.0")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
        enabled = false
    }

    tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> {
        enabled = false
    }
}

tasks.test {
    useJUnitPlatform()
}
