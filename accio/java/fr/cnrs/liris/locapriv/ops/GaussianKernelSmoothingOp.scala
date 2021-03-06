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
import fr.cnrs.liris.util.geo.Point
import fr.cnrs.liris.locapriv.domain.{Event, Trace}

@Op(
  category = "transform",
  help = "Apply gaussian kernel smoothing on traces.",
  description = "Apply gaussian kernel smoothing on a trace, attenuating the impact of noisy observations.",
  cpus = 4,
  ram = "2G")
case class GaussianKernelSmoothingOp(
  @Arg(help = "Bandwidth")
  omega: Duration,
  @Arg(help = "Input dataset")
  data: RemoteFile)
  extends ScalaOperator[GaussianKernelSmoothingOut] with SparkleOperator {

  override def execute(ctx: OpContext): GaussianKernelSmoothingOut = {
    val output = write(read[Trace](data).map(transform), ctx)
    GaussianKernelSmoothingOut(output)
  }

  private def transform(trace: Trace): Trace = trace.replace(_.map(transform(_, trace)))

  private def transform(event: Event, trace: Trace) = {
    var ks = 0d
    var x = 0d
    var y = 0d
    for (i <- trace.events.indices) {
      val k = gaussianKernel(event.time.millis, trace.events(i).time.millis, omega)
      ks += k
      x += k * trace.events(i).point.x
      y += k * trace.events(i).point.y
    }
    x /= ks
    y /= ks
    event.copy(point = Point(x, y))
  }

  private def gaussianKernel(t1: Long, t2: Long, omega: Duration): Double =
    Math.exp(-Math.pow(t1 - t2, 2) / (2 * omega.millis * omega.millis))
}

case class GaussianKernelSmoothingOut(@Arg(help = "Output dataset") data: RemoteFile)