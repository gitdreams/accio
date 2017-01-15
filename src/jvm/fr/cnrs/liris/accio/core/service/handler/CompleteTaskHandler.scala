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

package fr.cnrs.liris.accio.core.service.handler

import com.google.inject.Inject
import com.twitter.util.Future
import com.typesafe.scalalogging.LazyLogging
import fr.cnrs.liris.accio.core.service.{SchedulerService, StateManager}
import fr.cnrs.liris.accio.core.domain._

final class CompleteTaskHandler @Inject()(
  runRepository: RunRepository,
  workflowRepository: WorkflowRepository,
  stateManager: StateManager,
  scheduler: SchedulerService,
  graphFactory: GraphFactory)
  extends Handler[CompleteTaskRequest, CompleteTaskResponse] with LazyLogging {

  override def handle(req: CompleteTaskRequest): Future[CompleteTaskResponse] = {
    val taskLock = stateManager.lock(s"task/${req.taskId.value}")
    taskLock.lock()
    try {
      stateManager.get(req.taskId) match {
        case None =>
          logger.warn(s"Received unknown task ${req.taskId.value}")
        case Some(task) =>
          stateManager.remove(task.id)
          val runLock = stateManager.lock(s"run/${task.runId.value}")
          runLock.lock()
          try {
            runRepository.get(task.runId) match {
              case None => logger.warn(s"Received task ${req.taskId.value} associated with unknown run ${task.runId.value}")
              case Some(run) =>
                updateRun(run, task.nodeName, req.result)
                if (req.result.exitCode == 0) {
                  // Task completed successfully, schedule next nodes.
                  val graph = getGraph(run)
                  graph(task.nodeName).successors.foreach { nextNodeName =>
                    scheduler.submit(run, graph(nextNodeName))
                  }
                }
            }
          } finally {
            runLock.unlock()
          }
      }
      Future(CompleteTaskResponse())
    } finally {
      taskLock.unlock()
    }
  }

  private def getGraph(run: Run) = {
    // Workflow does exist, because it has been validate when creating the runs.
    val workflow = workflowRepository.get(run.pkg.workflowId, run.pkg.workflowVersion).get
    graphFactory.create(workflow.graph)
  }

  private def updateRun(run: Run, nodeName: String, result: OpResult) = {
    val now = System.currentTimeMillis()
    val nodeState = run.state.nodes.find(_.nodeName == nodeName).get
    if (result.exitCode == 0) {
      // Task completed successfully.
      val newNodeState = nodeState.copy(completedAt = Some(now), result = Some(result), status = NodeStatus.Success)
      var newRunState = run.state.copy(nodes = run.state.nodes - nodeState + newNodeState)

      newRunState = updateRunState(newRunState, now)
      runRepository.save(run.copy(state = newRunState))
    } else {
      // Task completed with an error.
      val newNodeState = nodeState.copy(completedAt = Some(now), status = NodeStatus.Failed)
      var newRunState = run.state.copy(nodes = run.state.nodes - nodeState + newNodeState)

      // Mark dependent tasks as cancelled.
      val graph = getGraph(run)
      graph(nodeName).successors.foreach { nextNodeName =>
        val nodeState = run.state.nodes.find(_.nodeName == nextNodeName).get
        val newNodeState = nodeState.copy(completedAt = Some(now), status = NodeStatus.Cancelled)
        run.state.copy(nodes = run.state.nodes - nodeState + newNodeState)
      }

      newRunState = updateRunState(newRunState, now)
      runRepository.save(run.copy(state = newRunState))
    }
    //TODO: update parent run if any.
  }

  private def updateRunState(runState: RunState, now: Long) = {
    // Run could already be marked as completed if it was killed. In this case we do not want to update its state.
    // Otherwise, we check if this node was the last one to complete the run.
    if (runState.completedAt.isEmpty) {
      // If all nodes are completed (not necessarily successfully), mark the run as completed. It is successfully
      // completed if all nodes completed successfully.
      if (runState.nodes.forall(s => Utils.isCompleted(s.status))) {
        val newRunStatus = if (runState.nodes.forall(_.status == NodeStatus.Success)) {
          RunStatus.Success
        } else {
          RunStatus.Failed
        }
        runState.copy(progress = 1, status = newRunStatus, completedAt = Some(now))
      } else {
        // Run is not yet completed, only, update progress.
        val progress = runState.nodes.count(s => Utils.isCompleted(s.status)).toDouble / runState.nodes.size
        runState.copy(progress = progress)
      }
    } else {
      runState
    }
  }
}