package domain.base

trait DBErrorHandler {

  /**
    * Return a semantically equivalent domain error for a given
    * exception that was encountered during a database operation.
    *
    * @param e - An exception thrown during a database operation
    * @return A DBError subclass that corresponds to the exception
    */
  def convert(e: Exception): DBError

}
