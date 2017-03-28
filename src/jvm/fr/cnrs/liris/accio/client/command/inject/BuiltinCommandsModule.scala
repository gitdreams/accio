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

package fr.cnrs.liris.accio.client.command.inject

import com.google.inject.TypeLiteral
import fr.cnrs.liris.accio.client.command._
import fr.cnrs.liris.accio.runtime.cli.Command
import net.codingwell.scalaguice.{ScalaModule, ScalaMultibinder}

/**
 * Guice module provisioning bindings for default built-in commands.
 */
object BuiltinCommandsModule extends ScalaModule {
  override def configure(): Unit = {
    val commands = ScalaMultibinder.newSetBinder(binder, new TypeLiteral[Class[_ <: Command]] {})
    commands.addBinding.toInstance(classOf[ExportCommand])
    commands.addBinding.toInstance(classOf[GetCommand])
    commands.addBinding.toInstance(classOf[HelpCommand])
    commands.addBinding.toInstance(classOf[DescribeCommand])
    commands.addBinding.toInstance(classOf[KillCommand])
    commands.addBinding.toInstance(classOf[LogsCommand])
    commands.addBinding.toInstance(classOf[PushCommand])
    commands.addBinding.toInstance(classOf[DeleteCommand])
    commands.addBinding.toInstance(classOf[SubmitCommand])
    commands.addBinding.toInstance(classOf[ValidateCommand])
    commands.addBinding.toInstance(classOf[VersionCommand])
  }
}