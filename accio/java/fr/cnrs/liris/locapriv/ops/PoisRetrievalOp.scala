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

import fr.cnrs.liris.accio.sdk.{RemoteFile, _}
import fr.cnrs.liris.locapriv.domain.{Poi, PoiSet}
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
  extends ScalaOperator[PoisRetrievalOut] with SparkleOperator {
  override def execute(ctx: OpContext): PoisRetrievalOut = {
    val trainDs = read[PoiSet](train)
    val testDs = read[PoiSet](test)
    val metrics = trainDs.zip(testDs).map { case (ref, res) => evaluate(ref, res) }.toArray
    PoisRetrievalOut(
      precision = metrics.map { case (k, v) => k -> v._1 }.toMap,
      recall = metrics.map { case (k, v) => k -> v._2 }.toMap,
      fscore = metrics.map { case (k, v) => k -> v._3 }.toMap)
  }

  private def evaluate(ref: PoiSet, res: PoiSet) = {
    require(ref.id == res.id, s"Trace mismatch: ${ref.id} / ${res.id}")
    val matched = res.pois.flatMap(resPoi => remap(resPoi, ref.pois, threshold, overlap)).distinct.size
    ref.id -> (MetricUtils.precision(res.size, matched), MetricUtils.recall(ref.size, matched), MetricUtils.fscore(ref.size, res.size, matched))
  }

  private def remap(resPoi: Poi, refPois: Seq[Poi], threshold: Distance, overlap: Option[Duration]) = {
    val matchingPois = refPois.zipWithIndex
      .filter { case (refPoi, _) => matches(refPoi, resPoi, threshold, overlap) }
      .map { case (refPoi, idx) => (idx, refPoi.centroid.distance(resPoi.centroid)) }
    if (matchingPois.nonEmpty) Some(matchingPois.minBy(_._2)._1) else None
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

case class PoisRetrievalOut(
  @Arg(help = "POIs retrieval precision")
  precision: Map[String, Double],
  @Arg(help = "POIs retrieval recall")
  recall: Map[String, Double],
  @Arg(help = "POIs retrieval F-Score")
  fscore: Map[String, Double])