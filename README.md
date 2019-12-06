[![Build Status](https://travis-ci.com/rio-cloud/mock-auth-server.svg?branch=master)](https://travis-ci.com/rio-cloud/mock-auth-server)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/cloud.rio/mock-auth-server?server=https%3A%2F%2Foss.sonatype.org&label=Sonatype%20Nexus%20%28Snapshots%29)](https://oss.sonatype.org/#nexus-search;gav~cloud.rio~mock-auth-server~~~)

# Mock Auth Server

Integrate a mock auth server within your Kotlin application to issue tokens for development and testing purpose.

## Include library

Currently only SNAPSHOTS are available at the sonatype snapshot repository. Add the following to your
`buidl.gradle.kts`.

```kotlin
repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    implementation("cloud.rio:mock-auth-server:0.2.0-SNAPSHOT")
}
```


## Usage

Within a (integration) test that requires a token

```kotlin
import cloud.rio.iam.auth.MockAuthServer

fun test() {

    MockAuthServer().use {
        val port = it.start()
        
        // do request a token on the random selected port on localhost   
        
        // do some code/tests with the token
        
        // server is closed automatically after the use-block 
    }

}
```

## License

Mock Auth Server is licensed under the [Apache 2.0 license](https://github.com/rio-cloud/mock-auth-server/blob/master/LICENSE).
