package `in`.edak.avsu.parser

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.lang.RuntimeException
import java.util.Properties

object PropertiesObj {
    val PROPERTIES_FILENAME = "avsugrabber.properties"
    var username: String = ""
    var password: String = ""
    var pupilId: Long = 0
    var telegaToken: String? = null
    var telegaChatId: Long? = null
    var hourBegin: Int = 9
    var hourEnd: Int = 15
    var proxyHost: String? = null
    var proxyPort: Int? = null
    var proxyUsername: String? = null
    var proxyPassword: String? = null
    var mqttBrokerUrl: String? = null
    var mqttClientId: String? = null
    var mqttQueue: String? = null
    var mqttUsername: String? = null
    var mqttPassword: String? = null

    init {
        val properties = Properties()
        var propertiesInputSteam: InputStream?
        try {
            propertiesInputSteam = FileInputStream(PROPERTIES_FILENAME)
        } catch(e: FileNotFoundException) {
            propertiesInputSteam = javaClass.classLoader.getResourceAsStream(PROPERTIES_FILENAME)
        }
        if(propertiesInputSteam == null) throw RuntimeException("could not load ${PROPERTIES_FILENAME}")
        properties.load(propertiesInputSteam.reader(Charsets.UTF_8))
        username = properties.get("username") as String
        password = properties.get("password") as String
        pupilId = (properties.get("pupilId") as String).toLong()
        telegaToken = properties.get("telegaToken") as String?
        telegaChatId = (properties.get("telegaChatId") as String?)?.toLong()
        hourBegin = (properties.get("hourBegin") as String).toInt()
        hourEnd = (properties.get("hourEnd") as String).toInt()
        proxyHost = properties.get("proxyHost") as String?
        proxyPort = (properties.get("proxyPort") as String?)?.toInt()
        proxyUsername = properties.get("proxyUsername") as String?
        proxyPassword = properties.get("proxyPassword") as String?
        mqttBrokerUrl = properties.get("mqtt.brokerUrl") as String?
        mqttClientId = properties.get("mqtt.clientId") as String?
        mqttQueue = properties.get("mqtt.queue") as String?
        mqttUsername = properties.get("mqtt.clientUser") as String?
        mqttPassword = properties.get("mqtt.clientPassword") as String?
    }

}