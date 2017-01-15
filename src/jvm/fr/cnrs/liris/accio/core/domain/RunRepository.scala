/*
 * Accio is a program whose purpose is to study location privacy.
 * Copyright (C) 2016 Vincent Primault <vincent.primault@liris.cnrs.fr>
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

package fr.cnrs.liris.accio.core.domain

import com.twitter.util.Time

/**
 * Repository persisting runtime data collected as runs are executed.
 */
trait RunRepository {
  /**
   * Search for runs matching a given query. Runs are returned ordered in inverse chronological order, the most
   * recent matching run being the first result.
   *
   * @param query Query.
   * @return List of runs and total number of results.
   */
  def find(query: RunQuery): RunList

  /**
   * Search for logs matching a given query. Logs are returned ordered in chronological order, the oldest matching
   * log being the first result (yes, this in *not* the same order than previous method).
   *
   * @param query Query.
   * @return List of logs.
   */
  def find(query: LogsQuery): Seq[RunLog]

  /**
   * Save a run. It will either create a new run or replace an existing one with the same identifier.
   *
   * @param run Run to save.
   */
  def save(run: Run): Unit

  /**
   * Save some logs. Since they are small objects, they can be saved in a batch (details are implementation-dependant).
   * Logs are append-only.
   *
   * @param logs Logs to save.
   */
  def save(logs: Seq[RunLog]): Unit

  /**
   * Retrieve a specific run, if it exists.
   *
   * @param id Run identifier.
   */
  def get(id: RunId): Option[Run]

  /**
   * Check whether a specific run exists.
   *
   * @param id Run identifier.
   * @return True if the run exists, false otherwise.
   */
  def contains(id: RunId): Boolean

  /**
   * Delete a run, if it exists. It will also delete all associated log lines.
   *
   * @param id Run identifier.
   */
  def remove(id: RunId): Unit
}

/**
 * Query to search for runs. Please note that you have to specify a maximum number of results.
 *
 * @param workflow    Only include runs being instances of a given workflow.
 * @param cluster     Only include runs executed on a given cluster.
 * @param owner       Only include runs initiated by a given user.
 * @param environment Only include runs executed inside a given environment.
 * @param name        Only include runs whose name matches a given string. Exact interpretation can be implementation-dependant.
 * @param status      Only include runs with a given status.
 * @param limit       Maximum number of matching runs to return. Must be in [1,100].
 * @param offset      Number of matching runs to skip.
 */
case class RunQuery(
  workflow: Option[WorkflowId] = None,
  cluster: Option[String] = None,
  owner: Option[String] = None,
  environment: Option[String] = None,
  name: Option[String] = None,
  status: Option[RunStatus] = None,
  limit: Int = 25,
  offset: Option[Int] = None) {
  require(limit > 0 && limit <= 100, s"Maximum number of runs must be in [1,100] (got $limit)")
}

/**
 * List of runs and total number of results.
 *
 * @param results    List of runs.
 * @param totalCount Total number of results.
 */
case class RunList(results: Seq[Run], totalCount: Int)

/**
 * Query to search for logs. It is only possible to search for logs issued by a specific task (i.e., a run/node
 * combination). Please note that you have to specify a maximum number of results.
 *
 * @param runId      Run for which to retrieve the logs.
 * @param nodeName   Node for which to retrieve the logs.
 * @param classifier Only include logs with this classifier.
 * @param limit      Maximum number of matching logs to return. Must be in [1,1000]
 * @param since      Only include logs older than a given instant.
 */
case class LogsQuery(
  runId: RunId,
  nodeName: String,
  classifier: Option[String] = None,
  limit: Int = 100,
  since: Option[Time] = None) {
  require(limit > 0 && limit <= 1000, s"Maximum number of logs must be in [1,1000] (got $limit)")
}