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

package fr.cnrs.liris.accio.core.infra.scheduler.local

import java.nio.file.Path

import com.google.inject.{Provides, Singleton}
import fr.cnrs.liris.accio.core.domain.MutableRunRepository
import fr.cnrs.liris.accio.core.service.{Downloader, Scheduler}
import net.codingwell.scalaguice.ScalaModule

/**
 * Local scheduler configuration.
 *
 * @param workDir      Working directory.
 * @param agentAddr    Agent address.
 * @param executorUri  URI where to fetch the executor.
 * @param javaHome     Java home to be used when running nodes.
 * @param executorArgs Arguments to pass to the executors.
 */
case class LocalSchedulerConfig(
  workDir: Path,
  agentAddr: String,
  executorUri: String,
  javaHome: Option[String],
  executorArgs: Seq[String])

/**
 * Guice module provisioning a local scheduler.
 *
 * @param config Configuration.
 */
class LocalSchedulerModule(config: LocalSchedulerConfig) extends ScalaModule {
  override protected def configure(): Unit = {}

  @Singleton
  @Provides
  def providesScheduler(downloader: Downloader, runRepository: MutableRunRepository): Scheduler = {
    val executorArgs = Seq("-addr", config.agentAddr) ++ config.executorArgs
    new LocalScheduler(downloader, config.workDir, config.executorUri, config.javaHome, executorArgs, runRepository)
  }
}