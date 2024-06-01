/**
 * @template T
 * @param {() => T} fn
 * @return {() => T}
 */
export function cache(fn) {
  let didCache = false;
  /** @type {T} */
  let cache;

  /**
   * @returns {T}
   */
  function cachedFunction() {
    if (!didCache) {
      cache = fn();
      didCache = true;
    }

    return cache;
  }
  return cachedFunction;
}

/**
 * @template K,T
 * @param {(key: K) => T} fn
 * @return {(key: K) => T}
 */
export function cacheWithKey(fn) {
  /** @type {Map<K, T>} */
  const cache = new Map();

  /**
   * @param {K} key
   * @returns {T}
   */
  function cachedFunctionWithKey(key) {
    if (!cache.has(key)) {
      cache.set(key, fn(key));
    }

    // @ts-expect-error Map.get includes undefined. We checked for it earlier, so it's fine.
    return cache.get(key);
  }
  return cachedFunctionWithKey;
}
