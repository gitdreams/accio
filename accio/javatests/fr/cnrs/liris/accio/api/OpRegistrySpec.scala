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

package fr.cnrs.liris.accio.api

import fr.cnrs.liris.testing.UnitSpec

/**
 * Unit tests for [[OpRegistry]].
 */
class OpRegistrySpec extends UnitSpec {
  private val registry = new OpRegistry(Operators.ops)
  private val names = Set("FirstSimple", "SecondSimple", "ThirdSimple", "Deprecated")

  behavior of "OpRegistry"

  it should "check whether an operator is registered" in {
    names.foreach { name =>
      registry.contains(name) shouldBe true
    }
    registry.contains("Unknown") shouldBe false
  }

  it should "return registered operators" in {
    names.foreach { name =>
      registry.get(name).map(_.name) shouldBe Some(name)
      registry(name).name shouldBe name
    }
  }

  it should "detect unknown operators" in {
    registry.get("Unknown") shouldBe None
    a[NoSuchElementException] shouldBe thrownBy {
      registry("Unknown")
    }
  }

  it should "return all registered operators" in {
    val ops = registry.ops
    ops should have size 4
    ops.map(_.name) should contain theSameElementsAs names
  }
}