package domain

sealed trait DBError

final case class UniqueViolationError(field: String) extends DBError
case object NotFoundError extends DBError
case object UnhandledDBError extends DBError
case object ConnectionError extends DBError
