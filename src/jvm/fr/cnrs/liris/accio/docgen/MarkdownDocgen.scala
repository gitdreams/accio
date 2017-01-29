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

package fr.cnrs.liris.accio.docgen

import java.io.{BufferedOutputStream, FileOutputStream, OutputStream}

import com.google.inject.Inject
import fr.cnrs.liris.accio.core.domain.{OpDef, Utils, Values}
import fr.cnrs.liris.accio.core.runtime.OpRegistry

/**
 * Generate documentation for all operators known to a registry in Markdown format.
 *
 * @param opRegistry Operator registry.
 */
class MarkdownDocgen @Inject()(opRegistry: OpRegistry) {
  /**
   * Generate documentation w.r.t. given options.
   *
   * @param flags Generator options.
   */
  def generate(flags: AccioDocgenFlags): Unit = {
    val out = new BufferedOutputStream(new FileOutputStream(flags.out.toFile))
    writeIntro(out, flags)
    opRegistry.ops.groupBy(_.category).foreach { case (category, ops) =>
      out.write(s"## ${category.capitalize} operators\n\n".getBytes)
      ops.toSeq.sortBy(_.name).foreach { opMeta =>
        writeOp(out, opMeta)
      }
    }

    out.close()
  }

  private def writeIntro(out: OutputStream, flags: AccioDocgenFlags) = {
    out.write("---\n".getBytes)
    out.write(s"layout: ${flags.layout}\n".getBytes)
    out.write(s"nav: ${flags.nav}\n".getBytes)
    out.write(s"title: ${flags.title}\n".getBytes)
    out.write("---\n\n".getBytes)

    if (flags.toc) {
      out.write("* TOC\n{:toc}\n\n".getBytes)
    }
  }

  private def writeOp(out: OutputStream, opDef: OpDef) = {
    out.write(s"### ${opDef.name}\n\n".getBytes)
    opDef.deprecation.foreach { deprecation =>
      out.write(s":broken_heart: **Deprecated:** $deprecation\n\n".getBytes)
    }
    opDef.help.foreach { help =>
      out.write(s"$help\n\n".getBytes)
    }
    opDef.description.foreach { description =>
      out.write(s"$description\n\n".getBytes)
    }
    if (opDef.inputs.nonEmpty) {
      out.write("| Input name | Type | Description |\n".getBytes)
      out.write("|:-----------|:-----|:------------|\n".getBytes)
      opDef.inputs.foreach { argDef =>
        out.write(s"| `${argDef.name}` | ${Utils.describe(argDef.kind)}".getBytes)
        if (argDef.defaultValue.isDefined) {
          out.write(s"; optional; default: ${Values.toString(argDef.defaultValue.get)}".getBytes)
        } else if (argDef.isOptional) {
          out.write("; optional".getBytes)
        } else {
          out.write("; required".getBytes)
        }
        out.write(s" | ${argDef.help.getOrElse("-")} |\n".getBytes)
      }
      out.write("{: class=\"table table-striped\"}\n\n".getBytes)
    }
    if (opDef.outputs.nonEmpty) {
      out.write("| Output name | Type | Description |\n".getBytes)
      out.write("|:------------|:-----|:------------|\n".getBytes)
      opDef.outputs.foreach { argDef =>
        out.write(s"| `${argDef.name}` | ${Utils.describe(argDef.kind)} | ${argDef.help.getOrElse("-")} |\n".getBytes)
      }
      out.write("{: class=\"table table-striped\"}\n\n".getBytes)
    }
  }
}