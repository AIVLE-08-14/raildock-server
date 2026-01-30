package kr.co.raildock.raildock_server.detect.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DetectScheduler(
    private val detectWorker: DetectWorker,
    private val llmWorker: LlmWorker
) {
    @Scheduled(fixedDelayString = "\${raildock.detect.scheduler.delay:5000}")
    fun tick(){
        var ran = true
        var count = 0
        while(ran && count < 3){
            ran = detectWorker.runOnce()
            count++
        }
    }

    @Scheduled(fixedDelay = 5000)
    fun llmTick(){
        var ran = true
        var count = 0
        while(ran && count < 3){
            ran = llmWorker.runOnce()
            count++
        }
}
}