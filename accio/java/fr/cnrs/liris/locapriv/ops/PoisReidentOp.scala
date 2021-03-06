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
import fr.cnrs.liris.locapriv.domain.PoiSet

/**
 * Implementation of a re-identification attack using the points of interest as a discriminating information. The POIs
 * are used to model the behaviour of training users, and then extracted from the tracks of test users and compared to
 * those from the training users. The comparison here is done between set of POIs, and only the spatial information.
 *
 * Vincent Primault, Sonia Ben Mokhtar, Cédric Lauradoux and Lionel Brunie. Differentially Private
 * Location Privacy in Practice. In MOST'14.
 */
@Op(
  category = "metric",
  help = "Re-identification attack using POIs a the discriminating information.",
  cpus = 6,
  ram = "3G")
case class PoisReidentOp(
  @Arg(help = "Train dataset (POIs)")
  train: RemoteFile,
  @Arg(help = "Test dataset (POIs)")
  test: RemoteFile)
  extends ScalaOperator[ReidentificationOut] with SparkleOperator {

  override def execute(ctx: OpContext): ReidentificationOut = {
    val trainPois = read[PoiSet](train).toArray
    val testPois = read[PoiSet](test).toArray

    val distances = computeDistances(trainPois, testPois)
    val matches = computeMatches(distances)
    val successRate = computeSuccessRate(trainPois, matches)

    //distances.map { case (k, v) => k -> v.toMap },
    ReidentificationOut(matches, successRate)
  }

  private def computeDistances(trainPois: Array[PoiSet], testPois: Array[PoiSet]) = {
    val costs = collection.mutable.Map[String, Map[String, Double]]()
    testPois.foreach { pois =>
      val distances = if (pois.nonEmpty) {
        //Compute the distance between the set of POIs from the test user and the models (from the training users).
        //This will give us an association between a training user and a distance. We only keep finite distances.
        trainPois
          .map(model => model.user -> model.distance(pois).meters)
          .filter { case (_, d) => !d.isInfinite }
          .toMap
      } else {
        Map[String, Double]()
      }
      costs.synchronized {
        costs += pois.user -> distances
      }
    }
    costs.toSeq.map { case (user, model) =>
      user -> model.toSeq.sortBy(_._2).map { case (u: String, d: Double) => (u, d) }
    }.toMap
  }

  private def computeMatches(distances: Map[String, Seq[(String, Double)]]) = {
    distances.map { case (testUser, res) => testUser -> res.headOption.map(_._1).getOrElse("-") }
  }

  private def computeSuccessRate(trainPois: Array[PoiSet], matches: Map[String, String]) = {
    val trainUsers = trainPois.map(_.user)
    matches.map { case (testUser, trainUser) => if (testUser == trainUser) 1 else 0 }.sum / trainUsers.length.toDouble
  }
}

case class ReidentificationOut(
  //@Arg(help = "Distances between users from test and train datasets")
  //distances: Map[String, Map[String, Double]],
  @Arg(help = "Matches between users from test and train datasets")
  matches: Map[String, String],
  @Arg(help = "Correct re-identifications rate")
  rate: Double)