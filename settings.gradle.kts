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
        // kakao login을 위한 과정
        jcenter() // Warning: this repository is going to shut down soon
        //KakaoSDK repository
        maven (url = "https://jitpack.io")
        maven (url = "https://devrepo.kakao.com/nexus/content/groups/public/")
    }
}

rootProject.name = "CloseTFrontEnd"
include(":app")
 