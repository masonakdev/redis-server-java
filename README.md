# Redis-Compatible Server in Java

---

## Overview

This project is a Redis-compatible server written in Java that uses the RESP2 protocol. It's built from scratch to understand how Redis works under the hood. My goal is to learn by building. I started with the basics of handling TCP connections and simple commands, and have been gradually adding more advanced features.<br>

> **Note:** This project is being actively developed in a private workspace. Updates will be periodically pushed here until the repository reaches a “final” version that I am happy with.

---

## Current Functionality

#### Core Server
- Bind to a TCP port
- Handle concurrent clients
- Respond to `PING` (single and multiple requests)
- Implement the `ECHO` command
- Implement the `SET` and `GET` commands
- Implement key expiry logic
- Implement the `TYPE` command

#### Lists
- Create a list
- Append elements (single/multiple)
- Prepend elements (single/multiple)
- Query list elements (positive & negative indexes)
- Query list length (`LLEN`)
- Remove single or multiple elements
- Blocking retrieval (`BLPOP`) with indefinite and defined timeout support

#### Streams
- Create a stream
- Validate entry IDs
- Generate partial and fully auto-generated IDs
- Query entries (`XRANGE`)
- Query with `-` / `+` range specifiers
- Read from single streams using `XREAD`

### Supported Commands
`BLPOP`, `ECHO`, `GET`, `LLEN`, `LPOP`, `LPUSH`, `LRANGE`, `PING`, `RPUSH`, `SET`, `TYPE`, `XADD`, `XRANGE`, `XREAD`

---

## Planned Functionality

I plan to implement the following Redis features.

### Stream Enhancements
- Query multiple streams using `XREAD`
- Blocking reads with and without timeouts
- Blocking reads using `$`

### Transactions
- `INCR`, `MULTI`, `EXEC`, `DISCARD` commands
- Command queueing and execution
- Handling empty transactions and failures
- Support for multiple concurrent transactions

### Replication
- Configurable listening ports
- Implement `INFO` command (master and replica)
- Initial replication ID and offset
- Full handshake process (send/receive stages)
- Empty RDB transfer
- Single and multi-replica propagation
- Command processing and acknowledgment handling
- `WAIT` command with various replication states

### Persistence
- RDB file configuration and loading
- Read keys and values (single/multiple)
- Handle all values with expiry

### Pub/Sub
- Subscribe to one or more channels
- Enter and manage subscribed mode
- Support `PING` during subscribed mode
- Publish and deliver messages
- Unsubscribe cleanly

### Sorted Sets
- Create and manage sorted sets
- Add members and retrieve ranks
- List members (`ZRANGE`, including negative indexes)
- Count and remove members
- Retrieve member scores

### Geospatial Commands
- Implement `GEOADD` and coordinate validation
- Store and calculate location data
- Respond to `GEOPOS`
- Decode coordinates and compute distances
- Search locations within a radius

---

**Author:** Mason Wilcox<br>
**License:** MIT