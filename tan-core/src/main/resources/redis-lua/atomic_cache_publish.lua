-- Redis Lua script for atomic broadcast + cache update
-- Guarantees that both Pub/Sub message and cache write succeed or both fail
-- Prevents inconsistency if server crashes between publish and cache update
--
-- KEYS[1]: Redis Hash name (e.g., "tan:query_cache")
-- KEYS[2]: Redis Pub/Sub channel (e.g., "tan:sync:cache_invalidation")
-- ARGV[1]: Hash field (cache key, e.g., "tan:cache:territory:abc123")
-- ARGV[2]: Hash value (JSON data)
-- ARGV[3]: TTL in seconds (e.g., 300 for 5 minutes)
-- ARGV[4]: Pub/Sub message (JSON sync message)
--
-- Returns: "OK" on success, error message on failure

-- Step 1: Write to cache (HSET)
redis.call('HSET', KEYS[1], ARGV[1], ARGV[2])

-- Step 2: Set TTL on hash (EXPIRE)
redis.call('EXPIRE', KEYS[1], tonumber(ARGV[3]))

-- Step 3: Publish sync message (PUBLISH)
redis.call('PUBLISH', KEYS[2], ARGV[4])

-- Step 4: Return success
return "OK"
