package `in`.edak.avsu.parser

import org.jsoup.Jsoup
import java.lang.RuntimeException
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class AvsuGrabber(
    val userName: String,
    val password: String,
    val pupilId: Long
) {
    companion object {
        val SITE = "https://www.avsu.ru"
        val GET_PHPSESSION_PAGE = "/loginparent/"
        val AUTH_PAGE = "/client/"
        val AUTH_PAGE_HEADERS = mapOf("Referer" to "$SITE/loginparent/")

        val BAR_PAGE = "/client/getbar"

        val HTTP_CODE_OK = 200
        val HTTP_CODE_REDIRECT = 301
        val HTTP_CODE_REDIRECT2 = 302
        val SESSION_COOKIE = "PHPSESSID"
    }

    var needSession: Boolean = true
    var needAuth: Boolean = true

    var httpRequester = HttpRequester()

    fun getPhpSession() {
        val url = "$SITE$GET_PHPSESSION_PAGE"
        val response = httpRequester.getRequest(url)
        if (response.code != HTTP_CODE_OK)
            throw AvsuFatal("Could not get site")
    }

    fun login() {
        val url = "$SITE$AUTH_PAGE"
        val session = httpRequester.getCookieValue(SESSION_COOKIE) ?: throw AvsuNeedSession()
        val params = mapOf(
            "avsu_sess" to session,
            "avsu_type" to "100",
            "avsu_nick" to userName,
            "avsu_pass" to password,
            "avsu_but" to "Войти"
        )
        val response = httpRequester.postRequest(url, params, AUTH_PAGE_HEADERS)
        if (response.code == HTTP_CODE_REDIRECT) throw AvsuNeedAuth()
    }

    fun getData(day: Int, month: Int, year: Int): BarData {
        //date_start1=01&date_start2=09&date_start3=2019&date_end1=01&date_end2=10&date_end3=2019&pupil_id=15125761
        val url = "$SITE$BAR_PAGE"
        val params = mapOf(
            "date_start1" to day.toString(),
            "date_start2" to month.toString(),
            "date_start3" to year.toString(),
            "date_end1" to day.toString(),
            "date_end2" to month.toString(),
            "date_end3" to year.toString(),
            "pupil_id" to pupilId.toString()
        )
        val response = httpRequester.postRequest(url, params, null)
        if (response.code == HTTP_CODE_REDIRECT ||
            response.code == HTTP_CODE_REDIRECT2) throw AvsuNeedAuth()
        if (response.body == null) throw AvsuFatal("ERROR NO DATA")
        val barData = parseBarResponse(response.body)
        return barData
    }

    private fun parseBarResponse(html: String): BarData {
        val parse = Jsoup.parse(html)
        val itemsDiv = parse.select("div[class=\"dataHeaderSale\"]")
        val balaceMather = "\\d+(\\.\\d*)?".toRegex().find(itemsDiv.last().text())
        val balanceOut = balaceMather!!.value.toBigDecimal()

        val yearText = "\\d+$".toRegex().find(parse.select("span[id='date_start']").text())!!.value

        val items = itemsDiv.subList(1, itemsDiv.size - 1).map {
            val fieldsDiv = it.select("div")

            val dateText = fieldsDiv[1].text() // 03.10 06:34
            val date = SimpleDateFormat("dd.MM hh:mm yyyy").parse("$dateText $yearText")

            val item = fieldsDiv[3].text() // Батончик Марс 50 гр.

            val priceText = fieldsDiv[4].text()
            val price = "(\\+|-)?\\d+(\\.\\d*)?".toRegex().find(priceText)!!.value.toBigDecimal().negate()

            val cntText = fieldsDiv[5].text()
            val cnt = "\\d+".toRegex().find(cntText)!!.value.toInt()

            BarItem(date, item, price, cnt)
        }
        return BarData(items, balanceOut)
    }

    data class BarData(
        val items: List<BarItem>,
        val balanceOut: BigDecimal
    )

    data class BarItem(
        val date: Date,
        val item: String,
        val price: BigDecimal,
        val count: Int
    )

    fun grab(): BarData {
        val date = LocalDateTime.now()
        var res: BarData? = null
        var errCnt = 0
        while (res == null && errCnt < 5) {
            try {
                if (needSession) {
                    getPhpSession()
                    needSession = false
                }
                if (needAuth) {
                    login()
                    needAuth = false
                }
                res = getData(date.dayOfMonth, date.monthValue, date.year)
            } catch (e: AvsuException) {
                errCnt++
                when (e) {
                    is AvsuNeedSession -> needSession = true
                    is AvsuNeedAuth -> needAuth = true
                }
            }
        }
        if (res == null) throw AvsuFatal("Many errors")
        return res
    }

    open class AvsuException(msg: String?) : RuntimeException(msg) {}
    class AvsuNeedSession : AvsuException(null) {}
    class AvsuNeedAuth : AvsuException(null) {}
    class AvsuFatal(msg: String) : AvsuException(msg) {}
}