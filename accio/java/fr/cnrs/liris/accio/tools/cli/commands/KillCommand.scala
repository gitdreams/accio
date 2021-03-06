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

package fr.cnrs.liris.accio.tools.cli.commands

import com.twitter.util.Future
import fr.cnrs.liris.accio.agent.KillJobRequest
import fr.cnrs.liris.accio.tools.cli.event.Event

final class KillCommand extends Command with ClientCommand {
  override def name = "kill"

  override def help = "Cancel active jobs."

  override def allowResidue = true

  override def execute(residue: Seq[String], env: CommandEnvironment): Future[ExitCode] = {
    if (residue.isEmpty) {
      env.reporter.handle(Event.error("You must provide at least one job name."))
      return Future.value(ExitCode.CommandLineError)
    }
    val fs = residue.map { name =>
      client
        .killJob(KillJobRequest(name))
        .map { _ =>
          env.reporter.handle(Event.info(s"Killed job $name"))
          ExitCode.Success
        }
    }
    Future.collect(fs).map(ExitCode.select)
  }
}