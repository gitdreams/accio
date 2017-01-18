/*
 * Accio is a program whose purpose is to study location privacy.
 * Copyright (C) 2016-2017 Vincent Primault <vincent.primault@liris.cnrs.fr>
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

package fr.cnrs.liris.privamov.ops

import fr.cnrs.liris.privamov.core.model.{Event, Trace}

import scala.collection.mutable

/**
 * Base trait for transformers using previous event and current one to take a decision about
 * whether to split a trace into multiple parts.
 */
private[ops] trait SlidingSplitting {
  protected def transform(input: Trace, split: (Seq[Event], Event) => Boolean): Seq[Trace] = {
    val output = mutable.ListBuffer.empty[Trace]
    val buffer = mutable.ListBuffer.empty[Event]
    var idx = 0
    for (event <- input.events) {
      if (buffer.nonEmpty && split(buffer, event)) {
        output += Trace(s"${input.id}-$idx", buffer.toList)
        buffer.clear()
        idx += 1
      }
      buffer += event
    }
    if (buffer.nonEmpty) {
      output += Trace(s"${input.id}-$idx", buffer.toList)
    }
    output.toList
  }
}