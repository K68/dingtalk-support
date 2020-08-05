package com.amzport.dingtalk

import com.amzport.dingtalk.model.{ErrorMsg, GetByMobile}
import com.dingtalk.api.DefaultDingTalkClient
import com.dingtalk.api.request.OapiUserGetByMobileRequest
import com.dingtalk.api.response.OapiUserGetByMobileResponse
import com.taobao.api.TaobaoResponse

import scala.util.Try

object TestUser {
  def handleResult[U <: TaobaoResponse, T](result: Try[U], factory: U => T): Either[ErrorMsg, T] = {
    if (result.isFailure) {
      Left(ErrorMsg(result.failed.get.getMessage))
    } else {
      val data = result.get
      if (data.getErrorCode.toIntOption.getOrElse(-1) != 0) {
        Left(ErrorMsg(data.getBody, data.getErrorCode.toLongOption.getOrElse(-1)))
      } else {
        Right(factory(result.get))
      }
    }
  }

  def getByMobile(token: String,mobile: String): Either[ErrorMsg,GetByMobile] = {
    val client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get_by_mobile")
    val req = new OapiUserGetByMobileRequest()
    req.setMobile(mobile)
    req.setHttpMethod("GET")

    val result = Try(client.execute(req,token))
    handleResult[OapiUserGetByMobileResponse,GetByMobile](result,data => {
      GetByMobile(data.getErrmsg,data.getUserid)
    })
  }

//  def getUserInfo(token: String,userId: String): Either[ErrorMsg,]

}
