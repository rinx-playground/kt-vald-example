import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    application
    kotlin("jvm") version "1.4.32"
    id("com.google.protobuf") version "0.8.16"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
}

apply(plugin = "kotlin")
apply(plugin = "com.google.protobuf")
apply(plugin = "org.jlleitschuh.gradle.ktlint")

group = "io.github.rinx.playground"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("io.github.rinx.playground.vald.example.ApplicationKt")
}

repositories {
    mavenLocal()
    mavenCentral()
    google()
}

val grpcVersion = "1.39.0"
val grpcKotlinVersion = "1.1.0"
val protobufVersion = "3.15.8"

val ktorVersion = "1.6.1"

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

sourceSets {
    main {
        proto {
            srcDir("vald")
            srcDir("vendor/src")

            exclude("vendor/**/*.proto")
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")

    // protobuf
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("com.google.protobuf:protobuf-java-util:$protobufVersion")
    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")

    // gRPC
    implementation("io.grpc:grpc-netty:$grpcVersion")

    // ktor
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-gson:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.2.3")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk7@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}
