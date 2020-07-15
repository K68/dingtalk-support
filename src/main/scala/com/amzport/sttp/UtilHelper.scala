package com.amzport.sttp

import play.api.libs.json.{JsValue, Json}

import scala.concurrent.duration._
import sttp.client.okhttp.WebSocketHandler

class UtilHelper {

  import sttp.client._
  import sttp.client.okhttp.OkHttpSyncBackend

  implicit val sttpBackend: SttpBackend[Identity, Nothing, WebSocketHandler] = OkHttpSyncBackend()

  def postJson(toSend: JsValue, url: String): Either[String, String] = {
    basicRequest.post(uri = uri"$url").readTimeout(20.seconds)
      .header("Content-Type", "application/json")
      .body(Json.stringify(toSend)).send().body
  }

}
