import com.github.hal4j.uritemplate.URITemplate
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.apache.hc.client5.http.fluent.Request
import org.apache.hc.core5.http.ContentType

plugins {
	id("com.modrinth.minotaur") version "2.+"
	id("io.github.CDAGaming.cursegradle") version "1.6.0"
	signing
}

buildscript {
	dependencies {
		classpath("org.apache.httpcomponents.client5:httpclient5-fluent:5.2.1")
		classpath("com.github.hal4j:uritemplate:1.3.0")
	}
}

java {
	withJavadocJar()
	withSourcesJar()
}

val pswgVersionName = Regex("^0\\.([0-9]+)\\.([0-9]+)\\+(.+)$").matchEntire(version as String)?.destructured
	?.let { (minor, patch, mc) -> "PSWG 0.$minor.$patch-${if (minor.toInt() != 0) "beta" else "alpha"}-$mc" }


modrinth {
	token = rootProject.findProperty("githubToken") as? String?
	changelog = rootProject.file("CHANGELOG.md").readText()
	projectId = "9rBI0wQz"
	versionName = pswgVersionName
	versionType = "alpha"
	uploadFile = tasks.remapJar as Any
	required.project("P7dR8mSH") // Fabric API
	optional.project("mOgUt4GM") // Mod Menu
	optional.project("nfn13YXA") // REI
}

curseforge {
	apiKey = rootProject.findProperty("curseforgeToken") ?: ""
	project {
		id = "496522"
		changelogType = "markdown"
		changelog = rootProject.file("CHANGELOG.md")
		releaseType = "beta"
		relations {
			requiredDependency("fabric-api")
			optionalDependency("modmenu")
			optionalDependency("pswg-addon-clonewars")
			optionalDependency("roughly-enough-items")
		}

		mainArtifact(tasks.remapJar.get()) {
			displayName = pswgVersionName
		}
	}
}

tasks.curseforge {
	group = "publishing"
}

val github by tasks.registering {
	inputs.files(tasks.remapJar)

	group = "publishing"

	doFirst {
		check(pswgVersionName != null)
		val version = version as String
		val gson = Gson()
		val token = rootProject.findProperty("githubToken") as String
		val file = tasks.remapJar.get().archiveFile.get().asFile
		val createResponse = JsonParser.parseString(
			Request.post("https://api.github.com/repos/Parzivail-Modding-Team/GalaxiesParzisStarWarsMod/releases")
				.addHeader("Authorization", "Bearer $token")
				.bodyString(gson.toJson(jsonObject {
					"tag_name"(version)
					"name"(pswgVersionName)
					"body"(rootProject.file("CHANGELOG.md").readText())
					"draft"(true)
					"prerelease"(true)
				}), ContentType.APPLICATION_JSON)
				.execute()
				.returnContent()
				.asString(Charsets.UTF_8)
		).asJsonObject
		val uploadUrl = createResponse["upload_url"].asString
		Request.post(URITemplate(uploadUrl).expandOnly(mapOf("name" to file.name)).toURI())
			.addHeader("Authorization", "Bearer $token")
			.bodyFile(file, ContentType.create("application/java-archive"))
			.execute()
			.returnContent()
		val releaseUrl = createResponse["url"].asString
		Request.patch(releaseUrl)
			.addHeader("Authorization", "Bearer $token")
			.bodyString(gson.toJson(jsonObject {
				"draft"(false)
				//"make_latest"(true) // not allowed for pre-releases
			}), ContentType.APPLICATION_JSON)
			.execute()
			.returnContent()
	}
}

class JsonObjectDsl {
	val jsonObject = JsonObject()

	operator fun String.invoke(b: Boolean) = jsonObject.addProperty(this, b)
	operator fun String.invoke(s: String) = jsonObject.addProperty(this, s)
}

fun jsonObject(f: JsonObjectDsl.() -> Unit) = JsonObjectDsl().apply(f).jsonObject

publishing {
	publications {
		named<MavenPublication>("mavenJava") {
			pom {
				name = "Galaxies: Parzi's Star Wars Mod"
				description = "Explore the galaxy with Galaxies: Parzi's Star Wars Mod!"
				url = "https://pswg.dev/"
				licenses {
					license {
						name = "GNU Lesser General Public License Version 3"
						url = "https://www.gnu.org/licenses/lgpl-3.0.txt"
					}
				}
				developers {
					developer {
						id = "parzivail"
					}
				}
				scm {
					connection = "scm:git:https://github.com/Parzivail-Modding-Team/GalaxiesParzisStarWarsMod.git"
					developerConnection = "scm:git:git@github.com:Parzivail-Modding-Team/GalaxiesParzisStarWarsMod.git"
					url = "https://github.com/Parzivail-Modding-Team/GalaxiesParzisStarWarsMod"
				}
			}
		}
	}
	repositories {
		maven(url = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
			name = "central"

			if (project.hasProperty("ossrhUsername") && project.hasProperty("ossrhPassword"))
				credentials {
					username = project.findProperty("ossrhUsername") as String
					password = project.findProperty("ossrhPassword") as String
				}
		}
	}
}

signing {
	useGpgCmd()
	sign(publishing.publications["mavenJava"])
}

val modPublish by tasks.registering {
	dependsOn(tasks.modrinth, tasks.curseforge, github, "publishMavenJavaPublicationToCentralRepository")
	group = "publishing"
}

for (task in arrayOf(tasks.modrinth, tasks.curseforge, modPublish, tasks.named("publishMavenJavaPublicationToCentralRepository"))) {
	task {
		doFirst {
			check(pswgVersionName != null)
		}
	}
}
