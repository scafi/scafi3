## [2.0.0](https://github.com/scafi/scafi3/compare/v1.2.2...v2.0.0) (2025-09-03)

### ⚠ BREAKING CHANGES

* add socket-based network manager requiring distributable values to be both Encodable & Decodable (#114)

### Features

* add socket-based network manager requiring distributable values to be both Encodable & Decodable ([#114](https://github.com/scafi/scafi3/issues/114)) ([e9d5c17](https://github.com/scafi/scafi3/commit/e9d5c174d5ab0977fe66618ff57b6f07b2b83dea))

### Style improvements

* forbid wildcard imports, merging them whenever possible ([#119](https://github.com/scafi/scafi3/issues/119)) ([d5e628a](https://github.com/scafi/scafi3/commit/d5e628a6199a65c713057683624c4c4d90e2c73a))

## [1.2.2](https://github.com/scafi/scafi3/compare/v1.2.1...v1.2.2) (2025-08-29)

### Dependency updates

* **core-deps:** update dependency scala to v3.7.2 ([9b4a202](https://github.com/scafi/scafi3/commit/9b4a202e93cddf073e9cfe797181f0566c38c76f))
* **deps:** update dependency ch.epfl.scala:sbt-scalafix to v0.14.3 ([38c141a](https://github.com/scafi/scafi3/commit/38c141a90a66c576f6fa8e3db1723166eec63abc))
* **deps:** update dependency com.github.sbt:sbt-ci-release to v1.11.2 ([2d27919](https://github.com/scafi/scafi3/commit/2d279196c32dd3966b0c4bcc621b74b5423194d2))
* **deps:** update dependency com.github.sbt:sbt-unidoc to v0.6.0 ([a9b92ac](https://github.com/scafi/scafi3/commit/a9b92aca984f52d3b1bed08e1caa4876212464c9))
* **deps:** update dependency org.scala-native:sbt-scala-native to v0.5.8 ([95a9fc6](https://github.com/scafi/scafi3/commit/95a9fc65482737a88d7ad89e0f8e25d408ca4ba8))
* **deps:** update dependency org.scalamock:scalamock to v7.3.2 ([0a084c4](https://github.com/scafi/scafi3/commit/0a084c4efa33faa3108c8bdbc8033e878c66cebc))
* **deps:** update dependency sbt/sbt to v1.11.0 ([f5fa3e7](https://github.com/scafi/scafi3/commit/f5fa3e7ba8691fd2d7cbc70589fc06ade6be7c03))
* **deps:** update dependency sbt/sbt to v1.11.1 ([eb92e29](https://github.com/scafi/scafi3/commit/eb92e297090fa7e9ff98844dd6dff22be47b9fc2))
* **deps:** update dependency sbt/sbt to v1.11.2 ([c16e9fc](https://github.com/scafi/scafi3/commit/c16e9fcf723c2959dae7670ee9214ec0afdeee33))
* **deps:** update dependency sbt/sbt to v1.11.3 ([fc740b2](https://github.com/scafi/scafi3/commit/fc740b206c39611ef863e91102374bca18f4dfad))
* **deps:** update dependency sbt/sbt to v1.11.4 ([3b9fdbe](https://github.com/scafi/scafi3/commit/3b9fdbeb5c30d6e34710a4feb09d07f9c0743f7c))
* **deps:** update dependency sbt/sbt to v1.11.5 ([902ad42](https://github.com/scafi/scafi3/commit/902ad421921ebd190043d9eb63147b6a9689f655))
* **deps:** update dependency scalafmt to v3.9.6 ([8fa0e07](https://github.com/scafi/scafi3/commit/8fa0e070ea21458176f7600a9f26ec29b001723b))
* **deps:** update dependency scalafmt to v3.9.7 ([c02375a](https://github.com/scafi/scafi3/commit/c02375aa2c1cd5a33e2d6384d3eb98bfe6368d28))
* **deps:** update dependency scalafmt to v3.9.8 ([fb04fb9](https://github.com/scafi/scafi3/commit/fb04fb9208509a49c9daae018acef4835565f20e))
* **deps:** update dependency scalafmt to v3.9.9 ([02756d4](https://github.com/scafi/scafi3/commit/02756d4f787ef168472baabc7a537bcf38e37dfe))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.128 ([0f88aa9](https://github.com/scafi/scafi3/commit/0f88aa92b9018a72f4958c76d79732bd92ae209c))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.129 ([a500ae3](https://github.com/scafi/scafi3/commit/a500ae34728574d2cb7aeddeee68c3c56f8651fc))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.130 ([9f3b047](https://github.com/scafi/scafi3/commit/9f3b047b9208e8c02bcef83e9f239b78e57ca160))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.131 ([a042e5a](https://github.com/scafi/scafi3/commit/a042e5ab6a37137402afcc6eb128f71b6ce110b8))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.132 ([e3fec51](https://github.com/scafi/scafi3/commit/e3fec517cee5d6d067a11f6cebe5cc3e13a2c11d))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.133 ([ffbc159](https://github.com/scafi/scafi3/commit/ffbc159eacb68eb196e1b245dee657bffd3607db))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.134 ([a986ca5](https://github.com/scafi/scafi3/commit/a986ca50932aa8d96f3f248c4cdc5a57db9f1201))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.135 ([31a8977](https://github.com/scafi/scafi3/commit/31a8977ad3ae5fe5f533d4885883152fe31dab37))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.136 ([64ac0bd](https://github.com/scafi/scafi3/commit/64ac0bd43c244c2fbb66bdbde636222ea0fc6158))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.137 ([eb6f1f3](https://github.com/scafi/scafi3/commit/eb6f1f342f47dd4f672b4c0fa55857aa6b538e82))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.138 ([7a5744a](https://github.com/scafi/scafi3/commit/7a5744ab55bf99abc03aacfdded5c33ef4fbb411))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.139 ([2bee87f](https://github.com/scafi/scafi3/commit/2bee87fb25efa8a13031100ab651ace63bf47faf))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.140 ([056024a](https://github.com/scafi/scafi3/commit/056024a334060e000a7487c58d293f2a0f3f1e7a))
* **deps:** update node.js to 22.16 ([d86271e](https://github.com/scafi/scafi3/commit/d86271e3105a228acb802b9d77edc098cc2bf46e))
* **deps:** update node.js to 22.17 ([905728c](https://github.com/scafi/scafi3/commit/905728cb2cf6d68bbaf23553addee57e62655528))
* **deps:** update node.js to 22.18 ([715c61c](https://github.com/scafi/scafi3/commit/715c61c558ad716ed5a790e23145922d021f0530))
* **deps:** update node.js to 22.19 ([f1996e5](https://github.com/scafi/scafi3/commit/f1996e55d330a77462451f17b78c8c24949af9e7))

### Build and continuous integration

* **deps:** update actions/checkout action to v4.3.0 ([143e8fe](https://github.com/scafi/scafi3/commit/143e8fe1ef9f614ae5b103f2ae37ecdc37db379c))
* **deps:** update actions/checkout action to v5 ([bb47911](https://github.com/scafi/scafi3/commit/bb4791157bfa1a058b166b3fc86214b4f528f2a9))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.20 ([29f654c](https://github.com/scafi/scafi3/commit/29f654c2c3dca56017d26d45e53874a0bdff2dd1))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.21 ([c5e6936](https://github.com/scafi/scafi3/commit/c5e6936878dbc82edc96bf40ce98078d0c5a68ce))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.22 ([7283c71](https://github.com/scafi/scafi3/commit/7283c718461815092e0753097e207500a42a18c2))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.23 ([8437dcb](https://github.com/scafi/scafi3/commit/8437dcbfe731fd6b57f5e5cce0c5b93f4fd6b59e))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.24 ([2065c44](https://github.com/scafi/scafi3/commit/2065c4451844df3cd4ed7f4c93d3cd6548a4bd11))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.25 ([de3200b](https://github.com/scafi/scafi3/commit/de3200b66553fd61e99a1186235be72ec4e1f0c4))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.27 ([fe18408](https://github.com/scafi/scafi3/commit/fe184082de4db140367b2930e9d92b7bd339bacd))
* re-enable scala multiplaform build (native + js) ([4d1af5a](https://github.com/scafi/scafi3/commit/4d1af5acaf5847ff8fdbe5441557d4550e7aae65))
* remove sonatype parameters as it is deprecated ([a86e79a](https://github.com/scafi/scafi3/commit/a86e79adb4f1d16153783ccad8d6c49a1d55f44d))
* remove sonatype parameters as it is deprecated ([51f9e64](https://github.com/scafi/scafi3/commit/51f9e64fb037284d637c50d514d1a92a1195f1c9))
* switch to windows-2025 and ubuntu-24.04 runners ([#98](https://github.com/scafi/scafi3/issues/98)) ([a2a5a59](https://github.com/scafi/scafi3/commit/a2a5a59713bd2f158e3259075bfa875c733f738f))

### Refactoring

* change engine structure and deeply refactor trait structures and package ([bf614e4](https://github.com/scafi/scafi3/commit/bf614e4363b9d5bdeb0eacf025ce54fec868b455))
* move `CanEqual` implicit in trait and suppress misleading unused warnings in type bounds ([#120](https://github.com/scafi/scafi3/issues/120)) ([b76e1f0](https://github.com/scafi/scafi3/commit/b76e1f065554ea28bf9c40ff8c8df91ba6c3aee5))

## [1.2.1](https://github.com/scafi/scafi3/compare/v1.2.0...v1.2.1) (2025-05-06)

### Dependency updates

* **core-deps:** update dependency scala to v3.7.0 ([3919422](https://github.com/scafi/scafi3/commit/3919422d5a10fb0fc01b820b4f276a3a4d7754d0))
* **deps:** update alchemistversion to v42.0.5 ([2b0a95e](https://github.com/scafi/scafi3/commit/2b0a95eb4960b7ef50cf67592dfb099690144fa4))
* **deps:** update alchemistversion to v42.0.6 ([c6af70d](https://github.com/scafi/scafi3/commit/c6af70d340fdd5128deba332d077f06b7b8ad78e))
* **deps:** update alchemistversion to v42.0.7 ([358b54b](https://github.com/scafi/scafi3/commit/358b54b6ca3d27836ffe26787dc24b8aab0f09b6))
* **deps:** update alchemistversion to v42.0.8 ([cba5b2b](https://github.com/scafi/scafi3/commit/cba5b2b4ae8f110b8a847f25ddc41dd0ec3f5a10))
* **deps:** update alchemistversion to v42.0.9 ([b6ed6b6](https://github.com/scafi/scafi3/commit/b6ed6b6871b889eb7468e9c998512497ffbf7680))
* **deps:** update alchemistversion to v42.1.0 ([20bae06](https://github.com/scafi/scafi3/commit/20bae06907a815defe687a5ee29a1402535628ba))
* **deps:** update dependency org.scala-js:sbt-scalajs to v1.19.0 ([9f84696](https://github.com/scafi/scafi3/commit/9f84696337a42a1cd5a47e1f79550c335cc1a3e0))
* **deps:** update dependency scalafmt to v3.9.5 ([fa6d529](https://github.com/scafi/scafi3/commit/fa6d529da382d75aa835f955f376789c75440a2b))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.126 ([17b3436](https://github.com/scafi/scafi3/commit/17b34368021cdeff04006c88e49e2198e0b084f4))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.127 ([ce72d76](https://github.com/scafi/scafi3/commit/ce72d76b5a31334ba2c1d669a81923012959cb10))
* **deps:** update node.js to 22.15 ([36edf41](https://github.com/scafi/scafi3/commit/36edf410e7966c7eb999bc1bada02ab81855d4f1))

### Build and continuous integration

* **deps:** update actions/setup-node action to v4.4.0 ([415af86](https://github.com/scafi/scafi3/commit/415af866cd3094706214172ebcd4150eee697ee4))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.18 ([9750a34](https://github.com/scafi/scafi3/commit/9750a34aa3570de5fedff03cfa04cdc4745b996a))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.19 ([8efe870](https://github.com/scafi/scafi3/commit/8efe870cdfdff4b814035e82130298a484b99cc6))

## [1.2.0](https://github.com/scafi/scafi3/compare/v1.1.4...v1.2.0) (2025-04-07)

### Features

* add align method for explicit alignment ([21eff3e](https://github.com/scafi/scafi3/commit/21eff3e29fadfa86fff6e5747874e73473801f15))

### Dependency updates

* **deps:** update dependency scalafmt to v3.9.4 ([5613dfd](https://github.com/scafi/scafi3/commit/5613dfd2689dbdd9d727cba7a11b847df9b75ebc))

### Documentation

* update readme ([b01981a](https://github.com/scafi/scafi3/commit/b01981a97f0bcfdf854a48f4fa47a05b31b634bc))

### Build and continuous integration

* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.17 ([b6385ee](https://github.com/scafi/scafi3/commit/b6385ee217606daeddc1c22cdf400e27e0970f37))

### Refactoring

* use scafi package name ([be9e26e](https://github.com/scafi/scafi3/commit/be9e26ecd2398fe1a817334aecf81c048364545a))

## [1.1.4](https://github.com/field4s/field4s/compare/v1.1.3...v1.1.4) (2025-03-21)

### Dependency updates

* **deps:** update dependency sbt/sbt to v1.10.11 ([eb9d59d](https://github.com/field4s/field4s/commit/eb9d59d6f0fcf094b476f1b339a797551b4cf82c))

### Performance improvements

* aligned process optimized ([3f9b9e2](https://github.com/field4s/field4s/commit/3f9b9e2dd64c44b1efbe3aa4ccf1e3a195ead478))

### Build and continuous integration

* **deps:** update actions/setup-node action to v4.3.0 ([79bbcc7](https://github.com/field4s/field4s/commit/79bbcc732e8a4787e8d6937a33f204eebd4b9606))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.16 ([d4d5a32](https://github.com/field4s/field4s/commit/d4d5a32f28a70868c81aff73b40fe49b70d2aae5))

### General maintenance

* remove class files ([b814b4d](https://github.com/field4s/field4s/commit/b814b4d650ad829daa9fa5125757bf69cb7d951c))

### Refactoring

* avoid deprecated methods ([642b63c](https://github.com/field4s/field4s/commit/642b63c3cf8381394d30642a41ac1dd6e2608f41))

## [1.1.3](https://github.com/field4s/field4s/compare/v1.1.2...v1.1.3) (2025-03-14)

### Performance improvements

* improve performance reading currentPath ([e88bfeb](https://github.com/field4s/field4s/commit/e88bfeb372b07c9490157dda4be3f0ccbdd8b675))

## [1.1.2](https://github.com/field4s/field4s/compare/v1.1.1...v1.1.2) (2025-03-14)

### Dependency updates

* **deps:** update alchemistversion to v41 ([7e2626f](https://github.com/field4s/field4s/commit/7e2626f0bc3452b8448d2edea3b788d5da049e47))
* **deps:** update alchemistversion to v42 ([10d6258](https://github.com/field4s/field4s/commit/10d62582c9d52e212826d2cbfcd0705feda36df3))
* **deps:** update alchemistversion to v42.0.1 ([6b83f55](https://github.com/field4s/field4s/commit/6b83f55695a009ff4c7a6cdc21bac3f2c378f060))
* **deps:** update dependency com.github.sbt:sbt-ci-release to v1.9.3 ([6893346](https://github.com/field4s/field4s/commit/6893346f00e1a9cbdb90fec07853f764bae1835f))
* **deps:** update dependency sbt/sbt to v1.10.10 ([cfb078e](https://github.com/field4s/field4s/commit/cfb078e915ee4c8fdda6cdcf876439afb069f0a0))

### Performance improvements

* improve the alignedValues performance ([f9ddc71](https://github.com/field4s/field4s/commit/f9ddc71bd0d794e81b501ef732e85170f2975285))

### Style improvements

* add comments ([ca4219a](https://github.com/field4s/field4s/commit/ca4219a7aaacf463f9faa21123a382c310dee09b))
* run scalafmt ([4b98a44](https://github.com/field4s/field4s/commit/4b98a44e5a8871ce03c76fd07f2215605b7c7d54))

### Refactoring

* better name of aggregate ([93ce62b](https://github.com/field4s/field4s/commit/93ce62bd9ae8255a62c4c8a01ee2a7818de76aca))
* better names ([fcb7124](https://github.com/field4s/field4s/commit/fcb71243e5744fbc8d166417e2ba3883eba97859))
* improve readability ([91d3fe0](https://github.com/field4s/field4s/commit/91d3fe0186199c2e65853690719fc7d76b5c46e4))
* improving names ([0e20d4d](https://github.com/field4s/field4s/commit/0e20d4dd27f08b060333a465e732776e18145d88))
* more on style ([b16b833](https://github.com/field4s/field4s/commit/b16b833228735cdf9af0379dc6e5984710f2bee0))
* move FieldBasedSharedData to langugage.exchange ([213c850](https://github.com/field4s/field4s/commit/213c850d254ad46e9e581dc98def5b3d38e485c1))
* remove `DeviceAwareAggregateFoundation` trait ([524d8d2](https://github.com/field4s/field4s/commit/524d8d25a2f3d7cd2212e04058c34f234e146560))
* remove foundation ([a696d26](https://github.com/field4s/field4s/commit/a696d269e79cd3e2ad9654527f57e6a72b86c536))
* update the package exchange as first level ([30fc21d](https://github.com/field4s/field4s/commit/30fc21d49309c1c0f0be5852c533878236908f40))
* update to alchemist 40 ([63fcf88](https://github.com/field4s/field4s/commit/63fcf88e04c82ba891fd42b70c146436ade7e6a5))

## [1.1.1](https://github.com/field4s/field4s/compare/v1.1.0...v1.1.1) (2025-03-08)

### Dependency updates

* **core-deps:** update dependency scala to v3.6.4 ([827aa9a](https://github.com/field4s/field4s/commit/827aa9a1742a3894266df957224a75dc1946d98f))

## [1.1.0](https://github.com/field4s/field4s/compare/v1.0.1...v1.1.0) (2025-03-08)

### Features

* bindings and sematics separation ([cfc622d](https://github.com/field4s/field4s/commit/cfc622d04ca893ec228923d5f1b9c0e6e943a867))
* improve API ergonomics ([dda4113](https://github.com/field4s/field4s/commit/dda411372c7fd79d1f90f50af6421905086daa92))
* improve API ergonomics ([df8dff3](https://github.com/field4s/field4s/commit/df8dff36c6742428d32c6dded5a3e7658fadc373))
* improve the API ergonomics ([e37ab56](https://github.com/field4s/field4s/commit/e37ab564fff6224848844f58c152e1e8b7fc6753))
* integrate alchemist incarnation from [@ldeluigi](https://github.com/ldeluigi) ([1402d9f](https://github.com/field4s/field4s/commit/1402d9fc796303b2d8c58df0427b41ff4dcb9f03))
* leaving to nico the beghesg ([f491dd4](https://github.com/field4s/field4s/commit/f491dd42ef593f86d37f7d3d579a683521129810))
* more on API ergonomics ([0049d3b](https://github.com/field4s/field4s/commit/0049d3bd6a7276deb8f1fa01c7ed944e1a5bc58c))
* more on ergonomics ([7252251](https://github.com/field4s/field4s/commit/725225147927570a2b608510d1716392ccd83950))
* more on ergonomics ([7b1a215](https://github.com/field4s/field4s/commit/7b1a215b53184bca3dad17a8a6b80dc31353c9a9))
* more on ergonomics ([9d6b1f8](https://github.com/field4s/field4s/commit/9d6b1f8798b54688a30c7520ec8877ddb1b79535))
* move the bindings package ([240780d](https://github.com/field4s/field4s/commit/240780db1ce22cfe7f6b884e7e683c2cbc5e6a20))
* on API ergonomics ([2b17ccb](https://github.com/field4s/field4s/commit/2b17ccbe70680fb03fe6cdd5b035da7327b3aea5))
* refactor, remove the concept of Language ([307b74d](https://github.com/field4s/field4s/commit/307b74dd2193d09d7747df651c37fc2cd3f1c00c))

### Dependency updates

* **deps:** update dependency ch.epfl.scala:sbt-scalafix to v0.14.2 ([7aa101f](https://github.com/field4s/field4s/commit/7aa101f720bf8586f06b84ef1f7f0409d2319a11))
* **deps:** update dependency org.scala-js:sbt-scalajs to v1.18.2 ([ccde28d](https://github.com/field4s/field4s/commit/ccde28d956fe1fd0fba463020dd39796ab019661))
* **deps:** update dependency org.scala-native:sbt-scala-native to v0.5.7 ([cef5862](https://github.com/field4s/field4s/commit/cef5862b72a901f966a6740bc9fd628e34d68f42))
* **deps:** update dependency org.scoverage:sbt-scoverage to v2.3.1 ([d0e0b23](https://github.com/field4s/field4s/commit/d0e0b23d66efa60969b2e28e08502da08ba054ac))
* **deps:** update dependency org.typelevel:cats-core to v2.13.0 ([357abbf](https://github.com/field4s/field4s/commit/357abbfeb844bde2877ca1e8aa62e3cbacf774c5))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.120 ([c4ef863](https://github.com/field4s/field4s/commit/c4ef8632c916ce02b13c33123f64419d917abe06))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.121 ([8f84227](https://github.com/field4s/field4s/commit/8f842272e62e78c2538924267e1146dc0df113e4))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.122 ([d409ec9](https://github.com/field4s/field4s/commit/d409ec9b211187628dbe376308904aea82eb6bb0))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.123 ([0952f15](https://github.com/field4s/field4s/commit/0952f159528c3b13577f56b751895b54c3ada659))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.124 ([226420a](https://github.com/field4s/field4s/commit/226420a31b80acd0ea7cf46490a8bfc05f495216))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.125 ([10e03fa](https://github.com/field4s/field4s/commit/10e03fabc3f4f1aa23c2f483799f1ec2adc17e62))
* **deps:** update node.js to 22.14 ([6440ad4](https://github.com/field4s/field4s/commit/6440ad4b50af85ba1e5b34a0658411727b2b622c))

### Bug Fixes

* **test:** add binding AbstractExchangeCalculusContext ([6616b2b](https://github.com/field4s/field4s/commit/6616b2bf70c29aabf6a8be16dc9852453e0c664a))
* use SharedData instead of AggregateValue ([7fc3673](https://github.com/field4s/field4s/commit/7fc3673f01a89d207342967b0c742988d769e9ea))

### Tests

* use new syntax ([4e801eb](https://github.com/field4s/field4s/commit/4e801ebe8930899459a447770d639679688bc50c))

### Build and continuous integration

* **deps:** update actions/setup-node action to v4.2.0 ([802a86a](https://github.com/field4s/field4s/commit/802a86a4d71f1df0e80be6cc2e2c10ede93c7992))
* update actions ([6de9113](https://github.com/field4s/field4s/commit/6de91131ca742184a6598c8950c0eba6f06dfebb))
* update build-check-deploy-sbt-actions ([84a5dde](https://github.com/field4s/field4s/commit/84a5dde27e2a4a9f296abc6510c9fa7270e2ba43))

### General maintenance

* **readme:** better badges positioning ([80836ed](https://github.com/field4s/field4s/commit/80836ed0355c79d7a549fa45ba6827bb681fc0af))

### Style improvements

* reformat style ([05ea4cd](https://github.com/field4s/field4s/commit/05ea4cdd482d22675258acbdcf67046fffd0250f))
* sort imports ([008be35](https://github.com/field4s/field4s/commit/008be3515b57f800cfeeedcb3ce706127a4f2a11))
* update scalafmt version ([8c6f01c](https://github.com/field4s/field4s/commit/8c6f01c17ea80762799f4148628de431d3da5216))

### Refactoring

* solve deprecated abstract given ([ec01a68](https://github.com/field4s/field4s/commit/ec01a683059d6ab9d3079da4f1f7edd11ef623dc))

## [1.0.1](https://github.com/field4s/field4s/compare/v1.0.0...v1.0.1) (2025-01-17)

### Dependency updates

* **core-deps:** update dependency scala to v3.6.3 ([089e455](https://github.com/field4s/field4s/commit/089e4552e6e7a8684b0bd4708afbcb98bfe1fead))
* **deps:** update dependency ch.epfl.scala:sbt-scalafix to v0.14.0 ([34fd93a](https://github.com/field4s/field4s/commit/34fd93a8dea3bf35ad1e01056933c176d706b437))
* **deps:** update dependency com.github.sbt:sbt-ci-release to v1.9.2 ([dcccc04](https://github.com/field4s/field4s/commit/dcccc04bdae865e49bc891022f1ccc7ef0b1f6d7))
* **deps:** update dependency org.scala-js:sbt-scalajs to v1.18.0 ([0c33886](https://github.com/field4s/field4s/commit/0c33886f433ded80080939c86bb3f44a5e2d411d))
* **deps:** update dependency org.scala-js:sbt-scalajs to v1.18.1 ([e01f2a5](https://github.com/field4s/field4s/commit/e01f2a5016bf821a0e4fa17aa08bd411369c06d3))
* **deps:** update dependency org.scoverage:sbt-scoverage to v2.3.0 ([39c83a2](https://github.com/field4s/field4s/commit/39c83a25f287daf5afc0e832d04c03ce5cf915e5))
* **deps:** update dependency sbt/sbt to v1.10.7 ([7c65a8d](https://github.com/field4s/field4s/commit/7c65a8d0562c4ceefbd9359c0612bdda4437ba16))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.117 ([8b72f95](https://github.com/field4s/field4s/commit/8b72f95d4aa504ff0b4c5064d66e1d700fe847d1))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.118 ([7cb6073](https://github.com/field4s/field4s/commit/7cb6073872dc50a862afb7f5763098baaa249e3f))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.119 ([9b8906b](https://github.com/field4s/field4s/commit/9b8906b175f678e35cb85f346f03d7aaedfa3b36))
* **deps:** update node.js to 22.13 ([5b65c18](https://github.com/field4s/field4s/commit/5b65c181fbdfdcf7e23b1fa2db0b8317589534fb))

## 1.0.0 (2024-12-10)

### Dependency updates

* **core-deps:** update dependency scala to v3.6.2 ([6148238](https://github.com/field4s/field4s/commit/61482383436eda262917230aa642b3e689636605))
* **deps:** update dependency org.scala-native:sbt-scala-native to v0.5.6 ([02a1737](https://github.com/field4s/field4s/commit/02a1737b24054faa21f6339262cc7916805cd502))
* **deps:** update dependency sbt/sbt to v1.10.6 ([d4993a5](https://github.com/field4s/field4s/commit/d4993a53ebef01c180b3ccd5c3ad12cba616082c))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.115 ([c1e2b88](https://github.com/field4s/field4s/commit/c1e2b88fe957cb5973e8b68256e97d4f7ea5d7c7))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.116 ([64998fd](https://github.com/field4s/field4s/commit/64998fd2d0655cfffce1392a07c05ce5eb2f3fff))
* **deps:** update node.js to 22.12 ([d5435fb](https://github.com/field4s/field4s/commit/d5435fb001c4f2d0c33f4ca7df6f26f2cee3418b))
* **deps:** update node.js to v22 ([0040b9f](https://github.com/field4s/field4s/commit/0040b9ff91ead69a51cbacf507cd0ee427f619c5))

### Build and continuous integration

* setup project based on `scafi-xc` ([#5](https://github.com/field4s/field4s/issues/5)) ([f7815fa](https://github.com/field4s/field4s/commit/f7815fad9732d717b85382bab24f068afe12bd6c))
* use new syntax mjs ([6dc3bdd](https://github.com/field4s/field4s/commit/6dc3bdd00019fca08a7d2de8c9070112eadf873c))

### General maintenance

* add coverage badge ([41bd56b](https://github.com/field4s/field4s/commit/41bd56b8ad3cebc866205b97f845fc92f9151000))
* add intro to field4s ([d4595fd](https://github.com/field4s/field4s/commit/d4595fd678d236c824ad4cf18c0d461485094cb6))
* add readme ([d993fc4](https://github.com/field4s/field4s/commit/d993fc464fe620e174e195d74d06c0fc88a23b3d))
* emphasis on scala 3 ([17c5a94](https://github.com/field4s/field4s/commit/17c5a9495d6d2ec3c9208ad0eb11f39b76d744ed))
* update renovate config ([340db01](https://github.com/field4s/field4s/commit/340db01a7d2532ca0c06b1a3dbb2356b707d2592))

### Refactoring

* remove Mappable and use Functor from cats ([18bae2a](https://github.com/field4s/field4s/commit/18bae2a2b095afd9b953e8e8a66018bd6be6b14b))
* use Applicative instead Liftable ([04d358b](https://github.com/field4s/field4s/commit/04d358bf114c89c0bffe58ed2605860956c31e08))
