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

package fr.cnrs.liris.locapriv.io

import fr.cnrs.liris.locapriv.domain._

import scala.reflect._

class TraceCodec extends TraceDecoder with Codec[Trace] {
  private[this] val eventCodec = new CsvEventCodec

  override def elementClassTag: ClassTag[Trace] = classTag[Trace]

  override def encode(key: String, elements: Seq[Trace]): Array[Byte] = {
    eventCodec.encode(key, elements.flatMap(_.events))
  }
}

class TraceDecoder extends Decoder[Trace] {
  private[this] val eventDecoder = new CsvEventCodec

  override def elementClassTag: ClassTag[Trace] = classTag[Trace]

  override def decode(key: String, bytes: Array[Byte]): Seq[Trace] = {
    val events = eventDecoder.decode(key, bytes).sortBy(_.time.getMillis)
    Seq(Trace(key, events))
  }
}