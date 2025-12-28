plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.4"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
}

group = "com"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("mysql:mysql-connector-java:8.0.33")

	// Google Cloud 
	implementation("org.springframework.cloud:spring-cloud-gcp-starter:1.2.5.RELEASE")
	implementation("org.springframework.cloud:spring-cloud-gcp-storage:1.2.5.RELEASE")
    implementation("com.google.cloud:google-cloud-vision:3.34.0")

	// Swagger
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.0")

	// Jackson
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	// Webflux
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	// Netty
	implementation("io.netty:netty-resolver-dns-native-macos:4.1.123.Final:osx-aarch_64")

    // Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // RabbitMQ
    implementation("org.springframework.boot:spring-boot-starter-amqp")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
