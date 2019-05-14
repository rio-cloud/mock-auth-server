[![Build Status](https://travis-ci.com/rio-cloud/mock-auth-server.svg?branch=master)](https://travis-ci.com/rio-cloud/mock-auth-server)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Mock Auth Server

Integrate a mock auth server within your Kotlin application to issue tokens for development and testing purpose.

## Usage

Within a (integration) test that requires a token

```kotlin
import cloud.rio.iam.auth.MockAuthServer

MockAuthServer().use {
    val port = it.start()
    
    // do request a token on the random selected port on localhost   
    
    // do some code/tests with the token
    
    // server is closed automatically after the use-block 
}
```

## License

Mock Auth Server is licensed under the [Apache 2.0 license](https://github.com/rio-cloud/mock-auth-server/blob/master/LICENSE).
