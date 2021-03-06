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

package fr.cnrs.liris.accio.tools.cli

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.helpers.NOPAppender
import com.twitter.util.logging.Slf4jBridgeUtility
import fr.cnrs.liris.accio.tools.cli.commands.{CommandDispatcher, CommandRegistry}
import fr.cnrs.liris.accio.tools.cli.terminal.OutErr
import org.slf4j.{Logger, LoggerFactory}

object AccioClientMain extends AccioClient

/**
 * Entry point of the Accio command line application.
 */
class AccioClient {
  def main(args: Array[String]): Unit = {
    // Prevent from displaying any logs.
    val ctx = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
    ctx.reset()
    val rootLogger = ctx.getLogger(Logger.ROOT_LOGGER_NAME)
    val noopAppender = new NOPAppender[ILoggingEvent]
    noopAppender.setContext(ctx)
    noopAppender.start()
    rootLogger.addAppender(noopAppender)

    Slf4jBridgeUtility.attemptSlf4jBridgeHandlerInstallation()

    val dispatcher = new CommandDispatcher(CommandRegistry.default)
    val exitCode = dispatcher.exec(args, OutErr.System)
    sys.exit(exitCode.code)
  }
}