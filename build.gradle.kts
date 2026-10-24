plugins {
    java
}

group = "dev.nautchkafe.scylladb.bridge"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.vavr:vavr:1.1.0")

    implementation("com.datastax.oss:java-driver-core:4.17.0") 
}
