package it.unibo.scafi.api

class ExchangeCalculusLibraryTest extends JSLibraryTest:

  "Aggregate programs" should:
    "work on JS platform using portable libraries" when:
      "a simple exchange-based aggregate program is run" in:
        def aggregateProgram(library: FullLibrary) =
          library
            .exchange(library.Field.of(library.localId))(n => returnSending(n))
            .neighborValues

        val (_, status) = test(aggregateProgram)
        forAll(status): (id, field) =>
          forAll(field.toMap): (nid, nvalue) =>
            nvalue shouldBe (if nid <= id then nid else id)

end ExchangeCalculusLibraryTest
