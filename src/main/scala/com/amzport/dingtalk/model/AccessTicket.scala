package com.amzport.dingtalk.model

case class AccessTicket(
                         access_token: String,
                         errmsg: String,
                         expires_in: Long,
                         errcode: Long = 0L
                       )
