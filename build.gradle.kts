plugins {
    java
}

group = "dev.nautchkafe.scylladb.bridge"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.vavr:vavr:0.10.5")

    implementation("com.datastax.oss:java-driver-core:4.17.0") 
}
