package `in`.edak.avsu.parser

import `in`.edak.messages.Telega
import `in`.edak.messages.TelegaTopic
import `in`.edak.messages.Topic
import org.tinylog.kotlin.Logger
import java.time.LocalTime

class AvsuRunner{
    val avsuGrabber = AvsuGrabber(
        PropertiesObj.username,
        PropertiesObj.password,
        PropertiesObj.pupilId
    )
    val hoursToRun = PropertiesObj.hourBegin..PropertiesObj.hourEnd
    var currentItems = listOf<AvsuGrabber.BarItem>()
    val topics = mutableListOf<Topic>()

    init {
        if(PropertiesObj.telegaChatId != null && PropertiesObj.telegaToken != null) {
            val telegaMessagging = Telega(
                proxyHost = PropertiesObj.proxyHost,
                proxyPort = PropertiesObj.proxyPort,
                proxyUsername = PropertiesObj.proxyUsername,
                proxyPassword = PropertiesObj.proxyPassword
            )
            topics.add(TelegaTopic(PropertiesObj.telegaToken!!, PropertiesObj.telegaChatId!!, telegaMessagging))
        }
    }

    fun runProcess() {
        val currentHour = LocalTime.now().hour
        if (currentHour in hoursToRun) {
            Logger.info("start grab")
            val bar = avsuGrabber.grab()
            Logger.info("done grab")
            if (bar.items.isNotEmpty()) {
                val newItems = bar.items.minus(currentItems)
                currentItems = bar.items
                if(newItems.isNotEmpty()) {
                    val message = FunMessageObj.message(newItems, bar.balanceOut)
                    Logger.info { "message to send $message" }
                    topics.forEach {
                        it.send(message)
                    }
                    Logger.info("sending done")
                }
            } else Logger.info("bar is empty")
        } else println("skip")
    }
}