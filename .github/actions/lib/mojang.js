import { cache, cacheWithKey } from "./cache.js";

/**
 * @typedef AllVersionsManifest
 * @property {{release:string;snapshot:string}} latest
 * @property {AllVersionsManifestVersion[]} versions
 */
/**
 * @typedef AllVersionsManifestVersion
 * @property {string} id
 * @property {'release'|'snapshot'} type
 * @property {string} url
 * @property {string} time
 * @property {string} releaseTime
 * @property {string} sha1
 * @property {number} complianceLevel
 */

export const getAllMinecraftVersions = cache(
  /**
   * @returns {Promise<AllVersionsManifest>}
   */
  async function getAllMinecraftVersions() {
    const response = await fetch(
      "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json",
      {
        headers: {
          "user-agent": "secret_online/mod-auto-updater (mc@secretonline.co)",
        },
      }
    );
    const data = await response.json();
    return data;
  }
);

/**
 * @typedef VersionManifest
 * @property {string} id
 * @property {'release'|'snapshot'} type
 * @property {{component:string;majorVersion:number}} javaVersion
 */

export const getMinecraftVersion = cacheWithKey(
  /**
   * @param {string} id
   * @returns {Promise<VersionManifest>}
   */
  async function getMinecraftVersion(id) {
    // Strip patch version if patch version is 0
    const zeroMatch = id.match(/^(\d+\.\d+)\.0$/);
    if (zeroMatch) {
      id = zeroMatch[1];
    }

    const allVersions = await getAllMinecraftVersions();

    const version = allVersions.versions.find((v) => v.id === id);
    if (!version) {
      throw new Error(`Could not find Minecraft version ${id}`);
    }

    const response = await fetch(version.url, {
      headers: {
        "user-agent": "secret_online/mod-auto-updater (mc@secretonline.co)",
      },
    });
    const data = await response.json();
    return data;
  }
);
