// package it.unibo.scafi.libraries

// class BranchingLibraryTest extends JSLibraryTest:

//   "Aggregate programs" should:
//     "work on JS platform using branching library" when:
//       "domain branching operation is run" in:
//         def aggregateProgram(lang: FullLibrary) =
//           lang.branch(lang.localId.isEven) { () =>
//             lang.exchange(lang.Field.of(true))(n => returnSending(n))
//           } { () =>
//             lang.exchange(lang.Field.of(false))(n => returnSending(n))
//           }

//         val (env, status) = test(aggregateProgram)
//         forAll(status): (id, result) =>
//           val alignedNeighbors = Set(id) ++ env // self is always aligned
//             .neighborsOf(id)
//             .getOrElse(fail(s"Node with id $id not found!"))
//             .map(_.id)
//             .filter(_.hasSameParityAs(id))
//           result.neighborValues.toMap should contain theSameElementsAs alignedNeighbors.map(_ -> id.isEven)

//   extension (id: Int)
//     def isEven: Boolean = id % 2 == 0
//     def hasSameParityAs(other: Int): Boolean = id.isEven == other.isEven
// end BranchingLibraryTest
