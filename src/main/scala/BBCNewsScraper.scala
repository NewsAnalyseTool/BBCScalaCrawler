
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.libs.json.Json.{JsValueWrapper, parse}
import play.api.libs.json._

import java.text.SimpleDateFormat
import java.util.Date
import scala.io.Source
import scala.util.Using
import scala.xml.{Elem, XML}

object BBCNewsScraper {
  //define the date format
  val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
  def start(): JsArray = {
    val worldNewsUrl = "https://feeds.bbci.co.uk/news/world/rss.xml"
    val topNewsUrl = "https://feeds.bbci.co.uk/news/rss.xml"
    val allNewsLinks = scrapeAllNewsLinks(worldNewsUrl, topNewsUrl)
    val newsPageInfoSet = allNewsLinks.map(scrapeNewsPageInfo)
    val jsonArray = toJsonArray(newsPageInfoSet)

    val numEntries = jsonArray.value.size
    println(s"\nNumber of entries in the JSON array: $numEntries")

    jsonArray
  }

  private case class NewsPageInfo(source: String, title: String, text: String, category: String, date: String, url: String)

  private def scrapeAllNewsLinks(worldNewsUrl: String, topNewsUrl: String): Set[String] = {
    val worldNewsLinks = scrapeNewsLinks(worldNewsUrl)
    val topNewsLinks = scrapeNewsLinks(topNewsUrl)

    // Combine and eliminate duplicates
    worldNewsLinks ++ topNewsLinks
  }

  private def scrapeNewsLinks(url: String): Set[String] = {
    Using.resource(Source.fromURL(url)) { source =>
      val xmlString = source.mkString
      val xml: Elem = XML.loadString(xmlString)
      val newsLinks = (xml \ "channel" \ "item" \ "link").map(_.text).toSet
      newsLinks
    }
  }

  implicit val jsoupDocumentReleasable: Using.Releasable[Document] = (_: Document) => ()

  private def scrapeNewsPageInfo(newsLink: String): NewsPageInfo = {
    Using.resource(Jsoup.connect(newsLink).get()) { document =>
      val source = "BBC"
      val title = document.select("h1").text()
      val text = document.select("article").text()
      val category = document.select("meta[property=article:section]").attr("content")
      val date = document.select("time[datetime]").attr("datetime")
      val url = newsLink

      NewsPageInfo(source, title, text, category, date, url)
    }
  }

  private def toJsonArray(newsPageInfoSet: Set[NewsPageInfo]): JsArray = {
    val jsonArray: JsArray = if (newsPageInfoSet.nonEmpty) {
      Json.arr(newsPageInfoSet.toSeq.map { pageInfo =>
        Json.obj(
          "source" -> Json.toJson(pageInfo.source),
          "title" -> Json.toJson(pageInfo.title),
          "text" -> Json.toJson(pageInfo.text),
          "category" -> Json.toJson(pageInfo.category),
          "date" -> Json.toJson(pageInfo.date),
          "url" -> Json.toJson(pageInfo.url)
        ): JsValueWrapper
      }: _*)
    } else {
      Json.arr(Json.obj("entry" -> "NoData"))
    }

    jsonArray
  }
}