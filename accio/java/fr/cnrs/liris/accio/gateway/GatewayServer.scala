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

package fr.cnrs.liris.accio.gateway

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import fr.cnrs.liris.accio.logging.LogbackConfigurator

object GatewayServerMain extends GatewayServer

class GatewayServer extends HttpServer with LogbackConfigurator {
  private[this] val uiFlag = flag("ui", false, "Whether to enable the web-based user interface")

  override protected def modules = Seq(GatewayModule)

  override protected def jacksonModule = AccioFinatraJacksonModule

  override protected def configureHttp(router: HttpRouter): Unit = {
    router
      .filter[CorsFilter](beforeRouting = true)
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add[HealthController]
      .add[ApiController]

    if (uiFlag()) {
      router.add[UiController]
    }
  }
}