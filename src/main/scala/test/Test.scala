package test

import com.amzport.dingtalk.{TestMain, TestMessage, TestUser}
import play.api.libs.json.Json

import scala.collection.mutable

object Test extends App {

  val token = TestMain.getToken(
    "ding9nasiqs9ufex5eye",
    "0rUItItPFBJkbcT79TcCFrRBYwmzjibPi6m4jxAAViDwjSLbNZbsAbWIkVMKKcgy"
  )
  token match {
    case Left(error) =>
      println(error.errmsg)
    case Right(_token) =>
      println(_token)
  }
//  val mapContent = new mutable.Map[String]("msgContent" -> "hello111")
  val asyncsend = TestMessage.corpconversationAsyncsend(
    "ed2dce63f36a372b83e4626f12403b01",
    835154839,
//    "054466244126610499",
    "282118392126133327",
    "file",
    mutable.Map("msgContent" -> "hello111")

  )
  asyncsend match {
    case Left(error) =>
      println(error.errmsg)
    case Right(_asyncsend) =>
      println(_asyncsend)
  }

  val getByMobile = TestUser.getByMobile(
    "ed2dce63f36a372b83e4626f12403b01",
    "18858325247"
  )
  getByMobile match {
    case Left(error) =>
      println(error.errmsg)
    case Right(_getByMobile) =>
      println(_getByMobile)
  }

  /*
  token match {
    case Some(_token) =>
      println(_token)
      println(_token.access_token)

    case None => println("no access_token")
  }

  if (token.isDefined) {
    println(token.get)
  } else {
    println("no access_token")
  }

  TestMain.getToken(
    "ding9nasiqs9ufex5eye111",
    "0rUItItPFBJkbcT79TcCFrRBYwmzjibPi6m4jxAAViDwjSLbNZbsAbWIkVMKKcgy"
  ) match {
    case Some(_token) => println(_token)
    case None => println("error access_token")
  }
  */


//  val ticket = TestMain.getTicket("13108da120593b9da51a7dc69e935fae")
//  ticket match {
//    case Left(error) =>
//      println(error.errmsg)
//    case Right(_ticket) =>
//      println(_ticket.access_token)
//  }

//  val userInfo = TestMain.getUserInfo("13108da120593b9da51a7dc69e935fae")
//  userInfo match {
//    case Left(error) =>
//      println(error.errmsg)
//    case Right(_userInfo) =>
//      println(_userInfo.userid)
//  }


}
