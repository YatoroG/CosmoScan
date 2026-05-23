plugins {
    java
    id("jacoco")
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation ("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            exclude(
                "sys/model/**",
                "sys/model/requests/**",
                "sys/utils/**",
                "sys/AnalysisApp.class"
            )
        }
    )

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}