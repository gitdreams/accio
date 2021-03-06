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

package fr.cnrs.liris.accio.storage.memory

import com.twitter.finagle.stats.{NullStatsReceiver, StatsReceiver}
import fr.cnrs.liris.accio.storage._

/**
 * In-memory storage. It is intended for development and testing purposes, as there is not
 * persistent storage.
 *
 * @param statsReceiver Stats receiver.
 */
private[storage] final class MemoryStorage(statsReceiver: StatsReceiver) extends Storage {
  override val jobs: JobStore = new MemoryJobStore(statsReceiver)
}

object MemoryStorage {
  /**
   * Creates a new empty in-memory storage for use in testing.
   */
  def empty: Storage = new MemoryStorage(NullStatsReceiver)
}