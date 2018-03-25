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

package fr.cnrs.liris.accio.agent.handler

import com.google.inject.Inject
import com.twitter.util.Future
import com.typesafe.scalalogging.LazyLogging
import fr.cnrs.liris.accio.agent.{StreamTaskLogsRequest, StreamTaskLogsResponse}
import fr.cnrs.liris.accio.api.thrift.{InvalidTaskException, InvalidWorkerException}
import fr.cnrs.liris.accio.scheduler.standalone.ClusterState
import fr.cnrs.liris.accio.storage.Storage
import fr.cnrs.liris.accio.runtime.commandbus.AbstractHandler

/**
 * Receive run logs from a task.
 *
 * @param storage Storage.
 * @param state   Cluster state.
 */
class StreamTaskLogsHandler @Inject()(storage: Storage, state: ClusterState)
  extends AbstractHandler[StreamTaskLogsRequest, StreamTaskLogsResponse] with LazyLogging {

  @throws[InvalidTaskException]
  @throws[InvalidWorkerException]
  override def handle(req: StreamTaskLogsRequest): Future[StreamTaskLogsResponse] = {
    state.ensure(req.workerId, req.taskId)
    storage.logs.save(req.logs)
    Future(StreamTaskLogsResponse())
  }
}