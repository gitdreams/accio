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

import com.github.nscala_time.time.Imports._
import fr.cnrs.liris.accio.sdk.{RemoteFile, _}
import fr.cnrs.liris.locapriv.domain.{Event, Trace}

@Op(
  category = "transform",
  help = "Enforce a minimum duration between two consecutive events in traces.",
  description = "If the duration is less than a given threshold, events will be discarded until " +
    "the next point that fulfills the minimum duration requirement.",
  cpus = 4,
  ram = "2G")
case class TemporalSamplingOp(
  @Arg(help = "Minimum duration between two consecutive events") duration: org.joda.time.Duration,
  @Arg(help = "Input dataset") data: RemoteFile)
  extends ScalaOperator[TemporalSamplingOut] with SlidingSampling with SparkleOperator {

  override def execute(ctx: OpContext): TemporalSamplingOut = {
    val sample = (prev: Event, curr: Event) => (prev.time to curr.time).duration >= duration
    val output = read[Trace](data).map(transform(_, sample))
    TemporalSamplingOut(write(output, ctx))
  }
}

case class TemporalSamplingOut(
  @Arg(help = "Output dataset") data: RemoteFile)