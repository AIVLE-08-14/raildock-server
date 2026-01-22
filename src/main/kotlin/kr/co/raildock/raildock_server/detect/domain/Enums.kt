package kr.co.raildock.raildock_server.detect.domain

enum class DetectJobStatus {
    PENDING, RUNNING, COMPLETED, FAILED
}

enum class VideoType {
    INSULATOR, RAIL, NEST
}