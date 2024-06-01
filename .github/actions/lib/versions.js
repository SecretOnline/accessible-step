import { parse } from "semver";

/**
 * @param {string} version
 * @returns {import('semver').SemVer}
 */
export function parseVersionSafe(version) {
  const zeroMatch = version.match(/^(\d+\.\d+)$/);
  if (zeroMatch) {
    version = `${version}.0`;
  }

  const result = parse(version);
  if (!result) {
    throw new Error(`Version ${version} is not valid semver`);
  }

  return result;
}

/**
 * @param {string} str
 * @returns {string}
 */
export function trimAllZeroVersions(str) {
  return str.replaceAll(/(?<=^|[^.\d])(\d+\.\d+)\.0(?=$|[^.\d])/g, "$1");
}

/**
 * @param {string} str
 * @returns {string}
 */
export function addAllZeroVersions(str) {
  return str.replaceAll(/(?<=^|[^.\d])(\d+\.\d+)(?=$|[^.\d])/g, "$1.0");
}
