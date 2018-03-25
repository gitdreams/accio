/*
 * Accio is a program whose purpose is to study location privacy.
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

package fr.cnrs.liris.accio.api

import scala.collection.mutable

/**
 * Factory for [[Error]].
 */
object Errors {
  /**
   * Create a new error from a throwable.
   *
   * @param e Throwable.
   */
  def create(e: Throwable): thrift.Error = {
    val causes = mutable.ListBuffer.empty[thrift.ErrorData]
    var maybeException = Option(e.getCause)
    while (maybeException.isDefined) {
      causes += createData(maybeException.get)
      maybeException = Option(maybeException.get.getCause)
    }
    thrift.Error(createData(e), causes)
  }

  private def createData(e: Throwable) = {
    thrift.ErrorData(
      classifier = e.getClass.getName,
      message = Some(e.getMessage),
      stacktrace = e.getStackTrace.map(_.toString))
  }
}