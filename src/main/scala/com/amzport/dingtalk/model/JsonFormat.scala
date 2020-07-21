package com.amzport.dingtalk.model

import play.api.libs.json.{Json, OFormat}

object JsonFormat {

  implicit val AccessTokenFormat: OFormat[AccessToken] = Json.format[AccessToken]

}
