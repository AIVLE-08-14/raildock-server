package kr.co.raildock.raildock_server.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "aws")
data class AwsProperties(
    val region: String,
    val s3: S3,
    val cloudfront: CloudFront
) {
    data class S3(
        val bucket: String
    )

    data class CloudFront(
        val domain: String
    )
}
