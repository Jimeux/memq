package domain.base

trait DBErrorHandler {
  def toDBError(e: Exception): DBError
}
