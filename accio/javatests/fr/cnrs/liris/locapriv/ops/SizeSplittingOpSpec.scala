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

import fr.cnrs.liris.locapriv.domain.Trace
import fr.cnrs.liris.locapriv.testing.WithTraceGenerator
import fr.cnrs.liris.testing.UnitSpec

/**
 * Unit tests for [[SizeSplittingOp]].
 */
class SizeSplittingOpSpec extends UnitSpec with WithTraceGenerator with ScalaOperatorSpec {
  behavior of "SizeSplittingOp"

  it should "split by size" in {
    val trace = randomTrace(Me, 150)
    val res = transform(Seq(trace), 20)
    res should have size 8
    res.foreach { trace =>
      trace.user shouldBe Me
      trace.size should be <= 20
    }
    res.flatMap(_.events) should contain theSameElementsInOrderAs trace.events
  }

  it should "handle a size greater than trace's size" in {
    val trace = randomTrace(Me, 60)
    val res = transform(Seq(trace), 65)
    res should have size 1
    res.head.user shouldBe trace.user
    res.head.events should contain theSameElementsInOrderAs trace.events
  }

  private def transform(data: Seq[Trace], size: Int) = {
    val ds = writeTraces(data: _*)
    val res = SizeSplittingOp(size = size, data = ds).execute(ctx)
    readTraces(res.data)
  }
}