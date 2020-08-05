package com.amzport.dingtalk.model

case class UserInfo(
                     userid: String,
                     sys_level: String,
                     errmsg: String,
                     is_sys: Boolean,
                     errcode: Long = 0L
                   )
