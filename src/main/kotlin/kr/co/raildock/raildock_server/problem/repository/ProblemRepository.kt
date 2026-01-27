package kr.co.raildock.raildock_server.problem.repository

import kr.co.raildock.raildock_server.problem.entity.ProblemEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProblemRepository : JpaRepository<ProblemEntity, UUID>
