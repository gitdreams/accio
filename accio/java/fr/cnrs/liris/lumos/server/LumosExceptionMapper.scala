/*
 * Accio is a platform to launch computer science experiments.
 * Copyright (C) 2016-2018 Vincent Primault <v.primault@ucl.ac.uk>
 *
 * Accio is free software: you can redistribute it and/or modify
 * it under the ter ms of the GNU General Public License as published by
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

package fr.cnrs.liris.lumos.server

import com.google.inject.Singleton
import com.twitter.finatra.thrift.exceptions.ExceptionMapper
import com.twitter.util.Future
import fr.cnrs.liris.lumos.domain.LumosException

@Singleton
final class LumosExceptionMapper extends ExceptionMapper[LumosException, ServerException] {
  override def handleException(e: LumosException): Future[ServerException] =
    e match {
      case LumosException.NotFound(resourceType, resourceName) =>
        Future.exception(ServerException(
          code = ErrorCode.NotFound,
          resourceType = Some(resourceType),
          resourceName = Some(resourceName)))
      case LumosException.AlreadyExists(resourceType, resourceName) =>
        Future.exception(ServerException(
          code = ErrorCode.AlreadyExists,
          resourceType = Some(resourceType),
          resourceName = Some(resourceName)))
      case LumosException.FailedPrecondition(errors) =>
        Future.exception(ServerException(code = ErrorCode.FailedPrecondition, errors = Some(errors)))
      case LumosException.InvalidArgument(errors) =>
        Future.exception(ServerException(code = ErrorCode.InvalidArgument, errors = Some(errors)))
    }
}