# Accio is a platform to launch computer science experiments.
# Copyright (C) 2016-2018 Vincent Primault <v.primault@ucl.ac.uk>
#
# Accio is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# Accio is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Accio.  If not, see <http://www.gnu.org/licenses/>.

scala_library(
  name = "json",
  srcs = glob(["*.scala"]),
  deps = [
    "//accio/java/fr/cnrs/liris/accio/api",
    "//accio/java/fr/cnrs/liris/accio/dsl",
    "//accio/java/fr/cnrs/liris/util/geo",
    "//accio/java/fr/cnrs/liris/util",
    "//3rdparty/jvm/com/twitter:finatra_jackson",
    "//3rdparty/jvm/com/twitter:util_core",
  ],
  exports = [
    "//3rdparty/jvm/com/twitter:finatra_jackson",
  ],
  visibility = ["//visibility:public"],
)
