package com.amzport.dingtalk.model

case class AccessToken(
                        access_token: String,
                        errmsg: String,
                        expires_in: Long,
                        errcode: Long = 0L
                      )
