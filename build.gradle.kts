plugins {
    id("email.haemmerle.baseplugin").version("1.0.3")
}

group = "email.haemmerle.restclient"
description = "RESTful HTTP Client Library"

`email-haemmerle-base`{
    username = "mhmmerle"
}

dependencies {
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.12.1"))
    testImplementation(platform("org.junit:junit-bom:5.5.2"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.github.kittinunf.fuel:fuel:2.2.1")
    implementation("com.beust:klaxon:5.0.13")
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.assertj:assertj-core:3.11.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.2.0")

    testRuntimeOnly("org.junit.platform:junit-platform-console")
}

java {
    withJavadocJar()
    withSourcesJar()
}