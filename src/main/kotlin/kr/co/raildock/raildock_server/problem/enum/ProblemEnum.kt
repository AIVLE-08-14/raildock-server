package kr.co.raildock.raildock_server.problem.enum

/**
 * RailType
 * ⚠ 주의:
 * 이 enum은 법적/행정적 철도 분류를 엄밀히 표현하기 위한 것이 아니라,
 * "AI 탐지 데이터셋 및 시나리오 문서에서 사용되는 분류 체계"를 그대로 반영한 것이다.
 * 따라서 서로 다른 분류 축(법적/기술적/시스템 규모)이 혼합되어 있으며,
 * 본 도메인에서는 이를 '노선/시스템 환경 구분용 태그'로 사용한다.
 */
enum class RailType {

    HIGH_SPEED,   // 고속철도 (KTX, SRT 등 / 국가철도·고속 운행 환경)
    GENERAL,      // 일반철도 (국가철도 일반선 / 여객·화물 혼재)
    URBAN,        // 도시철도 (지하철, 중전철 시스템)
    LIGHT_RAIL,   // 경전철 (LRT, 무인·소형 차량 시스템)
    COMMON        // 공통 (철도 유형과 무관하게 적용되는 결함/문제)
}

/**
 * Severity
 * 시나리오 문서 및 AI 탐지 결과에서 사용되는 심각도 분류.
 * 철도 유지보수 관점에서 "운행 영향도 + 안전 위험도"를 기준으로 한다.
 * S  : 즉시 조치 필요 (운행 중단 또는 긴급 통제 수준)
 * X2 : 단기 내 조치 필요 (운행 가능하나 위험 증가)
 * X1 : 중장기 조치 대상 (즉각적 위험은 없음)
 * O  : 경미 / 관찰 대상 (기록 및 추적 목적)
 * E  : 오탐 또는 환경 요인 (조치 불필요)
 */

enum class Severity {

    S,   // Severe : 즉각적인 안전 위협, 긴급 조치 필요
    X2,  // Major  : 단기 조치 필요, 위험도 높음
    X1,  // Minor  : 중장기 관리 대상
    O,   // Observe: 관찰 대상, 영향 미미
    E    // Error/False : 오탐 또는 의미 없는 이벤트
}

enum class ProblemStatus {
    UNASSIGNED,     // 미할당 (분석만 완료된 상태)
    ASSIGNED,       // 작업자/팀에 할당됨
    RESOLVED,       // 조치 완료
    FALSE_POSITIVE  // 오탐으로 판정됨
}
