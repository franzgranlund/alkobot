package controllers

import java.net.{UnknownHostException, InetAddress}

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

  def ip = LogAction { implicit request =>
    val ipAddress = request.remoteAddress

    try {
      val hostName = InetAddress.getByName(ipAddress).getHostName
      Ok(ipAddress + " -> " + hostName)
    } catch {
      case ue : UnknownHostException => Ok(ipAddress + " -> could not resolve.")
    }
  }

  val urlList = List(
    "http://www.playframework.com",
    "http://www.scala-lang.org",
    "http://akka.io",
    "https://www.haskell.org")
}
