package com.asap.client

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "client")
class ClientProperties {
    var oauth: OAuthProperties = OAuthProperties()
}

class OAuthProperties {
    var naver: NaverOAuthProperties = NaverOAuthProperties()
}

class NaverOAuthProperties {
    var clientId: String = ""
    var clientSecret: String = ""
}
