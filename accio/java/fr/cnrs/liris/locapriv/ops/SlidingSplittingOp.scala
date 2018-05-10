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

package fr.cnrs.liris.locapriv.ops

import fr.cnrs.liris.locapriv.domain.Event

import scala.collection.mutable

/**
 * Base trait for transformers using previous event and current one to take a decision about
 * whether to split a trace into multiple parts.
 */
private[ops] trait SlidingSplittingOp extends TransformOp[Event] {
  protected def split(buffer: Seq[Event], curr: Event): Boolean

  override protected def transform(key: String, trace: Seq[Event]): Seq[Event] = {
    val result = mutable.ListBuffer.empty[Event]
    var idx = 0
    for (event <- trace) {
      if (result.nonEmpty && split(result, event)) {
        idx += 1
      }
      result += event.copy(id = s"${event.id}-$idx")
    }
    result.toList
  }

  override protected def partitioner: Option[Event => Any] = Some((e: Event) => e.id)
}