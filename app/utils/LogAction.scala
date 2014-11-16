package utils

import play.api.mvc._
import play.api.Logger

import scala.concurrent.Future

object LogAction extends ActionBuilder[Request] {
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    val remoteAddress = request.remoteAddress
    val path = request.path
    val method = request.method

    Logger.info(s"$remoteAddress - $method $path")
    block(request)
  }
}
