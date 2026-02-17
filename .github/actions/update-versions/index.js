import { getInput, info, setOutput, warning } from "@actions/core";
import { URL, URLSearchParams } from "node:url";
import { compare, SemVer } from "semver";
import { getAllMinecraftVersions, getMinecraftVersion } from "../lib/mojang.js";
import { parseVersionSafe, trimAllZeroVersions } from "../lib/versions.js";

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

const versionToUpdate = getUpdateVersion();
const versionToUpdateSemver = new SemVer(versionToUpdate);
const updateVersionInfo = await getMinecraftVersion(versionToUpdate);

/**
 * @param {string} projectId
 * @returns {Promise<string>}
 */
async function getModrinthProjectVersion(projectId) {
  const url = new URL(
    `https://api.modrinth.com/v2/project/${projectId}/version`,
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
        `No versions of ${projectId} for Minecraft ${versionToUpdate}`,
      );
    }

    warning(
      `No versions of ${projectId} for Minecraft ${versionToUpdate}. Ignoring, but you may need to revert some changes until I update this action`,
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

/**
 * @returns {Promise<{neoforge:string;}>}
 */
async function getArchitecturyVersions() {
  const response = await fetch(
    "https://generate.architectury.dev/version_index.json",
    {
      headers: {
        "user-agent": "secret_online/mod-auto-updater (mc@secretonline.co)",
      },
    },
  );
  /** @type {Record<string,any>} */
  const data = await response.json();

  const versionData = data[versionToUpdate];
  if (versionData) {
    if (!versionData.neoforge) {
      throw new Error(`No Neoforge version in Architectury index`);
    }

    info(`Found Architectury data: neoforge ${versionData.neoforge}`);

    return versionData;
  }

  warning(
    `No version for ${versionToUpdate} in Architectury index. Trying Neoforge release`,
  );

  // Get latest Neoforge version from Neoforge Maven
  const neoforgeResponse = await fetch(
    "https://maven.neoforged.net/api/maven/versions/releases/net%2Fneoforged%2Fneoforge",
    {
      headers: {
        "user-agent": "secret_online/mod-auto-updater (mc@secretonline.co)",
      },
    },
  );
  /** @type {{versions:string[]}} */
  const neoforgeData = await neoforgeResponse.json();
  const matchRegex = new RegExp(
    `^${versionToUpdateSemver.minor}.${versionToUpdateSemver.patch}.`,
  );
  const matchingVersions = neoforgeData.versions.filter((version) =>
    matchRegex.test(version),
  );
  if (matchingVersions.length === 0) {
    throw new Error(`No version for ${versionToUpdate} in Neoforge Maven`);
  }
  matchingVersions.sort((a, b) => compare(b, a));
  const neoforgeVersion = matchingVersions[0];
  info(`Found Neoforge data: neoforge ${neoforgeVersion}`);

  return {
    neoforge: neoforgeVersion,
  };
}

const fabricApiVersion = await getModrinthProjectVersion("fabric-api");
const fabricLoaderVersion = await getFabricLoaderVersion();
const { neoforge } = await getArchitecturyVersions();

setOutput("has-updates", true);
setOutput("minecraft-version", versionToUpdate);
setOutput("java-version", updateVersionInfo.javaVersion.majorVersion);
setOutput("fabric-api-version", fabricApiVersion);
setOutput("fabric-loader-version", fabricLoaderVersion);
setOutput("neoforge-version", neoforge);
