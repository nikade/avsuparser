package `in`.edak.avsu.parser

import org.junit.Test

import org.junit.Assert.*

class PropertiesObjTest {
    @Test
    fun getUsername() {
        assertNotNull(PropertiesObj.username)
        assertNotNull(PropertiesObj.pupilId)
        assertNotNull(PropertiesObj.hourBegin)
    }
}