package com.amzport.dingtalk.model

case class AccessAsyncsend(
                            request_id: String,
                            task_id: Long,
                            errcode: Long = 0L
                          )
