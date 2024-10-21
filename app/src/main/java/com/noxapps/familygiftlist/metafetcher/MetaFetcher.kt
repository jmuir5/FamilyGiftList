package com.noxapps.familygiftlist.metafetcher

import android.util.Log
import com.noxapps.familygiftlist.mygifts.normaliseLink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


class MetaFetcher private constructor(
    val document: Document,
    val target: String,
){

    fun getTitle():String{
        return document.selectFirst(
            """
                meta[name=title],
                meta[property='og:title']
            """.trimIndent())?.attr("content")?.cleanse() ?: ""
    }
    fun getDescription():String{
        return document.selectFirst(
            """
                meta[name=description],
                meta[property='og:description']
            """.trimIndent())?.attr("content")?.cleanse() ?: ""
    }

    fun getImageString():String{
        Log.d("image","looking for image")
        val candidates: Elements = document.select("""
            meta[property='og:image'],
            meta[property='og:image:url'],
            meta[itemprop=image],
            meta[name='twitter:image:src'],
            meta[name='twitter:image'],
            meta[name='twitter:image0']
        """.trimIndent())
        candidates.forEachIndexed{index, element ->
            Log.d("candidates image $index", element.attr("content"))
        }
        val images = document.getElementsByTag("img")
        images.forEachIndexed { index, element ->
            //Log.d("images image $index", element.attr("src"))
            //Log.d("images image alt $index", element.attr("alt"))

            if((element.attr("alt")!= null&&element.attr("alt")!="")&& this.getTitle().contains(element.attr("alt")))
                candidates.add(element)
        }

        //candidate?.attr("content")?.let { Log.d("image", it)}
        return try {
            val content = candidates[0]?.attr("content")?.cleanse() ?: ""
            val src = candidates[0]?.attr("src")?.cleanse() ?: ""
            if(content.length>src.length)
                content
            else
                src
        }catch(e: Exception){
            ""
        }
    }

    fun String.cleanse(): String {
        return this.trim()
            .replace("null", "")
            .replace("""[\r\n\t]""".toRegex(), " ")
            .replace("""\s\s+""".toRegex(), " ")
            .replace("""<!--.+?-->""", "")
            .replace("""�""", "")
    }

    companion object {
        suspend operator fun invoke(target:String): MetaFetcher? {
            val normalisedTarget = target.normaliseLink()
            try {
                val doc = withContext(Dispatchers.IO) {
                    Jsoup.connect(normalisedTarget)
                        .userAgent("Mozilla")
                        .timeout(10000)
                        .get()
                }

                return MetaFetcher(doc, normalisedTarget)
            }
            catch (e:Exception){
                Log.d("metafetcher failed", "target: $target")
                Log.d("metafetcher failed", "reason: $e")
                return MetaFetcher(Document(""), "null")
            }
        }
    }
}
