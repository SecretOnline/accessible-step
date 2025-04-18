import { getInput, info, setOutput, warning } from "@actions/core";
import { URL, URLSearchParams } from "node:url";
import { getAllMinecraftVersions, getMinecraftVersion } from "../lib/mojang.js";

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
const updateVersionInfo = await getMinecraftVersion(versionToUpdate);

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

/**
 * @returns {Promise<{neoforge:string;neoforge_yarn_patch	:string}>}
 */
async function getArchitecturyVersions() {
  const response = await fetch(
    "https://generate.architectury.dev/version_index.json",
    {
      headers: {
        "user-agent": "secret_online/mod-auto-updater (mc@secretonline.co)",
      },
    }
  );
  /** @type {Record<string,any>} */
  const data = await response.json();

  const versionData = data[versionToUpdate];
  if (!versionData) {
    throw new Error(`No version for ${versionToUpdate} in Architectury index`);
  }

  if (!versionData.neoforge) {
    throw new Error(`No Neoforge version in Architectury index`);
  }

  info(
    `Found Architectury data: neoforge ${versionData.neoforge} yarnpatch ${versionData.neoforge_yarn_patch}`
  );

  return versionData;
}

const fabricApiVersion = await getModrinthProjectVersion("fabric-api");
const modMenuVersion = await getModrinthProjectVersion("modmenu");
const yarnMappingsVersion = await getYarnMappingsVersion();
const fabricLoaderVersion = await getFabricLoaderVersion();
const { neoforge, neoforge_yarn_patch } = await getArchitecturyVersions();

setOutput("has-updates", true);
setOutput("minecraft-version", versionToUpdate);
setOutput("java-version", updateVersionInfo.javaVersion.majorVersion);
setOutput("yarn-mappings-version", yarnMappingsVersion);
setOutput("fabric-api-version", fabricApiVersion);
setOutput("mod-menu-version", modMenuVersion);
setOutput("fabric-loader-version", fabricLoaderVersion);
setOutput("neoforge-version", neoforge);
setOutput("neoforge-yarn-patch-version", neoforge_yarn_patch);
