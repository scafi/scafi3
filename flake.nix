{
  description = "Scala Multiplatform project (Native, JS, JVM)";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };
      in
      {
        devShells.default = pkgs.mkShell {
          buildInputs = with pkgs; [
            jdk21
            sbt
            nodejs

            # Scala Native dependencies
            clang
            zlib
            boehmgc
            libunwind
            re2
            llvmPackages.libclang
          ];

          shellHook = ''
            export CXX=clang++
            export CC=clang
            export CPATH="${pkgs.boehmgc.dev}/include:${pkgs.re2.dev}/include:${pkgs.zlib.dev}/include:${pkgs.libunwind.dev}/include"
            export LIBRARY_PATH="${pkgs.boehmgc}/lib:${pkgs.re2}/lib:${pkgs.zlib}/lib:${pkgs.libunwind}/lib"
            export CLANG_PATH="${pkgs.clang}/bin/clang"
            export LIBCLANG_PATH="${pkgs.llvmPackages.libclang.lib}/lib"
            export LLVM_BIN="${pkgs.clang}/bin"
            
            # Needed for node packages
            export PATH="$PWD/node_modules/.bin:$PATH"
          '';
        };
      }
    );
}
