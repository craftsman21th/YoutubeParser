//apply(from = "versions.gradle")
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "YoutubeParser"
include(":app")
include(":youtube_parser")
include(":lib_network")
include(":lib_component_base")
include(":lib_business_kernel")
include(":lib_business_base_module")
