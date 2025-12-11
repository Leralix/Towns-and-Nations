-- Redis Lua script for atomic broadcast + multi-key invalidation
-- Guarantees that Pub/Sub message and all cache deletions succeed together
-- Use case: Territory upgrade → invalidate town + transaction history + broadcast
--
-- KEYS[1]: Redis Hash name (e.g., "tan:query_cache")
-- KEYS[2]: Redis Pub/Sub channel (e.g., "tan:sync:cache_invalidation")
-- ARGV[1]: Pub/Sub message (JSON sync message)
-- ARGV[2..N]: Hash fields to delete (cache keys)
--
-- Returns: Number of fields deleted

-- Step 1: Delete all cache entries atomically
local deleted = 0
if #ARGV > 1 then
    local keysToDelete = {}
    for i = 2, #ARGV do
        table.insert(keysToDelete, ARGV[i])
    end
    deleted = redis.call('HDEL', KEYS[1], unpack(keysToDelete))
end

-- Step 2: Publish sync message
redis.call('PUBLISH', KEYS[2], ARGV[1])

-- Step 3: Return deletion count
return deleted
