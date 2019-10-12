package `in`.edak.messages

import `in`.edak.avsu.parser.PropertiesObj
import org.junit.Test

class TelegaTest {

    @Test
    fun sendToTopic() {
        val telegaMessaging = Telega("edak.info",80,null,null)
        telegaMessaging.sendToTopic(
            "Тест!",
            PropertiesObj.telegaChatId!!,
            PropertiesObj.telegaToken!!
        )
    }
}