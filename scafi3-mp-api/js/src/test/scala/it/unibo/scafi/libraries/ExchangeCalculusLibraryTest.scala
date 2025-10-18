// package it.unibo.scafi.libraries

// class ExchangeCalculusLibraryTest extends JSLibraryTest:

//   "Aggregate programs" should:
//     "work on JS platform using exchange library" when:
//       "a simple exchange-based aggregate program is run" in:
//         def aggregateProgram(lang: FullLibrary) =
//           lang
//             .exchange(lang.Field.of(lang.localId))(n => returnSending(n))
//             .neighborValues

//         val (_, status) = test(aggregateProgram)
//         forAll(status): (id, field) =>
//           forAll(field.toMap): (nid, nvalue) =>
//             nvalue shouldBe (if nid <= id then nid else id)
