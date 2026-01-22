package kr.co.raildock.raildock_server.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class AwsConfig(
    @Value("\${aws.region}") private val region: String,
    @Value("\${aws.credentials.access-key:}") private val accessKey: String,
    @Value("\${aws.credentials.secret-key:}") private val secretKey: String
) {
    @Bean
    fun s3Client(): S3Client =
        S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider())
            .build()

    private fun credentialsProvider() =
        if (accessKey.isNotBlank() && secretKey.isNotBlank()) {
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            )
        } else {
            software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider.create()
        }
}
