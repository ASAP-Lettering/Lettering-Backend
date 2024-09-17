apply {
    plugin(Plugins.KOTLIN_JPA.id)
}

dependencies{
    implementation(Dependencies.Spring.DATA_JPA)
    runtimeOnly(Dependencies.DATABASE.MYSQL)
    runtimeOnly(Dependencies.DATABASE.H2)
}