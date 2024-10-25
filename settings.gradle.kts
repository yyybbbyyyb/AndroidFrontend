pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven ("https://jitpack.io")  // 确保 JitPack 仓库也在这里
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven ("https://jitpack.io")
    }
}

rootProject.name = "My Application"
include(":app")
