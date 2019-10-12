package `in`.edak.avsu.parser

import org.junit.Test

class AvsuGrabberTest {
    val avsuGrabber = AvsuGrabber(PropertiesObj.username,PropertiesObj.password, PropertiesObj.pupilId)

    @Test
    fun testAll() {
        avsuGrabber.getPhpSession()
        avsuGrabber.login()
        avsuGrabber.getData(3,10,2019)
    }
}