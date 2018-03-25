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
import fr.cnrs.liris.accio.runtime.commandbus.AbstractHandler
import fr.cnrs.liris.accio.agent.{HeartbeatExecutorRequest, HeartbeatExecutorResponse}
import fr.cnrs.liris.accio.api.thrift.InvalidExecutorException

/**
 * Handle a request from an executor sending a heartbeat indicating it is still alive.
 *
 * @param state Worker state.
 */
class HeartbeatExecutorHandler @Inject()(state: WorkerState)
  extends AbstractHandler[HeartbeatExecutorRequest, HeartbeatExecutorResponse] {

  @throws[InvalidExecutorException]
  override def handle(req: HeartbeatExecutorRequest): Future[HeartbeatExecutorResponse] = {
    state.recordHeartbeat(req.executorId)
    Future.value(HeartbeatExecutorResponse())
  }
}