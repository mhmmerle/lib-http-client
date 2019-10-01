plugins {
    id("email.haemmerle.baseplugin").version("0.0.5")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

`email-haemmerle-base` {
    username = "mhmmerle"
}
