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
import fr.cnrs.liris.accio.sdk._
import fr.cnrs.liris.locapriv.domain._
import fr.cnrs.liris.util.geo.Distance

@Op(
  category = "transform",
  help = "Compute POIs retrieval difference between two datasets of traces",
  cpus = 4,
  ram = "3G")
case class PoisExtractionOp(
  @Arg(help = "Clustering maximum diameter")
  diameter: Distance,
  @Arg(help = "Clustering minimum duration")
  duration: Duration,
  @Arg(help = "Minimum number of times a cluster should appear to consider it")
  minPoints: Int = 0,
  @Arg(help = "Input traces dataset")
  data: RemoteFile)
  extends ScalaOperator[PoisExtractionOut] with SparkleOperator {

  override def execute(ctx: OpContext): PoisExtractionOut = {
    val input = read[Trace](data)
    val output = if (minPoints == 0) {
      val clusterer = new DTClusterer(duration, diameter)
      input.map { trace =>
        val pois = clusterer.cluster(trace).map(cluster => Poi(cluster.events))
        PoiSet(trace.id, pois)
      }
    } else {
      val clusterer = new PoisClusterer(duration, diameter, minPoints)
      input.map { trace =>
        val pois = clusterer.cluster(trace.events)
        PoiSet(trace.id, pois)
      }
    }
    PoisExtractionOut(write(output, ctx))
  }
}

case class PoisExtractionOut(
  @Arg(help = "Output POIs dataset")
  data: RemoteFile)