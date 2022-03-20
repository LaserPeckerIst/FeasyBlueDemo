package com.feasycom.feasyblue

import android.text.Html
import android.text.Spanned
import com.swallowsonny.convertextlibrary.toHexString
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        Assert.assertEquals(4, (2 + 2).toLong())


    }

    @Test
    fun test() {
        val s = "123"
        println(s.toByteArray().toHexString())
    }


    fun formatColor(currentPage: Int, totalPage: Int): Spanned {
        return Html.fromHtml("<font color='#646f7f'  size='5'>" + "当前第" + "</font>" +
                "&nbsp;&nbsp" + "<font color='#a7002b'>" + currentPage + "&nbsp;&nbsp" + "</font>" +
                "<font color='#646f7f'  size='5'>" + "页,共" + "</font>" + "&nbsp;&nbsp" + "<font color='#a7002b'>" + totalPage + "&nbsp;&nbsp" + "</font>"
                + "<font color='#646f7f'  size='5'>" + "页" + "</font>")
    }

    val array: Array<out String>? = null



    @Test
    fun s() {
        val a = 15          // 每层十五个
        println(32 % 16)
    }



    @ExperimentalTime
    @Test
    fun c() = runBlocking {
        var measureTimedValue = measureTimeMillis {
            launch {
            }
        }
        println(measureTimedValue)
    }

    @Test
    fun a() {
        var a = "vvvvvvvvvvvvvvvvvvvvvvva"
        // val regex = "[0-9a-fA-F|\\s]*"
        val regex = "[^(0-9a-fA-F|\\s)*]"
        /*val p = Pattern.compile(regex)
        val m: Matcher = p.matcher(a)
        println(m.matches())
        if (m.matches()){
            println("1111111111111111111111111111111111111111")
            println( a)
        }else {
            println("22222222222222222222222222222222222222222")
            println(m.replaceAll(""))
        }*/

        println(regex.toRegex().matches(a))
    }

    @Test
    fun aaa(){
        println("1".toByteArray())
    }
}





/*
GlobalScope.launch(Dispatchers.IO) {
    val body = async {
        val api = TokenApi.get()
        val token = api.getToken(Keys.STAFFID)
        token.execute().body()
    }.await()
    return  "body?.data?.signToken.toString()"
}*/
