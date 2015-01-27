package controllers

import java.net.{UnknownHostException, InetAddress}

import org.xbill.DNS.Address
import play.api.mvc._
import utils.LogAction

import scala.util.{Failure, Success, Try, Random}

object Webpage extends Controller {

  def index = LogAction { implicit request =>
    val happyUrl = urlList(Random.nextInt(urlList.size))

    Ok(views.html.index(happyUrl)).withHeaders(
      VARY -> "Accept-Encoding"
    )
  }

  def ip = LogAction { implicit request =>
    resolveIp(request.remoteAddress) match {
      case Success(host) => Ok(request.remoteAddress + " -> " + host)
      case Failure(ex) => Ok(request.remoteAddress + " -> could not resolve.")
    }
  }

  def resolveIp(ip: String): Try[String] = Try { Address.getHostName(Address.getByAddress(ip)) }

  val urlList = List(
    "http://www.playframework.com",
    "http://www.scala-lang.org",
    "http://akka.io",
    "https://www.haskell.org")
}
