package `in`.edak.avsu.parser.main

import `in`.edak.avsu.parser.AvsuRunner
import java.lang.Exception
import java.util.*
import kotlin.concurrent.schedule


object MainScheduler {
    private lateinit var avsuRunner: AvsuRunner

    @JvmStatic
    fun main(args: Array<String>) {
        Thread.sleep(60000)
        avsuRunner = AvsuRunner()
        Timer().schedule(
            1000,
            10*60*1000 // period 10 minutes
        ) {
            try {
                avsuRunner.runProcess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        Thread.currentThread().join()
    }
}