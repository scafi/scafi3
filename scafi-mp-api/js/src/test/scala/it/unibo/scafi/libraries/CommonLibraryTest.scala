package it.unibo.scafi.libraries

class CommonLibraryTest extends JSLibraryTest:

  "Aggregate programs" should:
    "work on JS platform using common library" when:
      "localId is used" in:
        def aggregateProgram(lang: FullLibrary) = lang.localId

        val (_, status) = test(aggregateProgram)
        forAll(status): (id, result) =>
          (result: Int) shouldBe id

      "device is used" in:
        def aggregateProgram(lang: FullLibrary) = lang.device

        val (env, status) = test(aggregateProgram)
        forAll(status): (id, result) =>
          val itself = id -> id
          val neighbors = env.neighborsOf(id).getOrElse(fail(s"Node with id $id not found!")).map(_.id)
          result.neighborValues.toMap should contain theSameElementsAs neighbors.map(id => id -> id) + itself
end CommonLibraryTest
