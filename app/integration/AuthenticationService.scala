package integration

import java.security.spec.{ECPoint, ECPrivateKeySpec, ECPublicKeySpec}
import java.security.{KeyFactory, PrivateKey, PublicKey}
import javax.inject.{Inject, Singleton}

import cats.data._
import cats.implicits._
import domain.{NotFoundError, User}
import domain.UserData.AuthenticationData
import infrastructure.UserRepository
import integration.base._
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.ECNamedCurveSpec
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class AuthenticationService @Inject()(
  users: UserRepository,
  messages: MessagesApi,
  configuration: Configuration,
  executionContext: ExecutionContext
) extends BaseService(messages, executionContext) {

  private final val Expiration = configuration.get[Int]("jwt.expiration")
  private final val SParam = BigInt(configuration.get[String]("jwt.params.S"), 16)
  private final val XParam = BigInt(configuration.get[String]("jwt.params.X"), 16)
  private final val YParam = BigInt(configuration.get[String]("jwt.params.Y"), 16)

  private final val (privateKeyEC, publicKeyEC) = initialiseKeys

  def verifyToken(token: String): Future[Option[User]] =
    validateToken(token) map { _ =>
      users.findByToken(token) map (_.toOption)
    } getOrElse Future.successful(None)

  def authenticate(data: AuthenticationData): Future[JsResponse] = {
    val result = for {
      found <- EitherT(users.findByCredentials(data.username, data.password))
      updated = found.copy(token = Some(generateToken(found)))
      saved <- EitherT(users.update(updated))
    } yield saved

    result.value map {
      case Right(user) =>
        JsSuccessful(JsObject(Seq(
          "token" -> JsString(user.token.orNull),
          "user" -> Json.toJson(user)
        )))
      case Left(NotFoundError) =>
        JsUnauthorized(JsString(messages("json.error.unauthorized")))
      case Left(error) =>
        toJsError(error)
    }
  }

  private def generateToken(user: User): String = {
    val userString = Json.toJson(user).toString
    val claim = JwtClaim(userString).issuedNow.expiresIn(Expiration)
    Jwt.encode(claim, privateKeyEC, JwtAlgorithm.ES512)
  }

  private def validateToken(token: String): Try[String] =
    Jwt.decode(token, publicKeyEC, Seq(JwtAlgorithm.ES512))

  private def initialiseKeys: (PrivateKey, PublicKey) = {
    val curveParams = ECNamedCurveTable.getParameterSpec("P-521")
    val curveSpec = new ECNamedCurveSpec("P-521", curveParams.getCurve, curveParams.getG, curveParams.getN, curveParams.getH)
    val privateSpec = new ECPrivateKeySpec(SParam.underlying, curveSpec)
    val publicSpec = new ECPublicKeySpec(new ECPoint(XParam.underlying, YParam.underlying), curveSpec)
    val provider = new BouncyCastleProvider

    val privateKeyEC = KeyFactory.getInstance("ECDSA", provider).generatePrivate(privateSpec)
    val publicKeyEC = KeyFactory.getInstance("ECDSA", provider).generatePublic(publicSpec)

    (privateKeyEC, publicKeyEC)
  }

}
