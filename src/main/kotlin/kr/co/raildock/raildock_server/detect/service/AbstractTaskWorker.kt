package kr.co.raildock.raildock_server.detect.service

import kr.co.raildock.raildock_server.detect.domain.ProblemDetectionEntity
import kr.co.raildock.raildock_server.detect.repository.ProblemDetectionRepository
import org.springframework.transaction.annotation.Transactional

abstract class AbstractTaskWorker (
    val detectRepo: ProblemDetectionRepository
){
    @Transactional
    open fun processOnePending(): Boolean{
        val candidate = findCandidate() ?: return false

        val claimed = tryStart(candidate.id!!)
        if (claimed == 0) return true

        val pd = detectRepo.findById(candidate.id!!).orElseThrow()

        return try{
            execute(pd)
            true
        } catch (e: Exception){
            onError(pd, e)
            true
        }
    }
    protected abstract fun findCandidate(): ProblemDetectionEntity?
    protected abstract fun tryStart(id: Long): Int
    protected abstract fun execute(pd: ProblemDetectionEntity)
    protected abstract fun onError(pd: ProblemDetectionEntity, e: Exception)
}