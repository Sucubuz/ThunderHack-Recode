plugins {
	id 'fabric-loom' version '1.10-SNAPSHOT'
	id 'maven-publish'
}

sourceCompatibility = JavaVersion.VERSION_21
targetCompatibility = JavaVersion.VERSION_21

archivesBaseName = project.archives_base_name
version = project.mod_version
group = project.maven_group

loom {
	accessWidenerPath = file("src/main/resources/thunderhack.accesswidener")
}

repositories {
	maven {
		name = "jitpack.io"
		url = "https://jitpack.io"
	}
	maven {
		name = 'swt-repo'
		url = "https://maven-eclipse.github.io/maven"
	}
	maven {
		name = "meteor-maven"
		url = "https://maven.meteordev.org/releases"
	}
	maven {
		name = "meteor-maven-snapshots"
		url = "https://maven.meteordev.org/snapshots"
	}

	mavenCentral()
}

configurations {
	libImpl
	modImpl
}

dependencies {
	minecraft "com.mojang:minecraft:${project.minecraft_version}"
	mappings "net.fabricmc:yarn:${project.yarn_mappings}:v2"
	modImplementation "net.fabricmc:fabric-loader:${project.loader_version}"
	modImplementation "net.fabricmc.fabric-api:fabric-api:${project.fabric_version}"

	libImpl("meteordevelopment:orbit:${project.orbit_version}")

	modCompileOnly "meteordevelopment:baritone:1.21-SNAPSHOT"

	libImpl("io.netty:netty-handler-proxy:${project.netty_version}") { transitive = false }
	libImpl("io.netty:netty-codec-socks:${project.netty_version}") { transitive = false }

	configurations.libImpl.dependencies.each {
		implementation(it)
	}

	configurations.modImpl.dependencies.each {
		modImplementation(it)
		include(it)
	}
}

processResources {
	inputs.property "version", project.version

	filesMatching("fabric.mod.json") {
		expand "version": project.version
	}
}

tasks.withType(JavaCompile).configureEach {
	it.options.release = 21
}

java {
	withSourcesJar()
}

jar {
	from("LICENSE") {
		rename { "${it}_${project.archivesBaseName}"}
	}
	from {
		configurations.libImpl.collect { it.isDirectory() ? it : zipTree(it) }
	}
	duplicatesStrategy(DuplicatesStrategy.EXCLUDE)

	manifest {
		attributes(
				'Implementation-Title': project.name,
				'Implementation-Version': project.version,
				'Built-By': System.getProperty('user.name'),
		)
	}
}

publishing {
	publications {
		mavenJava(MavenPublication) {
			from components.java
		}
	}

	repositories {
	}
}
