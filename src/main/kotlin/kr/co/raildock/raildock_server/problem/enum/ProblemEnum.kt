package kr.co.raildock.raildock_server.problem.enum

// TODO : 실제와 맞게 변경 필요
enum class ProblemType {
    CRACK,
    DAMAGE,
    CORROSION,
    OTHER
}

enum class Severity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class ProblemStatus {
    UNASSIGNED,   // 미할당
    ASSIGNED,     // 할당됨
    IN_PROGRESS,  // 진행중
    RESOLVED      // 완료
}