package integration.base

import play.api.libs.json.{JsString, JsValue}

sealed trait JsResponse {
  def json: JsValue
}

final case class JsSuccessful(json: JsValue) extends JsResponse

final case class JsInvalid(json: JsValue) extends JsResponse

final case class JsUnknown(json: JsValue) extends JsResponse

final case class JsNotFound(json: JsValue) extends JsResponse

final case class JsForbidden(json: JsValue = JsString("")) extends JsResponse

final case class JsUnauthorized(json: JsValue = JsString("")) extends JsResponse
