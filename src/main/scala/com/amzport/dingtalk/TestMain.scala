package com.amzport.dingtalk

import com.amzport.dingtalk.model.{AccessTicket, AccessToken, ErrorMsg, UserInfo}
import com.dingtalk.api.DefaultDingTalkClient
import com.dingtalk.api.request.{OapiGetJsapiTicketRequest, OapiGettokenRequest, OapiUserGetuserinfoRequest}
import com.dingtalk.api.response.OapiGettokenResponse
import com.taobao.api.TaobaoResponse

import scala.util.Try
//import model.JsonFormat._
//import play.api.libs.json.Json

object TestMain {

  def handleResult[U <: TaobaoResponse, T](result: Try[U], factory: U => T): Either[ErrorMsg, T] = {
    if (result.isFailure) {
      Left(ErrorMsg(result.failed.get.getMessage))
    } else {
      val data = result.get
      if (data.getErrorCode.toIntOption.getOrElse(-1) != 0) {
        Left(ErrorMsg(data.getBody, data.getErrorCode.toLongOption.getOrElse(-1)))
      } else {
        // Json.parse(result.get.getBody).asOpt[AccessToken]
        Right(factory(result.get))
      }
    }
  }

  def getToken(appKey: String, appSecret: String): Either[ErrorMsg, AccessToken] = {
    val client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken")
    val req = new OapiGettokenRequest()
    req.setAppkey(appKey)
    req.setAppsecret(appSecret)
    req.setHttpMethod("GET")

    val result = Try(client.execute(req))
    handleResult[OapiGettokenResponse, AccessToken](result, data => {
      AccessToken(data.getAccessToken, data.getErrmsg, data.getExpiresIn)
    })
    /*
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
    */
  }

  def getTicket(token: String): Either[ErrorMsg, AccessTicket] = {
    val client = new DefaultDingTalkClient("https://oapi.dingtalk.com/get_jsapi_ticket")
    val req = new OapiGetJsapiTicketRequest()
    req.setHttpMethod("GET")

    val result = Try(client.execute(req, token));
    if(result.isFailure) {
      Left(ErrorMsg(result.failed.get.getMessage))
    } else {
      val data = result.get
      if(result.get.getErrcode > 0) {
        Left(ErrorMsg(data.getErrmsg, data.getErrcode))
      } else {
        Right(AccessTicket(data.getTicket,data.getErrmsg,data.getExpiresIn))
      }
    }
  }

  def getUserInfo(token: String): Either[ErrorMsg,UserInfo] = {
    val client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getuserinfo")
    val req = new OapiUserGetuserinfoRequest()
    req.setHttpMethod("GET")

    println(token)
    val result = Try(client.execute(req, token))
    if(result.isFailure) {
      Left(ErrorMsg(result.failed.get.getMessage))
    } else {
      val data = result.get
      if(result.get.getErrcode > 0) {
        Left(ErrorMsg(data.getErrmsg, data.getErrcode))
      } else {
        Right(UserInfo(data.getUserid, data.getSysLevel, data.getErrmsg, data.getIsSys))
      }
    }
  }





}
