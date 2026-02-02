package kr.co.raildock.raildock_server.problem.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID
import kr.co.raildock.raildock_server.problem.enum.*

@Entity
@Table(name = "problems")
class ProblemEntity(

    /** 문제(결함) ID */
    @Id
    @GeneratedValue
    val id: UUID? = null,

    /** 탐지 결과 ID */
    // TODO : 탐지결과 FK 해줘야함
    @Column(nullable = false)
    var detectionId: UUID,

    /** 업무용 결함 번호 */
    @Column(nullable = false, length = 100)
    var problemNum: String,

    /** 담당자 ID (미할당 가능) */
    @Column(nullable = true)
    var managerId: Long? = null,

    /** 문제 처리 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ProblemStatus = ProblemStatus.UNASSIGNED,

    /** 위도 */
    @Column(nullable = false)
    var latitude: Double,

    /** 경도 */
    @Column(nullable = false)
    var longitude: Double,

    /** 촬영 / 탐지 시각 */
    @Column(nullable = false)
    var detectedTime: LocalDateTime,

    /** 결함 유형 (텍스트 기반 분류) */
    @Column(nullable = false, length = 100)
    var problemType: String,

    /** 권장 조치내용 **/
    @Column(nullable = false)
    var recommendedActions : String,

    /** 위험 등급 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var severity: Severity,

    /** 위험 등급 판정 근거 */
    @Column(nullable = true,)
    var severityReason: String? = null,

    /** 참고 규정 / 기준 */
    @Column(nullable = true)
    var reference: String? = null,

    /** 철도 분류 (데이터셋 기준) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var railType: RailType,

    /** 결함 발생 부품 */
    @Column(nullable = false, length = 100)
    var component: String,

    /** 지역 */
    @Column(nullable = true, length = 100)
    var region: String? = null,

    /** 날씨 */
    @Column(nullable = true)
    var weather: String? = null,

    /** 온도 */
    @Column(nullable = true)
    var temperature: Int? = null,

    /** 습도 */
    @Column(nullable = true)
    var humidity: Int? = null,

    /** 원본 이미지 ID */
    @Column(nullable = true)
    var sourceImageId: UUID,

    /** Bounding Box JSON ID */
    @Column(nullable = true)
    var boundingBoxJsonId: UUID
)