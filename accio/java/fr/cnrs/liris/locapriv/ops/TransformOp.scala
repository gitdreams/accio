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

import fr.cnrs.liris.accio.sdk.{Arg, OpContext, RemoteFile, ScalaOperator}
import fr.cnrs.liris.locapriv.domain.Event

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag
import scala.util.Random

private[ops] abstract class TransformOp[T: TypeTag: ClassTag]
  extends ScalaOperator[TransformOp.Out]
    with SparkleOperator {

  protected var seeds = Map.empty[String, Long]

  override final def execute(ctx: OpContext): TransformOp.Out = {
    val input = read[Event](data)
    if (ctx.hasSeed) {
      val rnd = new Random(ctx.seed)
      seeds = input.keys.map(key => key -> rnd.nextLong()).toMap
    }
    val output = input.mapPartitionsWithKey(transform)
    TransformOp.Out(write(output, 0, ctx))
  }

  protected def data: RemoteFile

  protected def transform(key: String, trace: Seq[Event]): Seq[T]
}

object TransformOp {

  case class Out(
    @Arg(help = "Transformed dataset")
    data: RemoteFile)

}