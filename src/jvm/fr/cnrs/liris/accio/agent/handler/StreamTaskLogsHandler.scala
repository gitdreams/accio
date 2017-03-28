/*
 * Accio is a program whose purpose is to study location privacy.
 * Copyright (C) 2016-2017 Vincent Primault <vincent.primault@liris.cnrs.fr>
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
import fr.cnrs.liris.accio.agent.commandbus.AbstractHandler
import fr.cnrs.liris.accio.agent.{StreamTaskLogsRequest, StreamTaskLogsResponse}
import fr.cnrs.liris.accio.core.domain.{InvalidTaskException, InvalidWorkerException}
import fr.cnrs.liris.accio.core.scheduler.ClusterState
import fr.cnrs.liris.accio.core.storage.Storage

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
    // TODO: It could be done in an unsafe manner, because logs are append-only.
    storage.write { provider =>
      provider.logs.save(req.logs)
    }
    Future(StreamTaskLogsResponse())
  }
}