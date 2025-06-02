package it.unibo.scafi.api

// import it.unibo.scafi.libraries.All.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class JSXCApiTest extends AnyFlatSpec with should.Matchers:

  "A simple JS test" should "compile and run" in:
    true shouldBe true
