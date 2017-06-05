package domain

import java.time.LocalDateTime

trait Entity {
  def id: Option[Long]
}

trait Timestamps {
  def createdAt: LocalDateTime
  def modified: LocalDateTime
}
