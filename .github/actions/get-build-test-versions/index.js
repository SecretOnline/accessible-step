import { info, setOutput } from "@actions/core";
import { readFile } from "node:fs/promises";
import { join } from "node:path";
import { satisfies } from "semver";
import { getAllMinecraftVersions, getMinecraftVersion } from "../lib/mojang.js";
import { addAllZeroVersions, parseVersionSafe } from "../lib/versions.js";

const fileString = await readFile(
  join(process.cwd(), "src/main/resources", "fabric.mod.json"),
  { encoding: "utf8" }
);
const modJson = JSON.parse(fileString);
const recommendsRange = addAllZeroVersions(modJson.recommends.minecraft);

const versionsManifest = await getAllMinecraftVersions();
const allReleaseVersions = versionsManifest.versions.filter(
  (v) => v.type === "release"
);

const matchingVersions = allReleaseVersions.filter((version) =>
  satisfies(parseVersionSafe(version.id), recommendsRange)
);

info(
  `Found ${
    matchingVersions.length
  } versions matching range \`${recommendsRange}\`: ${matchingVersions
    .map((v) => v.id)
    .join(", ")}`
);

const versionEntries = await Promise.all(
  matchingVersions.map(async (version) => {
    const versionData = await getMinecraftVersion(version.id);

    /** @type {TestMatrixEntry} */
    const entry = {
      name: versionData.id,
      "minecraft-version": versionData.id,
      "java-version": versionData.javaVersion.majorVersion.toString(),
    };

    return entry;
  })
);

setOutput("test-matrix", JSON.stringify(versionEntries));

/**
 * @typedef TestMatrixEntry
 * @property {string} name
 * @property {string} minecraft-version
 * @property {string} java-version
 */
