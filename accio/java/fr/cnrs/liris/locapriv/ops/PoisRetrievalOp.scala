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

import fr.cnrs.liris.accio.sdk._
import fr.cnrs.liris.locapriv.domain.Poi
import fr.cnrs.liris.util.geo.Distance
import org.joda.time.Duration

@Op(
  category = "metric",
  help = "Compute POIs retrieval difference between two POIs datasets",
  cpus = 2,
  ram = "1G")
case class PoisRetrievalOp(
  @Arg(help = "Maximum distance between two POIs to consider they match")
  threshold: Distance,
  @Arg(help = "Minimum overlap between two POIs to consider they match")
  overlap: Option[Duration],
  @Arg(help = "Train dataset (POIs)")
  train: RemoteFile,
  @Arg(help = "Test dataset (POIs)")
  test: RemoteFile)
  extends ScalaOperator[PoisRetrievalOp.Out] with SparkleOperator {

  override def execute(ctx: OpContext): PoisRetrievalOp.Out = {
    val trainDs = read[Poi](train)
    val testDs = read[Poi](test)
    val metrics = trainDs.zipPartitions(testDs)(evaluate)
    PoisRetrievalOp.Out(write(metrics, 0, ctx))
  }

  private def evaluate(ref: Seq[Poi], res: Seq[Poi]): Seq[MetricUtils.FscoreValue] = {
    val matched = res.flatMap(resPoi => remap(resPoi, ref, threshold, overlap)).distinct.size
    Seq(MetricUtils.fscore(ref.head.id, ref.size, res.size, matched))
  }

  private def remap(resPoi: Poi, refPois: Seq[Poi], threshold: Distance, overlap: Option[Duration]): Seq[Int] = {
    val matchingPois = refPois.zipWithIndex
      .filter { case (refPoi, _) => matches(refPoi, resPoi, threshold, overlap) }
      .map { case (refPoi, idx) => (idx, refPoi.centroid.distance(resPoi.centroid)) }
    if (matchingPois.nonEmpty) Seq(matchingPois.minBy(_._2)._1) else Seq.empty
  }

  private def matches(refPoi: Poi, resPoi: Poi, threshold: Distance, overlap: Option[Duration]) = {
    if (refPoi.centroid.distance(resPoi.centroid) > threshold) {
      false
    } else {
      overlap match {
        case None => true
        case Some(minDuration) =>
          if (refPoi.lastSeen.isAfter(resPoi.firstSeen)) {
            (refPoi.lastSeen.getMillis - resPoi.firstSeen.getMillis) >= minDuration.getMillis
          } else if (resPoi.lastSeen.isAfter(refPoi.firstSeen)) {
            (resPoi.lastSeen.getMillis - refPoi.firstSeen.getMillis) >= minDuration.getMillis
          } else {
            false
          }
      }
    }
  }
}

object PoisRetrievalOp {

  case class Out(
    @Arg(help = "Metrics dataset")
    metrics: RemoteFile)

}