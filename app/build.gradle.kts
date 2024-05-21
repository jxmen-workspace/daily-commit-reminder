/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.7/userguide/building_java_projects.html in the Gradle documentation.
 */

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.jvm)

    // Apply the application plugin to add support for building a CLI application in Java.
    application

    /**
     * 종속성과 프로젝트 코드를 단일 jar로 패키징하는 shadow plugin
     * 예제에서는 5.2.0 버전이었지만 최신버전으로 등록하니 된다
     *
     * https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow
     */
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

val awsJavaLambdaCoreVersion = "1.2.3"
val telegramBotVersion = "6.9.7.1"
val okHttpVersion = "4.12.0"

dependencies {
    // This dependency is used by the application.
    implementation(libs.guava)

    // https://www.kodeco.com/5777183-write-an-aws-lambda-function-with-kotlin-and-micronaut
    implementation("com.amazonaws:aws-lambda-java-core:$awsJavaLambdaCoreVersion") // lambda 배포를 위한 라이브러리 (kotlin도 lambda-java를 사용)
    implementation("org.telegram:telegrambots:$telegramBotVersion")
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")

    // Use the Kotlin JUnit 5 integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // Use the JUnit 5 integration.
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    // Define the main class for the application.
    mainClass = "org.example.AppKt"
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
