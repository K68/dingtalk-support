package com.amzport.sttp

import akka.util.ByteString
import play.api.http.HttpEntity
import play.api.{Configuration, Logging}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Result, Results}

import scala.jdk.CollectionConverters._
import scala.concurrent.duration._
import com.amzport.repo.model.SmsEntity
import sttp.client.okhttp.WebSocketHandler
import sttp.model.{Header, Method}

class UtilHelper extends Logging {

  import sttp.client._
  import sttp.client.okhttp.OkHttpSyncBackend

  implicit val sttpBackend: SttpBackend[Identity, Nothing, WebSocketHandler] = OkHttpSyncBackend()

  val preDefHeadJsonContent = Seq(("Content-Type", "application/json"))

  var superRestUrl: String = ""
  var postgrest_header_keys: List[String] = List.empty[String]

  var utilsSmsPin: String = ""
  var utilsSmsUrl: String = ""
  var utilsSmsBatchUrl: String = ""
  var utilsSmsExpire: Long = 0

  var webServiceApi= ""
  var webServiceKey= ""

  var md2htmlURL = ""

  def init(configuration: Configuration): Unit = {
    superRestUrl = configuration.underlying.getString("action.super.rest.url")
    postgrest_header_keys = configuration.underlying.getStringList("postgrest.header.keys").asScala.toList

    utilsSmsPin = configuration.underlying.getString("action.utils.sms.pin")
    utilsSmsUrl = configuration.underlying.getString("action.utils.sms.url")
    utilsSmsBatchUrl = configuration.underlying.getString("action.utils.sms.batchurl")
    utilsSmsExpire = configuration.underlying.getLong("action.utils.sms.expire")

    webServiceApi = configuration.underlying.getString("web.service.api")
    webServiceKey = configuration.underlying.getString("web.service.key")

    md2htmlURL = configuration.underlying.getString("service.md2html.url")
  }

  def superRest(httpMethod: String, urlParams: String,
                jsonBody: Option[JsValue], mapHeader: Seq[(String, String)]): Result = {

    val url = s"$superRestUrl$urlParams"
    logger.debug(url)

    val _mapHeader = mapHeader.filter{ case (k, _) => postgrest_header_keys.contains(k) }.map(i => Header(i._1, i._2))
    val request = if (_mapHeader.nonEmpty) {
      basicRequest.method(Method(httpMethod), uri"$url").readTimeout(1.minute).headers(_mapHeader: _*)
    } else {
      basicRequest.method(Method(httpMethod), uri"$url").readTimeout(1.minute)
    }

    val rep = if (jsonBody.isDefined) {
      val rawBody = Json.stringify(jsonBody.get)
      if(rawBody == "{}" || rawBody == "[]") {
        request.send()
      } else {
        request.body(rawBody).send()
      }
    } else {
      request.send()
    }

    val body = rep.body match {
      case Left(lv) => lv
      case Right(rv) => rv
    }
//    val body = if (rep.body.isRight) rep.body.right.get else rep.body.left.get

    val headers = rep.headers.filter{
      case Header(k, _) => postgrest_header_keys.contains(k) && k != "Content-Type"
    }.map(i => (i.name, i.value))
    val contentType = rep.header("Content-Type")
    val entity = HttpEntity.Strict(ByteString(body), contentType)
    Results.Status(rep.code.code).sendEntity(entity).withHeaders(headers: _*) // : _* 变长用法
  }

  def postJsonInner(toSend: JsValue, url: String): Either[String, String] = {
    postJson(toSend, s"$superRestUrl$url")
  }

  def postJson(toSend: JsValue, url: String): Either[String, String] = {
    basicRequest.post(uri = uri"$url").readTimeout(20.seconds)
      .header("Content-Type", "application/json")
      .body(Json.stringify(toSend)).send().body
  }

  def md2html(md: String): String = {
    if (md2htmlURL.isEmpty) {
      ""
    } else {
      val toSend = Json.obj("md" -> md)
      postJson(toSend, md2htmlURL) match {
        case Right(body) => body
        case Left(error) => error
      }
    }
  }

  def sendSms(pn: String, code: String): Boolean = {
    val toSend = Json.obj(fields = "pn" -> pn, "code" -> code, "tpl" -> utilsSmsPin)
    postJson(toSend, utilsSmsUrl) match {
      case Right(_) =>
        true
      case Left(e) =>
        logger.error(s"Send SMS Error: $e")
        false
    }
  }

  def sendSmsBatch(messages: Seq[SmsEntity]): Boolean = {
    val entities = messages.map(i => s"${i.pn};${i.msg}")
    val toSend = Json.obj("tpl" -> utilsSmsPin, "accountList" -> entities)
    postJson(toSend, utilsSmsBatchUrl) match {
      case Right(_) =>
        true
      case Left(e) =>
        logger.error(s"Send SMS Batch Error: $e")
        false
    }
  }

  def fetchAccountTx(contractAddress: String, address: String, startblock: Long, page: Int, offset: Int, sort: String): Option[String] = {
    // asc desc
    // startblock 、endblock
    val fetchUrl = s"$webServiceApi?module=account&action=tokentx&contractaddress=$contractAddress&address=$address&page=$page&offset=$offset&sort=$sort&apikey=$webServiceKey&startblock=$startblock"
    basicRequest.get(uri"$fetchUrl").readTimeout(1.minute).send().body.toOption
  }

}
