package com.amzport.dingtalk

import com.amzport.dingtalk.model.{AccessToken, ErrorMsg}
import com.dingtalk.api.DefaultDingTalkClient
import com.dingtalk.api.request.OapiGettokenRequest

import scala.util.Try
//import model.JsonFormat._
//import play.api.libs.json.Json

object TestMain {

  def getToken(appKey: String, appSecret: String): Either[ErrorMsg, AccessToken] = {
    val client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken")
    val req = new OapiGettokenRequest()
    req.setAppkey(appKey)
    req.setAppsecret(appSecret)
    req.setHttpMethod("GET")

    val result = Try(client.execute(req))
    if (result.isFailure) {
      Left(ErrorMsg(result.failed.get.getMessage))
    } else {
      val data = result.get
      if (result.get.getErrcode > 0) {
        Left(ErrorMsg(data.getErrmsg, data.getErrcode))
      } else {
        // Json.parse(result.get.getBody).asOpt[AccessToken]
        Right(AccessToken(data.getAccessToken, data.getErrmsg, data.getExpiresIn))
      }
    }
  }

}
