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

package fr.cnrs.liris.locapriv.ops

import com.github.nscala_time.time.Imports._
import fr.cnrs.liris.locapriv.domain.{Event, Trace}
import fr.cnrs.liris.locapriv.testing.WithTraceGenerator
import fr.cnrs.liris.testing.UnitSpec

/**
 * Unit tests for [[TemporalSamplingOp]].
 */
class TemporalSamplingOpSpec extends UnitSpec with WithTraceGenerator with ScalaOperatorSpec {
  behavior of "TemporalSamplingOp"

  it should "downsample traces" in {
    val trace = Trace(Seq(
      Event(Me, Here, Now),
      Event(Me, Here, Now + 10.seconds),
      Event(Me, Here, Now + 19.seconds),
      Event(Me, Here, Now + 25.seconds),
      Event(Me, Here, Now + 34.seconds),
      Event(Me, Here, Now + 44.seconds)))
    val res = transform(Seq(trace), 10.seconds)
    res should have size 1
    res.head.user shouldBe trace.user
    res.head.events should contain theSameElementsInOrderAs Seq(
      Event(Me, Here, Now),
      Event(Me, Here, Now + 10.seconds),
      Event(Me, Here, Now + 25.seconds),
      Event(Me, Here, Now + 44.seconds))
  }

  it should "handle empty traces" in {
    val trace = Trace.empty(Me)
    val res = transform(Seq(trace), 10.seconds)
    res should have size 1
    res.head.user shouldBe trace.user
    res.head.events shouldBe trace.events
  }

  it should "handle singleton traces" in {
    val trace = Trace(Seq(Event(Me, Here, Now)))
    val res = transform(Seq(trace), 10.seconds)
    res should have size 1
    res.head.user shouldBe trace.user
    res.head.events should contain theSameElementsInOrderAs trace.events
  }

  private def transform(data: Seq[Trace], duration: Duration) = {
    val ds = writeTraces(data: _*)
    val res = TemporalSamplingOp(duration = duration, data = ds).execute(ctx)
    readTraces(res.data)
  }
}