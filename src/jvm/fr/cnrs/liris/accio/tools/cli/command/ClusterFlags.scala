package fr.cnrs.liris.accio.tools.cli.command

import fr.cnrs.liris.common.flags.Flag

case class ClusterFlags(@Flag(name = "cluster") cluster: Option[String])