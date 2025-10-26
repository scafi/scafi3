## [1.3.1](https://github.com/scafi/scafi3/compare/v1.3.0...v1.3.1) (2025-10-25)

### Dependency updates

* **deps:** update alchemistversion to v42.3.14 ([843ce8a](https://github.com/scafi/scafi3/commit/843ce8ad50a0aaa1c4e347f31f3e22c1b9deb9cd))

### Bug Fixes

* avoid the pitfall of possibly infinitely recursive calls due to an extension method delegating to a class method with the same name, for instance `default` on SharedDataOps and Field ([#168](https://github.com/scafi/scafi3/issues/168)) ([accc4ef](https://github.com/scafi/scafi3/commit/accc4ef3e62c05c52045ba800f3cebcfc2d81e30))

## [1.3.0](https://github.com/scafi/scafi3/compare/v1.2.0...v1.3.0) (2025-10-23)

### Features

* add "fielded" methods for common type classes ([#163](https://github.com/scafi/scafi3/issues/163)) ([68faef9](https://github.com/scafi/scafi3/commit/68faef9a02b73d99a6965d709f86972f7762013c))

### Dependency updates

* **deps:** update alchemistversion to v42.3.13 ([dfd47ef](https://github.com/scafi/scafi3/commit/dfd47efa9f7f9f144a3ac9733f92e19a27697019))
* **deps:** update dependency ch.epfl.scala:sbt-scalafix to v0.14.4 ([4daa983](https://github.com/scafi/scafi3/commit/4daa9831e42803d1daf6639c1481a491808ae9f7))
* **deps:** update dependency org.scoverage:sbt-scoverage to v2.4.0 ([70dc65c](https://github.com/scafi/scafi3/commit/70dc65cb980798050050ec7c32f6614e22659936))
* **deps:** update dependency scalafmt to v3.10.1 ([92c8885](https://github.com/scafi/scafi3/commit/92c88855e4d784eb6c55b5bea02d241d2ece17c0))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.148 ([7967341](https://github.com/scafi/scafi3/commit/7967341c5d1018b74b35d551d6b7a92f68f07c9f))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.149 ([1c0ad6a](https://github.com/scafi/scafi3/commit/1c0ad6a5f838cf9cadc41f37aa086d508edb3730))
* **deps:** update node.js to 22.21 ([17e4694](https://github.com/scafi/scafi3/commit/17e4694c96b6ab0f35f23aa6d2a253a422980d5b))

## [1.2.0](https://github.com/scafi/scafi3/compare/v1.1.0...v1.2.0) (2025-10-18)

### Features

* **incarnation:** checkrequired parameters in yaml specification ([46dcbe3](https://github.com/scafi/scafi3/commit/46dcbe312df20d59aca7257c8d610cd437c07990))

### Dependency updates

* **deps:** update alchemistversion to v42.3.12 ([b5430b5](https://github.com/scafi/scafi3/commit/b5430b526dd11ef875d131dec00fc7c52d0501ed))

### Bug Fixes

* aggregate incarnation in root project ([e77155b](https://github.com/scafi/scafi3/commit/e77155b1b81a9c4553fef5788ec9a4f660d82f44))

### Style improvements

* reformat code ([cdad6cc](https://github.com/scafi/scafi3/commit/cdad6cc30cb89d04d571b2478fb1c9f58cfe603d))

## [1.1.0](https://github.com/scafi/scafi3/compare/v1.0.5...v1.1.0) (2025-10-17)

### Features

* implement Alchemist incarnation for scafi3 ([#145](https://github.com/scafi/scafi3/issues/145)) ([9712811](https://github.com/scafi/scafi3/commit/9712811b46b835b7001f95cebe92aee742d8d6ee))

### Dependency updates

* **deps:** update dependency scalafmt to v3.10.0 ([294fdb3](https://github.com/scafi/scafi3/commit/294fdb397953f4ac33c20a076d238af1cb1d6215))

### Build and continuous integration

* **deps:** update actions/setup-node action to v6 ([fc753aa](https://github.com/scafi/scafi3/commit/fc753aa4e3a721a5f76567794dbeba2cd3d6558a))

## [1.0.5](https://github.com/scafi/scafi3/compare/v1.0.4...v1.0.5) (2025-10-12)

### Bug Fixes

* **distributed:** prevent socket from closing on chunked messages in JS and enforce explicit byte order ([#150](https://github.com/scafi/scafi3/issues/150)) ([8af737c](https://github.com/scafi/scafi3/commit/8af737cd4a81e8b6ccfe68f693e501b5bc68dcd9))

### General maintenance

* update coverage badge ([4dac201](https://github.com/scafi/scafi3/commit/4dac201a9a45d79c3902908a29cf518f58b0f970))

## [1.0.4](https://github.com/scafi/scafi3/compare/v1.0.3...v1.0.4) (2025-10-08)

### Dependency updates

* **deps:** update dependency sbt/sbt to v1.11.7 ([675fbc0](https://github.com/scafi/scafi3/commit/675fbc062327e69c909e705b8b5393ccdb074a18))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.146 ([fa3d778](https://github.com/scafi/scafi3/commit/fa3d77839135a226454eb42f661f662a46bac3b0))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.147 ([732c8f4](https://github.com/scafi/scafi3/commit/732c8f47b3f0a514620c6af542c7e2a3bb05c835))
* **deps:** update node.js to 22.20 ([4e7431c](https://github.com/scafi/scafi3/commit/4e7431cea692f77c6d19df0ad38208246539204d))

### Bug Fixes

* rename all modules to scafi3 to prevent publishing clashes ([e6e724b](https://github.com/scafi/scafi3/commit/e6e724bfffed5f18b5eded01d0b8c551785070b0))

### Build and continuous integration

* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.30 ([230be7c](https://github.com/scafi/scafi3/commit/230be7c6ecb3e14e73c904a443077223c5eb5961))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.31 ([0da70b1](https://github.com/scafi/scafi3/commit/0da70b1fe3f71989d9ed870c827a708a829bd7e5))
* prevent coverage to be applied always ([7f76664](https://github.com/scafi/scafi3/commit/7f76664aca3642c7984833bba4e2a2fe0ac1567a))
* set explicit task for coverage ([483bf76](https://github.com/scafi/scafi3/commit/483bf76beb3b112ae57c0705375bdb725d975d51))

### General maintenance

* add codecov configuration file ([9f76744](https://github.com/scafi/scafi3/commit/9f767447d0d2bc5675e2d547f10ef4fad2331483))

## [1.0.3](https://github.com/scafi/scafi3/compare/v1.0.2...v1.0.3) (2025-09-23)

### Dependency updates

* **deps:** update dependency org.scalameta:sbt-scalafmt to v2.5.5 ([a26b085](https://github.com/scafi/scafi3/commit/a26b0853cd9553a55112e012f642dad96dc4e763))

### Bug Fixes

* **core:** propagate self-messages locally between rounds without relying on the network ([#137](https://github.com/scafi/scafi3/issues/137)) ([5c99e5c](https://github.com/scafi/scafi3/commit/5c99e5c65d4b1b10799965b2e3def6981fce9384))

### General maintenance

* use unix lineEndings when formatting with scalafmt ([#134](https://github.com/scafi/scafi3/issues/134)) ([a81945a](https://github.com/scafi/scafi3/commit/a81945af4907f8c11b35cde482584f4617743dd1))

## [1.0.2](https://github.com/scafi/scafi3/compare/v1.0.1...v1.0.2) (2025-09-23)

### Dependency updates

* **deps:** update dependency scalafmt to v3.9.10 ([f2d2562](https://github.com/scafi/scafi3/commit/f2d25625ca3e8ac07586bef5bf7df8d75bf0ab11))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.142 ([940fa3c](https://github.com/scafi/scafi3/commit/940fa3cedefd75e380b273c0b99eb4336485350e))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.143 ([dbaf865](https://github.com/scafi/scafi3/commit/dbaf865cbc7132b980a614aab3835315f5024158))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.144 ([01934f7](https://github.com/scafi/scafi3/commit/01934f7ec9e0b1fb1b849eae3a2f38f1f187fcab))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.145 ([d03b289](https://github.com/scafi/scafi3/commit/d03b28973b72ab8dc9609a4480c9c1b339859c27))

### Documentation

* use new `ReturnSending` syntax in doc examples ([#131](https://github.com/scafi/scafi3/issues/131)) ([02cd09c](https://github.com/scafi/scafi3/commit/02cd09c74c83cd7eea1c057c9d31625a6cfcd4f5))

### Build and continuous integration

* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.29 ([55a8c20](https://github.com/scafi/scafi3/commit/55a8c20ed97921fc116b1790fddab8c5e118f525))

## [1.0.1](https://github.com/scafi/scafi3/compare/v1.0.0...v1.0.1) (2025-09-09)

### Dependency updates

* **core-deps:** update dependency scala to v3.7.3 ([c8c3daf](https://github.com/scafi/scafi3/commit/c8c3daf4be061049e23657e33214f340cda87dff))
* **deps:** update dependency org.scala-js:sbt-scalajs to v1.20.1 ([223eab0](https://github.com/scafi/scafi3/commit/223eab0ceabc9f509ead244af326487a49cfac6a))
* **deps:** update dependency sbt/sbt to v1.11.6 ([4b77453](https://github.com/scafi/scafi3/commit/4b77453c6d70688887bea364f5dcddf43baf04a0))

### Build and continuous integration

* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.28 ([228bccf](https://github.com/scafi/scafi3/commit/228bccf9458456d1f7f5a7391efa2a7b4e45ae70))

## 1.0.0 (2025-09-05)

### ⚠ BREAKING CHANGES

* add socket-based network manager requiring distributable values to be both Encodable & Decodable (#114)

### Features

* add align method for explicit alignment ([c4c0477](https://github.com/scafi/scafi3/commit/c4c047720c955557e9a006b3fca5b0b9260df2f4))
* add socket-based network manager requiring distributable values to be both Encodable & Decodable ([#114](https://github.com/scafi/scafi3/issues/114)) ([0d103eb](https://github.com/scafi/scafi3/commit/0d103ebe185777e91565bfb2727ff6a06fa69c92))
* bindings and sematics separation ([5f7c415](https://github.com/scafi/scafi3/commit/5f7c415c993061d0cca18defb6d89755909c7498))
* improve API ergonomics ([979b5b5](https://github.com/scafi/scafi3/commit/979b5b566ad33f3efe804c5d176db4d19be03a12))
* improve API ergonomics ([b83ed79](https://github.com/scafi/scafi3/commit/b83ed797412d1278a2488438c4adbfeec86316a2))
* improve the API ergonomics ([9e349f8](https://github.com/scafi/scafi3/commit/9e349f8adad58e3223c31f760a43862373a2b4e1))
* integrate alchemist incarnation from [@ldeluigi](https://github.com/ldeluigi) ([966dd37](https://github.com/scafi/scafi3/commit/966dd378941a59a1be4ebf035a76654ffea1c180))
* leaving to nico the beghesg ([aad0c23](https://github.com/scafi/scafi3/commit/aad0c23fd00ef01a29d89836a9d7f9c707eccb30))
* more on API ergonomics ([c588df4](https://github.com/scafi/scafi3/commit/c588df4edcbacfe268a1fac6ca26091c396ca2e9))
* more on ergonomics ([4b90d6c](https://github.com/scafi/scafi3/commit/4b90d6cfaa642b136da988fcce1d618f8d7b6288))
* more on ergonomics ([5e257c9](https://github.com/scafi/scafi3/commit/5e257c9cb7c12f521e5c94fd220a41d974a74cf9))
* more on ergonomics ([e4e4591](https://github.com/scafi/scafi3/commit/e4e45917a38d66f6c92a06ef7d2c5d303903d64b))
* move the bindings package ([5798cd0](https://github.com/scafi/scafi3/commit/5798cd050b70d0efa81cb1e757b6011593981fb6))
* on API ergonomics ([e878a4d](https://github.com/scafi/scafi3/commit/e878a4d520050d18f9d96b14f8cbfff6ca104d9d))
* refactor, remove the concept of Language ([508c911](https://github.com/scafi/scafi3/commit/508c911da8e4709856d8d34b33f1e7318ef63dbb))

### Dependency updates

* **core-deps:** update dependency scala to v3.6.2 ([6148238](https://github.com/scafi/scafi3/commit/61482383436eda262917230aa642b3e689636605))
* **core-deps:** update dependency scala to v3.6.3 ([dc88c6a](https://github.com/scafi/scafi3/commit/dc88c6a59d7923fedd7a7e315bfda408bbe81b1b))
* **core-deps:** update dependency scala to v3.6.4 ([ac03de1](https://github.com/scafi/scafi3/commit/ac03de1f7f08b57f7327b11e0e6ff6a6adfd7cbc))
* **core-deps:** update dependency scala to v3.7.0 ([705a627](https://github.com/scafi/scafi3/commit/705a6272947a150a7ca3467fcb19faa59adf4260))
* **core-deps:** update dependency scala to v3.7.2 ([8dbdb0c](https://github.com/scafi/scafi3/commit/8dbdb0c0116b3e77247d565251cba12136d9dd25))
* **deps:** update alchemistversion to v41 ([e744b01](https://github.com/scafi/scafi3/commit/e744b019bbdcc1a4959eb90ccba836a7e3e78f23))
* **deps:** update alchemistversion to v42 ([e643f11](https://github.com/scafi/scafi3/commit/e643f110cbb37fe665e2e84efb1825a2dde5bbe7))
* **deps:** update alchemistversion to v42.0.1 ([097a816](https://github.com/scafi/scafi3/commit/097a8167076bf9fc995cc83514caa7c8e268b50d))
* **deps:** update alchemistversion to v42.0.5 ([7cffd83](https://github.com/scafi/scafi3/commit/7cffd83e391e9d381e48b9d83fa529a1ce0300ef))
* **deps:** update alchemistversion to v42.0.6 ([c53f114](https://github.com/scafi/scafi3/commit/c53f1147b466ae5cd135eee13bbac553acc6ce88))
* **deps:** update alchemistversion to v42.0.7 ([d099e61](https://github.com/scafi/scafi3/commit/d099e616edb73880f6b2173f03d2505a233960fa))
* **deps:** update alchemistversion to v42.0.8 ([cc9d6a7](https://github.com/scafi/scafi3/commit/cc9d6a72d4d238608c1d0a8ac3d13481f813a1da))
* **deps:** update alchemistversion to v42.0.9 ([9aa5c38](https://github.com/scafi/scafi3/commit/9aa5c38b24b20f94ab56b9857ab321f607d77932))
* **deps:** update alchemistversion to v42.1.0 ([831dbb1](https://github.com/scafi/scafi3/commit/831dbb1644424d971f3a2efa6f3300744e725d7c))
* **deps:** update dependency ch.epfl.scala:sbt-scalafix to v0.14.0 ([ffea202](https://github.com/scafi/scafi3/commit/ffea2020d7ed5171cd5189363890ff550fcde9ea))
* **deps:** update dependency ch.epfl.scala:sbt-scalafix to v0.14.2 ([0c26662](https://github.com/scafi/scafi3/commit/0c266629cd311fb2b02a8217b4275e2074608813))
* **deps:** update dependency ch.epfl.scala:sbt-scalafix to v0.14.3 ([68aab1d](https://github.com/scafi/scafi3/commit/68aab1da8f087c6565fafcef354e93f878e686e9))
* **deps:** update dependency com.github.sbt:sbt-ci-release to v1.11.2 ([a240e62](https://github.com/scafi/scafi3/commit/a240e62ebe7a93a12a03d574e5da9ec5c430b475))
* **deps:** update dependency com.github.sbt:sbt-ci-release to v1.9.2 ([74e8e23](https://github.com/scafi/scafi3/commit/74e8e230368ee98c00043346aa8e15f164e49686))
* **deps:** update dependency com.github.sbt:sbt-ci-release to v1.9.3 ([5c6f6be](https://github.com/scafi/scafi3/commit/5c6f6be9ecb3797dd5a7ddc0186c0c40b6432303))
* **deps:** update dependency com.github.sbt:sbt-unidoc to v0.6.0 ([b24f0e3](https://github.com/scafi/scafi3/commit/b24f0e36ff8add1bdab287a2fb7bc0c9567ab568))
* **deps:** update dependency io.github.iltotore:iron to v3.0.4 ([06e66ed](https://github.com/scafi/scafi3/commit/06e66ed3de0b71056533b0234912e504fee62e42))
* **deps:** update dependency io.github.iltotore:iron to v3.2.0 ([17c7601](https://github.com/scafi/scafi3/commit/17c7601d0f7f13beca25ab297790b8f1f5212d24))
* **deps:** update dependency org.scala-js:sbt-scalajs to v1.18.0 ([c2c2769](https://github.com/scafi/scafi3/commit/c2c27699f1b77e16e190b68d863c3cb434d0feba))
* **deps:** update dependency org.scala-js:sbt-scalajs to v1.18.1 ([5d70669](https://github.com/scafi/scafi3/commit/5d706691d426d784105338674ccf308d705570ee))
* **deps:** update dependency org.scala-js:sbt-scalajs to v1.18.2 ([2aebec9](https://github.com/scafi/scafi3/commit/2aebec9d1034fa09d9cfb09849f31ced3b3d16a1))
* **deps:** update dependency org.scala-js:sbt-scalajs to v1.19.0 ([1332808](https://github.com/scafi/scafi3/commit/13328086d09321ab4e4d421643389172f46f3fb6))
* **deps:** update dependency org.scala-js:sbt-scalajs to v1.20.0 ([aac08d8](https://github.com/scafi/scafi3/commit/aac08d8cd21779c35bc692fbe847b828e2873239))
* **deps:** update dependency org.scala-native:sbt-scala-native to v0.5.6 ([02a1737](https://github.com/scafi/scafi3/commit/02a1737b24054faa21f6339262cc7916805cd502))
* **deps:** update dependency org.scala-native:sbt-scala-native to v0.5.7 ([ce9a085](https://github.com/scafi/scafi3/commit/ce9a085d35c62c923cc2466dfdc3a45c809e44d6))
* **deps:** update dependency org.scala-native:sbt-scala-native to v0.5.8 ([373afb6](https://github.com/scafi/scafi3/commit/373afb6680b05f9fccbc31dfb6dc85379d3eb94c))
* **deps:** update dependency org.scalamock:scalamock to v7.3.2 ([0dd6c0c](https://github.com/scafi/scafi3/commit/0dd6c0c3f5008112eff425eb606e740cdb40bc66))
* **deps:** update dependency org.scoverage:sbt-scoverage to v2.3.0 ([df130a6](https://github.com/scafi/scafi3/commit/df130a631ac7d4fbda981e8159de5d340a38dd61))
* **deps:** update dependency org.scoverage:sbt-scoverage to v2.3.1 ([1b21648](https://github.com/scafi/scafi3/commit/1b21648c1d286df5c3a26cb646c4ebc2f8b13b51))
* **deps:** update dependency org.typelevel:cats-core to v2.13.0 ([da21f78](https://github.com/scafi/scafi3/commit/da21f78a11045a7905af42058ad57ba29fe76dfd))
* **deps:** update dependency sbt/sbt to v1.10.10 ([8ef2cee](https://github.com/scafi/scafi3/commit/8ef2ceed1a50c78cc197beca6c3c0eb060895aa4))
* **deps:** update dependency sbt/sbt to v1.10.11 ([4499957](https://github.com/scafi/scafi3/commit/44999578ea3751d772528fb8e827cb8e14486699))
* **deps:** update dependency sbt/sbt to v1.10.6 ([d4993a5](https://github.com/scafi/scafi3/commit/d4993a53ebef01c180b3ccd5c3ad12cba616082c))
* **deps:** update dependency sbt/sbt to v1.10.7 ([4822c9c](https://github.com/scafi/scafi3/commit/4822c9c28f0a344678e6b82bfa680730e2cf1670))
* **deps:** update dependency sbt/sbt to v1.11.0 ([396f542](https://github.com/scafi/scafi3/commit/396f5423fbd86d35c8195132abb6d04523416392))
* **deps:** update dependency sbt/sbt to v1.11.1 ([c2f0c90](https://github.com/scafi/scafi3/commit/c2f0c90fa94542fc51049d1dfd6770bb86b4ee85))
* **deps:** update dependency sbt/sbt to v1.11.2 ([4699ed3](https://github.com/scafi/scafi3/commit/4699ed3877937467b7388fe7c8c1671afd833be8))
* **deps:** update dependency sbt/sbt to v1.11.3 ([3d5aade](https://github.com/scafi/scafi3/commit/3d5aadeb0c60fb38609c0767eafb4ac88f3a51b8))
* **deps:** update dependency sbt/sbt to v1.11.4 ([665e370](https://github.com/scafi/scafi3/commit/665e37067b047a6d4c0df693169834c40278bd7a))
* **deps:** update dependency sbt/sbt to v1.11.5 ([240cfdd](https://github.com/scafi/scafi3/commit/240cfdd365106c9ebd6a4c6ff3128f7a0c456aa4))
* **deps:** update dependency scalafmt to v3.9.4 ([d571c9a](https://github.com/scafi/scafi3/commit/d571c9aee2c9596f6d17fe1ede6a051bf66b766a))
* **deps:** update dependency scalafmt to v3.9.5 ([e2ae975](https://github.com/scafi/scafi3/commit/e2ae975e2aa1138db9ec9fe369d63450f560b16e))
* **deps:** update dependency scalafmt to v3.9.6 ([5ae1f02](https://github.com/scafi/scafi3/commit/5ae1f02f230ee0cdb70ad8188580082ba0ec6fa7))
* **deps:** update dependency scalafmt to v3.9.7 ([303e187](https://github.com/scafi/scafi3/commit/303e187fe2568aa4173752dd966673822b5035bb))
* **deps:** update dependency scalafmt to v3.9.8 ([3cabc68](https://github.com/scafi/scafi3/commit/3cabc6855993fc1b4a72cb578432062c4dd99cdb))
* **deps:** update dependency scalafmt to v3.9.9 ([b79e571](https://github.com/scafi/scafi3/commit/b79e571457c5f8012a0407c6055eed1b630371b5))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.115 ([c1e2b88](https://github.com/scafi/scafi3/commit/c1e2b88fe957cb5973e8b68256e97d4f7ea5d7c7))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.116 ([64998fd](https://github.com/scafi/scafi3/commit/64998fd2d0655cfffce1392a07c05ce5eb2f3fff))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.117 ([2cb019c](https://github.com/scafi/scafi3/commit/2cb019c25c16913add58b78fbf2d38971d9af1d9))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.118 ([09bff54](https://github.com/scafi/scafi3/commit/09bff540fe375f1f0153b16a1cbd53f6f1b4fa25))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.119 ([91a1ff8](https://github.com/scafi/scafi3/commit/91a1ff8a73dd186988395682c24bdc3d373a7f39))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.120 ([b73c2f2](https://github.com/scafi/scafi3/commit/b73c2f2d266d863c02b4f819bf01c1ebee71965e))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.121 ([2f21868](https://github.com/scafi/scafi3/commit/2f21868a4c6d9ccb8aebbbb8d7c9f280deef5a1a))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.122 ([07226c1](https://github.com/scafi/scafi3/commit/07226c114976aecc04d8bf41412f80ce0c53a1c9))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.123 ([547f15b](https://github.com/scafi/scafi3/commit/547f15bbb6a7135574556bb918ca9a0b785bd8d3))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.124 ([dfe4128](https://github.com/scafi/scafi3/commit/dfe4128893bd1547de57e8d0e578d03e6684a92c))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.125 ([f0484fc](https://github.com/scafi/scafi3/commit/f0484fc68e9775fe40ddeed23139fd42961cfeb3))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.126 ([61c9397](https://github.com/scafi/scafi3/commit/61c939756ed7bfd32072df546cf28a01c0baa060))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.127 ([4d37560](https://github.com/scafi/scafi3/commit/4d37560947380209cd34f0c5236c26f455c93207))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.128 ([2136b40](https://github.com/scafi/scafi3/commit/2136b40031343b08c47f8095f84eb8953474ed83))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.129 ([d9fcc55](https://github.com/scafi/scafi3/commit/d9fcc55bd07eb9e24e5849b346e60a081b869c86))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.130 ([b3d61f1](https://github.com/scafi/scafi3/commit/b3d61f10a8bca622d7d6e00d1d5baf8abd91cc17))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.131 ([ad00dc8](https://github.com/scafi/scafi3/commit/ad00dc855d98e9e8db506dbdd1d3cf96b9581693))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.132 ([26089f5](https://github.com/scafi/scafi3/commit/26089f53b41111375242aff111b9d07c001f258b))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.133 ([be04db4](https://github.com/scafi/scafi3/commit/be04db4404ee2b485235197cd33deeb363fa9e97))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.134 ([4bbd372](https://github.com/scafi/scafi3/commit/4bbd3726f2b2a2291f35142abbc2564a50c6fcf2))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.135 ([dd29c71](https://github.com/scafi/scafi3/commit/dd29c71bae697b777e6157b051d66de7790fdd26))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.136 ([9c0df28](https://github.com/scafi/scafi3/commit/9c0df28526f8edd87c4d387643fed3f71bda0254))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.137 ([c30fb87](https://github.com/scafi/scafi3/commit/c30fb871d963dff032a44e200cdaea121142295d))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.138 ([b5081c6](https://github.com/scafi/scafi3/commit/b5081c6802e4f7f6bd12a8383289de00d78b3912))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.139 ([797334e](https://github.com/scafi/scafi3/commit/797334ed56205882f2644a7eb2b7ba84490f4cd5))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.140 ([719edfd](https://github.com/scafi/scafi3/commit/719edfd048468ac1776f9bf54898dabb037c9919))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.141 ([6769042](https://github.com/scafi/scafi3/commit/6769042bcb0c243a8e7127fa08c8468fa354d65f))
* **deps:** update node.js to 22.12 ([d5435fb](https://github.com/scafi/scafi3/commit/d5435fb001c4f2d0c33f4ca7df6f26f2cee3418b))
* **deps:** update node.js to 22.13 ([54c606c](https://github.com/scafi/scafi3/commit/54c606c023ff55f70ea276aab9ea328286a0ac05))
* **deps:** update node.js to 22.14 ([94c3aed](https://github.com/scafi/scafi3/commit/94c3aed73109fe0d18a46b2b144a5f65fca1d924))
* **deps:** update node.js to 22.15 ([e418ebf](https://github.com/scafi/scafi3/commit/e418ebffe21ab079324f354f21147f4372f8c2ca))
* **deps:** update node.js to 22.16 ([ade8514](https://github.com/scafi/scafi3/commit/ade85140cb68cac933361f9bf2ee6b2b7e810178))
* **deps:** update node.js to 22.17 ([5dd1817](https://github.com/scafi/scafi3/commit/5dd18174eb94d2561d76c84ab38b2f2866f65701))
* **deps:** update node.js to 22.18 ([d52ffc0](https://github.com/scafi/scafi3/commit/d52ffc0b9ea26f1508cbce38ff31b4e213868fe0))
* **deps:** update node.js to 22.19 ([e910bc8](https://github.com/scafi/scafi3/commit/e910bc8936900789f9b08f18e79b57811338931e))
* **deps:** update node.js to v22 ([0040b9f](https://github.com/scafi/scafi3/commit/0040b9ff91ead69a51cbacf507cd0ee427f619c5))

### Bug Fixes

* **test:** add binding AbstractExchangeCalculusContext ([e252637](https://github.com/scafi/scafi3/commit/e2526379f8dc638c8f540abaead23884c852c12f))
* use SharedData instead of AggregateValue ([d772c1f](https://github.com/scafi/scafi3/commit/d772c1f31090f2c1113293c6070a3d2f9cb74e8e))

### Documentation

* update readme ([708aa82](https://github.com/scafi/scafi3/commit/708aa829d2566fa230460ecd94fb8af709f559f3))

### Performance improvements

* aligned process optimized ([e595fa1](https://github.com/scafi/scafi3/commit/e595fa1a9c187c656655a9aea65c030c24936eeb))
* improve performance reading currentPath ([d12f6e4](https://github.com/scafi/scafi3/commit/d12f6e45da14bc6441e07b4a118be358bed2f249))
* improve the alignedValues performance ([6d62b53](https://github.com/scafi/scafi3/commit/6d62b53e27ced7721a9b09e3b99a9df2788e1c1e))

### Tests

* use new syntax ([2bfb86f](https://github.com/scafi/scafi3/commit/2bfb86f7dea9f60b7606b447dec7280767a94a98))

### Build and continuous integration

* **deps:** update actions/checkout action to v4.3.0 ([10f5dd5](https://github.com/scafi/scafi3/commit/10f5dd53c8f1ca696f3733b439f51cce874413c4))
* **deps:** update actions/checkout action to v5 ([e95cc07](https://github.com/scafi/scafi3/commit/e95cc0700065be913ebe56509fd87923590d9e4a))
* **deps:** update actions/setup-node action to v4.2.0 ([bedff46](https://github.com/scafi/scafi3/commit/bedff46bc5b63ee3f88e5f619ef65d652d8c0707))
* **deps:** update actions/setup-node action to v4.3.0 ([b3a9638](https://github.com/scafi/scafi3/commit/b3a9638afcfb7f4c9c8f259d108216e043137f7c))
* **deps:** update actions/setup-node action to v4.4.0 ([67c6331](https://github.com/scafi/scafi3/commit/67c633147a8746778c4fafdd7a85147f56832e54))
* **deps:** update actions/setup-node action to v5 ([15252f3](https://github.com/scafi/scafi3/commit/15252f3404e61571b9829bcaea6224ee2f4a5ace))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.16 ([623e732](https://github.com/scafi/scafi3/commit/623e732c26578a767695774040a3f2c61df3aec3))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.17 ([db89b8f](https://github.com/scafi/scafi3/commit/db89b8f94323fa8575180f3912c7a41f060e5e30))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.18 ([9089858](https://github.com/scafi/scafi3/commit/908985875696002ad489352fa1b53541a5a2e6b4))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.19 ([ef70e4d](https://github.com/scafi/scafi3/commit/ef70e4d478064cd7347f2a2be27ef31b627c972b))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.20 ([e6c545b](https://github.com/scafi/scafi3/commit/e6c545bb735ccf83e95f6f1ffb20b44d394f575d))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.21 ([ee13988](https://github.com/scafi/scafi3/commit/ee13988b43a41bac335d0ebe3778dee24d4e1652))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.22 ([65f13ef](https://github.com/scafi/scafi3/commit/65f13ef8e64f95c25c4048dfff7dd71613058fb3))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.23 ([db73be8](https://github.com/scafi/scafi3/commit/db73be8c21273533c0acd76c53ce779820ebc4b6))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.24 ([81db409](https://github.com/scafi/scafi3/commit/81db409af5bbd4eb94ab810e80c41d95e478cd99))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.25 ([51882a3](https://github.com/scafi/scafi3/commit/51882a33479e78e6a63903ab3d4ab27dbfa1905d))
* **deps:** update nicolasfara/build-check-deploy-sbt-action action to v1.0.27 ([34ec396](https://github.com/scafi/scafi3/commit/34ec396d6936acfdf86fe86e855717f75243a339))
* re-enable scala multiplaform build (native + js) ([51cd6db](https://github.com/scafi/scafi3/commit/51cd6dbace337c01f5acffd9ca87eb088481d1e3))
* remove sonatype parameters as it is deprecated ([100d29a](https://github.com/scafi/scafi3/commit/100d29a2fe330bd0a9f4012a8f3d6bd1adc64e38))
* remove sonatype parameters as it is deprecated ([d610bd9](https://github.com/scafi/scafi3/commit/d610bd9a1b543b3bde2b13f6453c668b38215b23))
* setup project based on `scafi-xc` ([#5](https://github.com/scafi/scafi3/issues/5)) ([f7815fa](https://github.com/scafi/scafi3/commit/f7815fad9732d717b85382bab24f068afe12bd6c))
* switch to windows-2025 and ubuntu-24.04 runners ([#98](https://github.com/scafi/scafi3/issues/98)) ([de82a1e](https://github.com/scafi/scafi3/commit/de82a1e6dfdc78b4ad20ddb218e9a56389111aa3))
* update actions ([74eee46](https://github.com/scafi/scafi3/commit/74eee4608b1136fdbd506c951043ce3d08939618))
* update build-check-deploy-sbt-actions ([9333eef](https://github.com/scafi/scafi3/commit/9333eefd9d916352a7d78edb8112ab614b36a61e))
* use new syntax mjs ([6dc3bdd](https://github.com/scafi/scafi3/commit/6dc3bdd00019fca08a7d2de8c9070112eadf873c))

### General maintenance

* add coverage badge ([41bd56b](https://github.com/scafi/scafi3/commit/41bd56b8ad3cebc866205b97f845fc92f9151000))
* add intro to field4s ([d4595fd](https://github.com/scafi/scafi3/commit/d4595fd678d236c824ad4cf18c0d461485094cb6))
* add readme ([d993fc4](https://github.com/scafi/scafi3/commit/d993fc464fe620e174e195d74d06c0fc88a23b3d))
* emphasis on scala 3 ([17c5a94](https://github.com/scafi/scafi3/commit/17c5a9495d6d2ec3c9208ad0eb11f39b76d744ed))
* **readme:** better badges positioning ([8c2b265](https://github.com/scafi/scafi3/commit/8c2b265572eb6530c9c4d767494a79c7202791de))
* remove class files ([ddde974](https://github.com/scafi/scafi3/commit/ddde974faa00fbe8cbc43b482c79dffcb7d94086))
* update renovate config ([340db01](https://github.com/scafi/scafi3/commit/340db01a7d2532ca0c06b1a3dbb2356b707d2592))

### Style improvements

* add comments ([80e5710](https://github.com/scafi/scafi3/commit/80e57102a48847e47296d9d4e15436da7fff6159))
* forbid wildcard imports, merging them whenever possible ([#119](https://github.com/scafi/scafi3/issues/119)) ([38ba90f](https://github.com/scafi/scafi3/commit/38ba90f8b5a535c54b9477d42c586707a6877bdc))
* reformat style ([ec9862b](https://github.com/scafi/scafi3/commit/ec9862bbc15b96d645d5cf978a0c5457b8dc618b))
* run scalafmt ([137a78d](https://github.com/scafi/scafi3/commit/137a78d53de40a28e1d789219b48153bd82919e3))
* sort imports ([8ab1c60](https://github.com/scafi/scafi3/commit/8ab1c6010aac4964dcfee979b8acd4c031a32922))
* update scalafmt version ([aa55bda](https://github.com/scafi/scafi3/commit/aa55bdade394f4d883dd3f05c0f60538ff9fe58a))

### Refactoring

* avoid deprecated methods ([e76b982](https://github.com/scafi/scafi3/commit/e76b9826e3b1f436b4a286ba1f846546472af73e))
* better name of aggregate ([35b2c99](https://github.com/scafi/scafi3/commit/35b2c99ac33bd8ed3e2939390bb3b3611b169839))
* better names ([0572d78](https://github.com/scafi/scafi3/commit/0572d78e09e4997b091ed45a0035f76a8e2e5173))
* change engine structure and deeply refactor trait structures and package ([e6742f1](https://github.com/scafi/scafi3/commit/e6742f177a9ae534ae0dbafe8c8df231bc66f604))
* improve readability ([c497010](https://github.com/scafi/scafi3/commit/c497010a82a92c680f8a98b9b5e2f2acb9680ac0))
* improving names ([0efcb12](https://github.com/scafi/scafi3/commit/0efcb12fd8eb233d3d9322b396fa70d7ad43e9e3))
* more on style ([f006ce7](https://github.com/scafi/scafi3/commit/f006ce71f621d89c8b99ffa6d534f5193519d6b3))
* move `CanEqual` implicit in trait and suppress misleading unused warnings in type bounds ([#120](https://github.com/scafi/scafi3/issues/120)) ([b13bc39](https://github.com/scafi/scafi3/commit/b13bc39aec8c949f3e8af584eecba1cefd1d267a))
* move FieldBasedSharedData to langugage.exchange ([b614d8f](https://github.com/scafi/scafi3/commit/b614d8f516ea58551f9b22c3b6dd92fdf1e6c847))
* remove `DeviceAwareAggregateFoundation` trait ([7b0a9cd](https://github.com/scafi/scafi3/commit/7b0a9cd2f0c0209f4021bb5945e5e765bc720bef))
* remove foundation ([2a8f479](https://github.com/scafi/scafi3/commit/2a8f4798e6d60b3a73a3285070edf35038637280))
* remove Mappable and use Functor from cats ([18bae2a](https://github.com/scafi/scafi3/commit/18bae2a2b095afd9b953e8e8a66018bd6be6b14b))
* solve deprecated abstract given ([b09b054](https://github.com/scafi/scafi3/commit/b09b054459fc7b5a6957fc9bf147908759b69e75))
* update the package exchange as first level ([b2bb0af](https://github.com/scafi/scafi3/commit/b2bb0af44c7ce335d09e3926071ca1abdb8de96a))
* update to alchemist 40 ([c63f3c2](https://github.com/scafi/scafi3/commit/c63f3c23f7207b70c96eae53fd5e14f221a3288b))
* use Applicative instead Liftable ([04d358b](https://github.com/scafi/scafi3/commit/04d358bf114c89c0bffe58ed2605860956c31e08))
* use scafi package name ([ec9ecb2](https://github.com/scafi/scafi3/commit/ec9ecb2d155c58fc1d598b013d7eb757b1f1169a))
