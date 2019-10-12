package `in`.edak.avsu.parser

import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl

class HttpRequester {
    companion object {
        private const val POST = "POST"
        private const val GET = "GET"
    }

    private var cookie: OwnCookieJar = OwnCookieJar()
    private var okHttpClient = OkHttpClient.Builder()
        .cookieJar(cookie)
        .followRedirects(false)
        .followSslRedirects(false)
        .build()

    fun getRequest(
        url: String,
        params: Map<String, String>? = null,
        headers: Map<String, String>? = null
    ): HttpRequestResult {
        return request(url, GET, params, headers)
    }

    fun postRequest(
        url: String,
        params: Map<String, String>? = null,
        headers: Map<String, String>? = null
    ): HttpRequestResult {
        return request(url, POST, params, headers)
    }

    private fun request(
        url: String,
        method: String,
        params: Map<String, String>? = null,
        headers: Map<String, String>? = null
    ): HttpRequestResult {
        var preparedUrl = url
        if (method == GET) {
            val urlBuilder = url.toHttpUrl().newBuilder()
            params?.forEach {
                urlBuilder.addQueryParameter(it.key, it.value)
            }
            preparedUrl = urlBuilder.toString()
        }

        var formBody: FormBody? = null
        if (method == POST) {
            val formBodyBuilder = FormBody.Builder()
            params?.forEach {
                formBodyBuilder.add(it.key, it.value)
            }
            formBody = formBodyBuilder.build()
        }

        val builder = Request.Builder().url(preparedUrl)

        if (formBody != null) builder.post(formBody)

        headers?.forEach {
            builder.addHeader(it.key, it.value)
        }

        val request = builder.build()

        val httpCall = okHttpClient.newCall(request)

        val response = httpCall.execute()
        val result = HttpRequestResult(
            response.code,
            if(response.body!=null)  String(response.body!!.bytes()) else null
        )
        response.close()
        return result
    }

    fun getCookieValue(cookieName: String): String? {
        return cookie.getCookie(cookieName)
    }

    class OwnCookieJar : CookieJar {
        private var cookiesMap: MutableMap<String, Cookie> = mutableMapOf()
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            cookies.forEach {
                cookiesMap[it.name] = it
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookiesMap.values.toList()
        }

        fun getCookie(name: String): String? {
            return cookiesMap[name]?.value
        }
    }

    data class HttpRequestResult(
        val code: Int,
        val body: String?
    )
}