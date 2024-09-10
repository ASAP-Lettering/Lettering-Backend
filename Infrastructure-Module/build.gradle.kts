subprojects{
    dependencies{
        implementation(project(Modules.COMMON_MODULE))
        implementation(project(Modules.APPLICATION_MODULE))
        implementation(project(Modules.DOMAIN_MODULE))
    }
}