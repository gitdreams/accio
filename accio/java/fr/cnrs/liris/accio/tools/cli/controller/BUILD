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
  name = "controller",
  srcs = glob(["*.scala"]),
  deps = [
    "//accio/java/fr/cnrs/liris/accio/api",
    "//accio/thrift/fr/cnrs/liris/accio/api",
    "//accio/thrift/fr/cnrs/liris/accio/agent",
    "//accio/java/fr/cnrs/liris/accio/tools/cli/event",
    "//accio/java/fr/cnrs/liris/accio/tools/cli/terminal",
    "//accio/java/fr/cnrs/liris/util",
    "//3rdparty/jvm/org/ocpsoft/prettytime",
    "//3rdparty/jvm/com/github/nscala_time",
    "//3rdparty/jvm/com/google/guava",
  ],
  visibility = ["//visibility:public"],
)
