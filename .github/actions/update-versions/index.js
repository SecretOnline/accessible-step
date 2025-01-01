import { getInput, info, setOutput, warning } from "@actions/core";
import { readFile } from "node:fs/promises";
import { join } from "node:path";
import { URL, URLSearchParams } from "node:url";
import { minSatisfying, satisfies } from "semver";
import { getAllMinecraftVersions, getMinecraftVersion } from "../lib/mojang.js";
import {
  addAllZeroVersions,
  parseVersionSafe,
  trimAllZeroVersions,
} from "../lib/versions.js";

const fileString = await readFile(
  join(process.cwd(), "src/client/resources", "fabric.mod.json"),
  { encoding: "utf8" }
);
const modJson = JSON.parse(fileString);
const recommendsRange = addAllZeroVersions(modJson.recommends.minecraft);

const allVersions = await getAllMinecraftVersions();

/**
 * @returns {string}
 */
function getUpdateVersion() {
  const inputValue = getInput("minecraft-version");
  if (inputValue) {
    return inputValue;
  }

  info("No version specified, getting latest");
  info(`Latest version is ${allVersions.latest.release}`);
  return allVersions.latest.release;
}

/**
 * @returns {string | null}
 */
function getMaxUpdateVersion() {
  const inputValue = getInput("max-minecraft-version");
  if (inputValue) {
    return inputValue;
  }

  return null;
}

const versionToUpdate = getUpdateVersion();
const updateVersionInfo = await getMinecraftVersion(versionToUpdate);

async function getNewVersionRange() {
  if (updateVersionInfo.type !== "release") {
    return versionToUpdate;
  }

  const updateSemver = parseVersionSafe(versionToUpdate);

  if (recommendsRange === updateSemver.toString()) {
    return versionToUpdate;
  }

  if (satisfies(updateSemver, recommendsRange)) {
    // New version already satisfies the current range, but double check Java version first.
    const allReleaseVersions = allVersions.versions.filter(
      (v) => v.type === "release"
    );
    const minMatchingSemver = minSatisfying(
      allReleaseVersions.map((v) => parseVersionSafe(v.id)),
      recommendsRange
    );
    if (!minMatchingSemver) {
      throw new Error(`No versions matched range ${recommendsRange}`);
    }

    const minMatchingInfo = await getMinecraftVersion(
      minMatchingSemver.toString()
    );

    const isSameJava =
      updateVersionInfo.javaVersion.majorVersion ===
      minMatchingInfo.javaVersion.majorVersion;
    if (isSameJava) {
      return recommendsRange;
    }

    // New version satisfies range, but has new Java version so is incompatible.
  }

  const minVersion = versionToUpdate;
  const maxUpdateVersion = getMaxUpdateVersion();
  // Current rules mean that the minor version is always the same, so we can use a range here
  // Also we're assuming middle versions are compatible. This should always be the case, but
  // I can't wait to eat my words on that one.
  const simplified = trimAllZeroVersions(
    maxUpdateVersion !== null
      ? `${minVersion} - ${maxUpdateVersion}`
      : `~${minVersion}`
  );

  info(
    `New version is likely compatible with existing versions of Minecraft. Adding to version range ${simplified}`
  );

  return simplified;
}

/**
 * @param {string} projectId
 * @returns {Promise<string>}
 */
async function getModrinthProjectVersion(projectId) {
  const url = new URL(
    `https://api.modrinth.com/v2/project/${projectId}/version`
  );
  url.search = new URLSearchParams({
    game_versions: `["${versionToUpdate}"]`,
  }).toString();

  const response = await fetch(url, {
    headers: {
      "user-agent": "secret_online/mod-auto-updater (mc@secretonline.co)",
    },
  });
  const data = await response.json();

  if (data.length === 0) {
    const shouldIgnoreModDependencies =
      getInput("ignore-mod-dependencies") === "true";
    if (!shouldIgnoreModDependencies) {
      throw new Error(
        `No versions of ${projectId} for Minecraft ${versionToUpdate}`
      );
    }

    warning(
      `No versions of ${projectId} for Minecraft ${versionToUpdate}. Ignoring, but you may need to revert some changes until I update this action`
    );
    return "";
  }

  const newVersion = data[0].version_number;
  info(`Found ${projectId}: ${newVersion}`);

  return newVersion;
}

/**
 * @returns {Promise<string>}
 */
async function getYarnMappingsVersion() {
  const response = await fetch("https://meta.fabricmc.net/v2/versions/yarn", {
    headers: {
      "user-agent": "secret_online/mod-auto-updater (mc@secretonline.co)",
    },
  });
  /** @type {any[]} */
  const data = await response.json();
  const entriesForVersion = data.filter(
    (v) => v.gameVersion === versionToUpdate
  );

  if (entriesForVersion.length === 0) {
    throw new Error(
      `No versions of Yarn mappings for Minecraft ${versionToUpdate}`
    );
  }

  info(`Found Yarn mappings: ${entriesForVersion[0].version}`);

  return entriesForVersion[0].version;
}

/**
 * @returns {Promise<string>}
 */
async function getFabricLoaderVersion() {
  const response = await fetch("https://meta.fabricmc.net/v2/versions/loader", {
    headers: {
      "user-agent": "secret_online/mod-auto-updater (mc@secretonline.co)",
    },
  });
  /** @type {any[]} */
  const data = await response.json();

  if (data.length === 0) {
    throw new Error(`No versions of Fabric loader`);
  }

  info(`Found Fabric loader: ${data[0].version}`);

  return data[0].version;
}

// Early exit if latest version already matches
const newVersionRange = await getNewVersionRange();
const fabricApiVersion = await getModrinthProjectVersion("fabric-api");
const modMenuVersion = await getModrinthProjectVersion("modmenu");
const yarnMappingsVersion = await getYarnMappingsVersion();
const fabricLoaderVersion = await getFabricLoaderVersion();

setOutput("has-updates", true);
setOutput("minecraft-version", versionToUpdate);
setOutput("minecraft-version-range", newVersionRange);
setOutput("java-version", updateVersionInfo.javaVersion.majorVersion);
setOutput("yarn-mappings-version", yarnMappingsVersion);
setOutput("fabric-api-version", fabricApiVersion);
setOutput("mod-menu-version", modMenuVersion);
setOutput("loader-version", fabricLoaderVersion);
// Do not add code below the above closing brace, as it will run whether or not any updates happened.
// Control flow is hard.
