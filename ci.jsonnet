# Common
local common = import 'ci/ci_common/common.jsonnet';
local graal_common = import 'graal-common.json';

# Compiler
local compiler = import 'compiler/ci/ci.jsonnet';

# GraalWasm
local wasm = import 'wasm/ci/ci.jsonnet';

# Espresso
local espresso = import 'espresso/ci/ci.jsonnet';

# Regex
local regex = import 'regex/ci/ci.jsonnet';

# SDK
local sdk = import 'sdk/ci/ci.jsonnet';

# SubstrateVM
local substratevm = import 'substratevm/ci/ci.jsonnet';

# Sulong
local sulong = import 'sulong/ci/ci.jsonnet';

# Tools
local tools = import 'tools/ci/ci.jsonnet';

# Truffle
local truffle = import 'truffle/ci/ci.jsonnet';

# JavaDoc
local javadoc = import "ci_includes/publish-javadoc.jsonnet";

# VM
local vm = import 'vm/ci/ci_includes/vm.jsonnet';

# Visualizer
local visualizer = import 'visualizer/ci/ci.jsonnet';

local verify_ci = (import 'ci/ci_common/ci-check.libsonnet').verify_ci;

# JDK latest only works on MacOS Ventura (GR-49652)
local exclude_latest_darwin_amd64(builds) = [b for b in builds if !(import 'ci/ci_common/common-utils.libsonnet').contains(b.name, "labsjdk-latest-darwin-amd64")];

{
  # Ensure that non-hidden entries in ci/common.jsonnet and ci/ci_common/common.jsonnet can be resolved.
  assert std.length(std.toString(import 'ci/ci_common/common.jsonnet')) > 0,
  ci_resources:: (import 'ci/ci_common/ci-resources.libsonnet'),
  overlay: graal_common.ci.overlay,
  specVersion: "3",
  builds: exclude_latest_darwin_amd64([common.add_excludes_guard(b) for b in (
    common.with_tags_for_gates(compiler.builds, ["compiler"]) +
    common.with_tags_for_gates(wasm.builds, ["wasm"]) +
    common.with_tags_for_gates(espresso.builds, ["espresso"]) +
    common.with_tags_for_gates(regex.builds, ["regex"]) +
    common.with_tags_for_gates(sdk.builds, ["sdk"]) +
    common.with_tags_for_gates(substratevm.builds, ["svm"]) +
    common.with_tags_for_gates(sulong.builds, ["sulong"]) +
    common.with_tags_for_gates(tools.builds, ["tools"]) +
    common.with_tags_for_gates(truffle.builds, ["truffle"]) +
    common.with_tags_for_gates(javadoc.builds, ["javadoc"]) +
    common.with_tags_for_gates(vm.builds, ["vm"]) +
    common.with_tags_for_gates(visualizer.builds, ["visualizer"])
  )]),
  assert verify_ci(self.builds),
  // verify that the run-spec demo works
  assert (import "ci/ci_common/run-spec-demo.jsonnet").check(),
  // ensure that the galahad CI configuration does not break
  assert std.type(std.manifestJson((import "galahad.jsonnet").builds)) == "string"
}
