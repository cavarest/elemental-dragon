# Docker Architecture Decision: Command vs Dockerfile

## Current Issue
We have the startup command in `docker-compose.yml` instead of the `Dockerfile`. This creates several problems:

1. **Inconsistency**: Same image runs differently depending on how it's started
2. **Maintenance**: Commands duplicated across environments
3. **Portability**: Image isn't self-contained
4. **Security**: Easy to accidentally override critical startup logic

## Best Practice Analysis

### Approach 1: Command in docker-compose.yml ✅
**When to use:**
- Development environments with frequent configuration changes
- Multi-environment deployments (dev/staging/prod) with different needs
- Testing different configurations without rebuilding

**Pros:**
- Flexible - easy to change without rebuilding image
- Good for development
- Allows environment-specific overrides

**Cons:**
- Inconsistent behavior across environments
- Not portable
- Maintenance overhead

### Approach 2: Command in Dockerfile ✅
**When to use:**
- Production deployments
- When image should be self-contained
- When consistency is critical

**Pros:**
- Portable - same image everywhere
- Consistent behavior
- Self-documenting
- Better security

**Cons:**
- Less flexible for development
- Requires image rebuild for changes

## Recommended Solution for This Project

For a Minecraft server plugin, we should use **Approach 2** (command in Dockerfile) because:

1. **Consistency**: Server should behave the same way in dev/prod
2. **Reliability**: Critical startup logic shouldn't be accidentally overridden
3. **Simplicity**: Self-contained image is easier to deploy
4. **CI/CD**: Works better with automated pipelines

## Implementation Plan

1. **Remove command from docker-compose.yml**
2. **Add proper CMD/ENTRYPOINT to Dockerfile**
3. **Use docker-compose.yml only for environment variables and volumes**
4. **Keep .env file for configuration management**

This approach gives us:
- Self-contained, portable Docker image
- Consistent behavior across all environments
- Clean separation: Dockerfile = how to run, docker-compose.yml = what to run
- Easy configuration management through .env file
