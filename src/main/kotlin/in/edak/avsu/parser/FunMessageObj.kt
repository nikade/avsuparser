package `in`.edak.avsu.parser

import java.math.BigDecimal

object FunMessageObj {
    val samples = listOf(
        "Машеньека изволили отведать \n%s\nОстаток %s р.",
        "Сегодня в столовой подавали \n%s\nОстаток %s р."
    )

    fun message(items: List<AvsuGrabber.BarItem>, balanceOut: BigDecimal): String {
        val messageSample = samples.random()
        val msg = items.map {
            val cntStr = if(it.count>1) "* ${it.count}" else ""
            "${it.item} ${it.price.toString()} р. $cntStr"
        }.joinToString("\n")
        return messageSample.format(msg,balanceOut.toString())
    }
}