package test

import com.amzport.dingtalk.TestMain

object Test extends App {

  val token = TestMain.getToken(
    "ding9nasiqs9ufex5eye",
    "0rUItItPFBJkbcT79TcCFrRBYwmzjibPi6m4jxAAViDwjSLbNZbsAbWIkVMKKcgy"
  )

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

  token match {
    case Left(error) =>
      println(error.errmsg)
    case Right(_token) =>
      println(_token)
  }

  TestMain.getToken(
    "ding9nasiqs9ufex5eye123",
    "0rUItItPFBJkbcT79TcCFrRBYwmzjibPi6m4jxAAViDwjSLbNZbsAbWIkVMKKcgy"
  ) match {
    case Left(error) =>
      println(error)
    case Right(_token) =>
      println(_token)
  }

}
