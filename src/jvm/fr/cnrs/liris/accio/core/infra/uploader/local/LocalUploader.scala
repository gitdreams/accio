/*
 * Accio is a program whose purpose is to study location privacy.
 * Copyright (C) 2016 Vincent Primault <vincent.primault@liris.cnrs.fr>
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

package fr.cnrs.liris.accio.core.infra.uploader.local

import java.nio.file.Path

import fr.cnrs.liris.accio.core.service.Uploader
import fr.cnrs.liris.common.util.FileUtils

/**
 * Uploader copying files locally.
 *
 * @param rootDir Root directory under which to store files.
 */
final class LocalUploader(rootDir: Path) extends Uploader {
  override def upload(src: Path, dst: String): String = {
    // We copy files to another path. We do *not* want to symlink them, as original files can disapear at any time.
    val target = rootDir.resolve(dst)
    FileUtils.recursiveCopy(src, target)
    target.toAbsolutePath.toString
  }
}