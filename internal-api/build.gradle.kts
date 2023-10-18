plugins{
    id("com.google.protobuf") version "0.9.4"
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:3.24.3")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.24.3"
    }
}
