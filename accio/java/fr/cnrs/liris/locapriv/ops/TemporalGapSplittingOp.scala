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
  help = "Split traces, when there is a too long duration between consecutive events.",
  cpus = 4,
  ram = "2G")
case class TemporalGapSplittingOp(
  @Arg(help = "Maximum duration between two consecutive events") duration: org.joda.time.Duration,
  @Arg(help = "Input dataset") data: RemoteFile)
  extends ScalaOperator[TemporalGapSplittingOut] with SlidingSplitting with SparkleOperator {

  override def execute(ctx: OpContext): TemporalGapSplittingOut = {
    val split = (buffer: Seq[Event], curr: Event) => (buffer.last.time to curr.time).duration >= duration
    val output = read[Trace](data).flatMap(transform(_, split))
    TemporalGapSplittingOut(write(output, ctx))
  }
}

case class TemporalGapSplittingOut(
  @Arg(help = "Output dataset") data: RemoteFile)
