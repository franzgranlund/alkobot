package controllers

import play.api.mvc._
import utils.LogAction

import scala.util.Random

object Webpage extends Controller {

  def index = LogAction { implicit request =>
    val happyUrl = urlList(Random.nextInt(urlList.size))

    Ok(views.html.index(happyUrl)).withHeaders(
      VARY -> "Accept-Encoding"
    )
  }

  val urlList = List(
    "http://www.playframework.com",
    "http://www.scala-lang.org",
    "http://akka.io",
    "https://www.haskell.org")
}
