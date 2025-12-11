-- Redis Lua script for atomic multi-key cache invalidation
-- Guarantees that all cache entries are deleted atomically
-- Prevents partial invalidation if connection drops mid-operation
--
-- KEYS[1]: Redis Hash name (e.g., "tan:query_cache")
-- ARGV[1..N]: Hash fields to delete (cache keys)
--
-- Returns: Number of fields deleted

-- Delete all fields atomically
local deleted = redis.call('HDEL', KEYS[1], unpack(ARGV))

-- Return count
return deleted
