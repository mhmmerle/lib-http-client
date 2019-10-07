plugins {
    id("email.haemmerle.baseplugin").version("0.0.5")
}

group = "email.haemmerle.restclient"
description = "RESTful HTTP Client Library"

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

`email-haemmerle-base`{
    username = "mhmmerle"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.github.kittinunf.fuel:fuel:2.2.1")
    implementation("com.beust:klaxon:5.0.13")
    implementation("org.apache.logging.log4j:log4j-api:2.11.1")
    implementation("org.apache.logging.log4j:log4j-core:2.11.1")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.2")
    testImplementation("org.assertj:assertj-core:3.11.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.2.0")
}
