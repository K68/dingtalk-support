package com.amzport.dingtalk

import com.amzport.dingtalk.model.{AccessAsyncsend, AccessToken, ErrorMsg}
import com.dingtalk.api.DefaultDingTalkClient
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request.Image
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request.{File, Msg, Text}
import com.dingtalk.api.response.{OapiGettokenResponse, OapiMessageCorpconversationAsyncsendV2Response}
import com.taobao.api.TaobaoResponse

import scala.collection.mutable
import scala.util.Try

object TestMessage {
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

  def corpconversationAsyncsend(token: String, agentId: Long, useridList: String, msgType: String, msgContent:mutable.Map[String,String])
  : Either[ErrorMsg,AccessAsyncsend] = {
    val client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2")
    val req = new OapiMessageCorpconversationAsyncsendV2Request()
    req.setAgentId(agentId)
    req.setUseridList(useridList)
    val obj1 = new Msg()
    obj1.setMsgtype(msgType)
    msgType match {
      case "text" =>
        val obj2 = new Text()
        obj2.setContent(msgContent("msgContent"))
        obj1.setText(obj2)
      case "image" =>
        val obj2 = new Image()
        obj2.setMediaId(msgContent("msgContent"))
        obj1.setImage(obj2)
      case "file" =>
        val obj2 = new File()
        obj2.setMediaId(msgContent("msgContent"))
        obj1.setFile(obj2)
    }
    req.setMsg(obj1)

    val result = Try(client.execute(req,token))
    handleResult[OapiMessageCorpconversationAsyncsendV2Response, AccessAsyncsend](result, data => {
      AccessAsyncsend(data.getRequestId,data.getTaskId)
    })

  }

}
