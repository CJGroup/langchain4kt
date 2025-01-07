import com.vanniktech.maven.publish.SonatypeHost
import java.net.URI

plugins {
    `maven-publish`
    signing
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates((group as String), name, version.toString())
    pom {
        name.set("Langchain4kt")
        description.set("KMP library for generic LLM application")
        url.set("https://github.com/CJGroup/langchain4kt")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                name.set("Stream")
            }
        }
        scm {
            url.set("https://github.com/CJGroup/langchain4kt")
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = URI("https://maven.pkg.github.com/CJGroup/langchain4kt")
            credentials {
                username = System.getenv("GITHUB_ACTOR")!!
                password = System.getenv("GITHUB_TOKEN")!!
            }
        }
    }
}

signing {
    if (project.hasProperty("signing.gnupg.keyName")) {
        useGpgCmd()
        sign(publishing.publications)
    }
}
