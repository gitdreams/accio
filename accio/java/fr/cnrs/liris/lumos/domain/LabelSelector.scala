/*
 * Accio is a platform to launch computer science experiments.
 * Copyright (C) 2016-2018 Vincent Primault <v.primault@ucl.ac.uk>
 *
 * Accio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Accio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Accio.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.cnrs.liris.lumos.domain

case class LabelSelector(requirements: Seq[LabelSelector.Req]) {
  def matches(labels: Map[String, String]): Boolean = requirements.forall(_.matches(labels))

  def +(other: LabelSelector): LabelSelector = LabelSelector(requirements ++ other.requirements)
}

object LabelSelector {

  case class Req(key: String, op: Op, values: Set[String] = Set.empty) {
    op match {
      case In => require(values.nonEmpty, "'In' operator requires at least one value")
      case NotIn => require(values.nonEmpty, "'NotIn' operator requires at least one value")
      case _ => // Nothing to check.
    }

    def matches(labels: Map[String, String]): Boolean = op.matches(labels.get(key), values)
  }

  sealed trait Op {
    def matches(value: Option[String], values: Set[String]): Boolean
  }

  case object In extends Op {
    override def matches(value: Option[String], values: Set[String]): Boolean = {
      value.exists(values.contains)
    }
  }

  case object NotIn extends Op {
    override def matches(value: Option[String], values: Set[String]): Boolean = {
      value.forall(v => !values.contains(v))
    }
  }

  case object Present extends Op {
    override def matches(value: Option[String], values: Set[String]): Boolean = value.isDefined
  }

  case object Absent extends Op {
    override def matches(value: Option[String], values: Set[String]): Boolean = value.isEmpty
  }

  def apply(first: Req, rest: Req*): LabelSelector = LabelSelector(first +: rest)

  def present(key: String): LabelSelector = LabelSelector(Req(key, Present))

  def absent(key: String): LabelSelector = LabelSelector(Req(key, Absent))

  def in(key: String, values: Set[String]): LabelSelector = LabelSelector(Req(key, In, values))

  def notIn(key: String, values: Set[String]): LabelSelector = LabelSelector(Req(key, NotIn, values))

  def equal(key: String, value: String): LabelSelector = LabelSelector(Req(key, In, Set(value)))

  def notEqual(key: String, value: String): LabelSelector = LabelSelector(Req(key, NotIn, Set(value)))

  def parse(str: String): Either[String, LabelSelector] = LabelSelectorParser.parse(str)
}