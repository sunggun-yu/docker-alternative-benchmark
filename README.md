# Docker Alternative Benchmark

## Candidates

- Colima
- Rancher Desktop
- Podman
- ~~Multipass~~

## Requirement

- Can do everything Docker provide. but followings are priority
  - docker build
  - docker pull
  - docker push
  - docker run
  - docker save
  - docker load
- Easy to install and configure
- Seamless experience
- Similar performance with Docker
- Integration
  - Skaffold
  - BuildPacks
  - Kind cluster

## Test Condition

- container engine resource
  - CPU: 2 Core
  - Memory: 6GB
  - Disk: N/A
  - run command with `time` to measure execution time and cpu usage
  - run original docker as basis of comparison
  - use same Dockerfile for build
  - copy/add directory/file under root directory
  - multi stage build
  - ~~install package - apt / apk~~
- push docker image into registry and pull and run with original docker to check compatibility - only for podman ?
- run docker container with exposing port. port should be accessible from local. e.g. curl <http://localhost:8080
