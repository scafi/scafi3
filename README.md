<p align="center">

[//]: # (<img width=60% src="https://github.com/field4s/resources/blob/master/logos/field4s-logo-rounded-text.png?raw=true">)
<br>
<a href="#"><img src="https://img.shields.io/badge/Scala-%23DC322F.svg?logo=scala&logoColor=white" alt="Scala"></a>
<a href="./LICENSE"><img src="https://img.shields.io/github/license/field4s/field4s.svg?style=flat" alt="Apache 2.0 License"></a>
<a href="https://conventionalcommits.org"><img src="https://img.shields.io/badge/Conventional%20Commits-1.0.0-%23FE5196?logo=conventionalcommits&logoColor=white" alt="Conventional Commits"></a>
<a href="[https://codecov.io/gh/field4s/field4s](https://codecov.io/gh/scafi/scafi3)"><img src="https://codecov.io/gh/scafi/scafi3/graph/badge.svg?token=5ZT5AEMNDF" alt="codecov"></a>
</p>

# ScaFi 3

**ScaFi 3** (Scala Fields 3) is a modern Scala 3 DSL and toolkit for **Aggregate Programming**, a paradigm for designing resilient and self-organizing distributed systems.

## 🌟 What is Aggregate Programming?

Aggregate Programming enables you to program collective behaviors across networks of devices—from IoT sensors to robot swarms—by thinking in terms of **computational fields**: distributed data structures that span across the entire system. Instead of programming individual devices, you express global behaviors that automatically adapt to the network topology and evolve over time.

## 🎯 Philosophy and Design

ScaFi 3 embraces a **functional, composable, and type-safe** approach to distributed programming:

### Computational Fields First
At its core, ScaFi operates on **fields**: values mapped across space and time in a distributed network. A field might represent temperatures across sensors, distances from a source, or any aggregate value. Fields are first-class citizens that you can manipulate with aggregate operators.

### Effect System Integration
ScaFi 3 leverages Scala 3's advanced type system and **effect tracking** to provide compile-time safety guarantees:

- **Safer Exceptions**: Using Scala 3's `throws` clauses, functions explicitly declare what exceptions they can throw, enabling better error handling at compile time
- **Context Functions**: The framework uses `using`/`given` for implicit context passing, making the aggregate computation context available without boilerplate
- **Type-Level Guarantees**: Strong typing ensures that field operations are correctly aligned and serialization is properly configured

### Programming Style

ScaFi programs are:
- **Declarative**: Express *what* the system should compute, not *how* each device should behave
- **Composable**: Build complex behaviors from simple, reusable building blocks
- **Resilient**: Automatically handle device failures and network topology changes
- **Pure**: Computation logic is separated from side effects and device-specific concerns

## 📦 Installation

### With SBT

Add ScaFi 3 to your `build.sbt`:

```scala
libraryDependencies ++= Seq(
  "it.unibo.scafi" %%% "scafi3-core" % "1.0.5",
  // For distributed systems support
  "it.unibo.scafi" %%% "scafi3-distributed" % "1.0.5"
)
```

ScaFi 3 supports **JVM**, **JavaScript**, and **Native** platforms through Scala's cross-platform compilation:

```scala
// For a specific platform, use %% instead of %%%
libraryDependencies += "it.unibo.scafi" %% "scafi3-core" % "1.0.5"
```

### With Mill

Add ScaFi 3 to your `build.sc`:

```scala
import mill._, scalalib._

object myproject extends ScalaModule {
  def scalaVersion = "3.7.3"
  
  def ivyDeps = Agg(
    ivy"it.unibo.scafi::scafi3-core:1.0.5",
    // For distributed systems support
    ivy"it.unibo.scafi::scafi3-distributed:1.0.5"
  )
}
```

For cross-platform projects with Mill:

```scala
import mill._, scalalib._, scalajslib._, scalanativelib._

object myproject extends Cross[MyProjectModule](JVMPlatform, JSPlatform, NativePlatform)

class MyProjectModule(val platform: Platform) extends CrossPlatformScalaModule {
  def scalaVersion = "3.7.3"
  
  def ivyDeps = Agg(
    ivy"it.unibo.scafi::scafi3-core::1.0.5",
  )
}
```

## 🚀 Quick Start

### Basic Field Operations

The foundation of ScaFi is the **field calculus**, which provides three core primitives:

```scala
import it.unibo.scafi.language.xc.ExchangeLanguage
import it.unibo.scafi.language.xc.calculus.ExchangeCalculus
import it.unibo.scafi.libraries.All.*

// Define your aggregate program using context functions
def myProgram(using context: ExchangeCalculus & ExchangeLanguage): Int =
  // neighborValues: share a value with neighbors and get their values back
  val neighborIds = neighborValues(localId)
  
  // evolve: maintain state across rounds (like a distributed variable)
  val roundCount = evolve(0)(count => count + 1)
  
  // share: compute values while sharing them with neighbors
  val consensus = share(0)(neighborValues => 
    (neighborValues.toIterable.sum + roundCount) / (neighborValues.size + 1)
  )
  
  consensus
```

### Distance Gradient

A classic aggregate programming example—computing distance from a source:

```scala
import it.unibo.scafi.libraries.All.*

def gradient(using context: ExchangeCalculus & ExchangeLanguage { type DeviceId = Int }): Double =
  // Compute distance from source using neighbor distance + edge cost
  distanceTo[Double, Double](
    source = localId == 0, // Device 0 is the source
    distances = neighborValues(1.0) // Each hop costs 1.0
  )
```

### Domain Splitting with Branching

Control information flow with domain branching:

```scala
import it.unibo.scafi.libraries.All.*

def conditionalBehavior(using context: ExchangeCalculus & ExchangeLanguage { type DeviceId = Int }): String =
  val distanceFromSource = gradient
  
  // Split the network into two independent computational domains
  branch(distanceFromSource < 5.0)(
    "Close to source: " + distanceFromSource
  )(
    "Far from source: " + distanceFromSource
  )
```

### Exchange Calculus: The Foundation

Under the hood, ScaFi implements the **Exchange Calculus**, a more expressive variant of field calculus:

```scala
import it.unibo.scafi.libraries.All.*

def exchangeExample(using context: ExchangeCalculus & ExchangeLanguage): Int =
  // exchange: send and receive different values
  exchange(0) { receivedValues =>
    val maxFromNeighbors = receivedValues.withoutSelf.max
    val myValue = 1 + maxFromNeighbors
    
    // Return one value but send another to neighbors
    returning(myValue) send (myValue + 1)
  }(localId)
```

### Complete Example: Self-Healing Gradient

Here's a complete example showing ScaFi's resilience:

```scala
import it.unibo.scafi.language.xc.{ExchangeLanguage, ExchangeCalculus}
import it.unibo.scafi.libraries.All.*

// Define an aggregate program that computes a self-healing gradient
def selfHealingGradient(
  isSource: Boolean, 
  hopDistance: Double = 1.0
)(using context: ExchangeCalculus & ExchangeLanguage): Double =
  
  // Automatically computes and maintains shortest distance from source
  // Adapts to topology changes and device failures
  distanceTo[Double, Double](isSource, neighborValues(hopDistance))

// In practice, this would be executed by the ScaFi engine across devices
// Each device runs the same program but operates on its local context
```

## 🏗️ Key Concepts

### Shared Data Types
`SharedData[T]` represents a field value—a mapping from device IDs to values of type `T`. It includes:
- The local value on the current device
- Values received from aligned neighbors
- Combinators for field manipulation (map, flatMap, etc.)

### Alignment
ScaFi automatically manages **alignment**: ensuring that values from different parts of a computation are correctly associated across devices. The alignment mechanism uses tokens derived from the program structure.

### Context Functions
The `using` syntax provides implicit access to the aggregate computation context, which includes:
- Device ID and neighbor information
- Message history for state evolution
- Network communication primitives

## 🔧 Advanced Features

- **Multi-platform**: Run on JVM, JavaScript (browser/Node.js), and Native targets
- **Type-safe serialization**: Automatic codecs with compile-time guarantees using the `CodableFromTo` type class
- **Modular libraries**: Compose pre-built libraries for common patterns (gradients, broadcasting, leader election)
- **Integration ready**: Distributed module for real network deployments with socket-based communication

## 📚 Learn More

- **Examples**: Check the test suite for comprehensive examples
- **API Documentation**: [ScalaDoc](https://scafi.github.io/scafi3/)
- **Research**: Based on the [Aggregate Computing](https://doi.org/10.1109/MC.2015.261) research

## 📄 License

ScaFi 3 is released under the [Apache 2.0 License](./LICENSE). 
