---
layout: docs
weight: 10
title: Codebase tour
---

This page contains more information for people willing to contribute to Accio.
It gives an overview of the code layout and pointers to help you getting ready into contributing to Accio.
A good place to start is reading [compilation instructions](compiling.html), to have an overview of languages and tools used.

* TOC
{:toc}

## Repository layout

We describe briefly the role of each top-level directory.

  * `3rdparty/`: contains Pants definitions of third party libraries, with one sub-directory per language.
  * `build-support/`: contains additional resources needed by Pants to work.
  * `docs/`: contains source code of these docs (but **not** the HTML of the compiled website).
  * `etc/`: contains configuration files and examples.
  * `src/`: contains source code of Accio, with one sub-directory per language.
  * `test/`: contains tests of Accio, with one sub-directory per language.

## Accio core
We have split Accio code into a *core* library, and applications.
The core library contains no executable application but all of Accio generic code, that can be used later as a library by other modules and by applications.
Accio core is grouped under the `fr.cnrs.liris.accio.core` package, which contains several subpackages.

  * `analysis`: code used when generating reports from run results.
  * `api`: it is the only dependency that modules defining custom operators need to rely on.
  It is a lightweight package that only defines an interface to be implemented by operators, and provides the [Sparkle library](../extending/sparkle.html).
  You can find more information about implementing custom operators in [the dedicated section](../extending/custom-operator.html).
  * `domain`: classes implenting data structures for [Accio concepts](../concepts/index.html) and associated utils.
  Most of the data structures are defined using the [Thrift IDL](https://thrift.apache.org/).
  * `downloader`: interface and implementation of downloader.
  * `dsl`: code related to DSL files parsing.
  * `runtime`: code needed to actually execute workflows.
  This package contains most of the logic of Accio, from validating data structures to executing an operator.
  * `scheduler`: interface and implementation of schedulers.
  * `statemgr`: interface and implementation of state managers.
  * `storage`: interface and implementation of persistent storage.
  * `uploader`: interface and implementation of uploaders.
  * `util`: small helper code.

## Accio components
Each of Accio components is contained inside its own package.
More information can be found on specific documentation pages.

  * **[Server](server.html)**: technical information about the agent, gateway and executor.
  * **[Client](client.html)**: technical information about the CLI application.
  * **[Web UI](ui.html)**: technical information about the Web interface.

## Common utils
We had to write some helper code, which is not coupled to Accio but is somewhat generic.
All of this code is grouped under the `fr.cnrs.liris.common` package.
We describe in the next sections some of them.

### Package fr.cnrs.liris.common.reflect
It is a reflection API for Scala used to discover interfaces provided by case classes at runtime.
In contrary to Scala's own reflection API, which need type information to be specified through TypeTag's, the API we define here analyses the JVM bytecode generated by the Scala compiler to infer type information.
The main advantage we found to this approach is that we only need a class name to get such information.

### Package fr.cnrs.liris.common.flags
It is a command-line flags parsing library for Scala.
It consists essentially in a port of [Bazel's options library](https://github.com/bazelbuild/bazel/tree/master/src/main/java/com/google/devtools/common/options) in Scala, that uses case classes and our reflection API.

### Package fr.cnrs.liris.common.geo
It is our spatial library, containing everything we need to deal with locations in various forms (either as a latitude/longitude pair or projected), distances and GeoJSON.
Internally uses [Google's S2 library](https://github.com/google/s2-geometry-library-java) for computations whenever possible.
S2 library is copy/pasted into Accio repository, as it is otherwise not available on Maven.

## Writing tests
A good practice is to write unit tests for your code.
Tests are written using [ScalaTest](http://www.scalatest.org), a testing framework designed for Scala.
We use the *FlatSpec* testing style, where all tests are described with sentences such as "An empty graph should have size 0".  
You can extend the `fr.cnrs.liris.testing.UnitSpec` class to get started quickly writing your own tests.

Keep in mind that all the code is tested at each push or pull request, so you want all the tests to be green at all time!