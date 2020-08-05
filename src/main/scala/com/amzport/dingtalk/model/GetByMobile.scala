package com.amzport.dingtalk.model

case class GetByMobile(
                        errmsg: String,
                        userid: String,
                        errcode: Long = 0L
                      )
