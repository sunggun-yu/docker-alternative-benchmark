
# ------------------------------------------------------------------------------
# variables
# ------------------------------------------------------------------------------

DOCKER_IMG = ghcr.io/sunggun-yu/docker-alt-test:$(runtime)
JAR_NAME = hello-0.0.1-SNAPSHOT.jar

# test runtime: docker, colima, rancher, multipass, podman
runtime ?= docker
RUNTIME_CPU = 2
RUNTIME_MEMORY = 6

# docker binary: docker or podman
RUNTIME_BIN ?= docker
FILE_OUT = true
# OUTPUT = 
# ifeq ($(debug), false)
# 	OUTPUT = > /dev/null
# endif

ifneq ($(runtime), $(filter $(runtime),none docker colima rancher multipass podman))
	$(error runtime must be on of none | docker | colima | rancher | multipass | podman)
endif

ifeq ($(runtime),podman)
	RUNTIME_BIN = podman
endif

ifeq ($(runtime),multipass)
	RUNTIME_BIN = multipass exec docker -- docker
endif

MACHINE_MODEL = $$(system_profiler SPHardwareDataType -json | jq -r '.SPHardwareDataType[0].machine_name') $$(sysctl -n machdep.cpu.brand_string) $$(sysctl -n hw.ncpu)CPU $$(expr $$(sysctl -n hw.memsize) / $$((1024**3)))GB
TIME_OUTPUT_FORMAT = \"$(MACHINE_MODEL)\",\"$(runtime)\",\"$@\",%U,%S,%P,%e
TIME = gtime --quiet --append --output=benchmark.csv --format="$(TIME_OUTPUT_FORMAT)"
ifeq ($(FILE_OUT), false)
	TIME = gtime --quiet --format="$(runtime),$(STAGE),%Us user,%Ss system,%P cpu,%es total"
endif

# ------------------------------------------------------------------------------
# general workflow
# ------------------------------------------------------------------------------

# all:
# 1. build
# 2. check image
# 3. save as tar
# 4. remove built image
# 5. load image from tar
# 6. run container with built/loaded image

.PHONY: all 
all: build images save rmi load run rm rmi prune

CMD_BUILD =  $(RUNTIME_BIN) build \
	--build-arg jarName=$(JAR_NAME) \
	-t $(DOCKER_IMG) .

.PHONY: build
build:
	$(TIME) $(CMD_BUILD)

.PHONY: images
images:
	$(TIME) $(RUNTIME_BIN) images $(DOCKER_IMG)

PORT=8080
INTERVAL = 1
TIMEOUT = 300s
.PHONY: run
run:
	make rm FILE_OUT=false
	$(TIME) $(RUNTIME_BIN) run --name docker-alt-test-$(runtime) -d -p $(PORT):8080 \
			$(DOCKER_IMG)
	make readiness

.PHONY: readiness
readiness:
	$(TIME) timeout --foreground -s TERM $(TIMEOUT) bash -c \
		'while [[ "$$(curl -s -o /dev/null -IL -w "%{http_code}" http://localhost:$(PORT))" != "200" ]]; \
		do $(RUNTIME_BIN) logs docker-alt-test-$(runtime) && sleep $(INTERVAL); \
		done'

.PHONY: rm
rm:
	$(TIME) $(RUNTIME_BIN) rm -f docker-alt-test-$(runtime)

.PHONY: save
save:
	$(TIME) $(RUNTIME_BIN) save \
		$(DOCKER_IMG) \
		-o image-$(runtime).tar

.PHONY: rmi
rmi:
	$(TIME) $(RUNTIME_BIN) rmi $(DOCKER_IMG)

.PHONY: load
load:
	$(TIME) $(RUNTIME_BIN) load \
		--input image-$(runtime).tar

.PHONY: prune
prune:
	$(TIME) $(RUNTIME_BIN) system prune -f

# ------------------------------------------------------------------------------
# Shutdown All runtimes
# ------------------------------------------------------------------------------
.PHONY: stop.all
stop.all:
	make docker.stop FILE_OUT=false
	make colima.stop FILE_OUT=false
	make rancher.stop FILE_OUT=false
	make podman.stop FILE_OUT=false

# ------------------------------------------------------------------------------
# Install runtimes and prerequisites
# ------------------------------------------------------------------------------
.PHONY: install
install:
	# rancher should install before docker for some conflict issue
	brew install --cask rancher docker #multipass
	brew install colima podman gnu-time jq

# ------------------------------------------------------------------------------
# Docker
# ------------------------------------------------------------------------------

.PHONY: docker
docker: runtime = docker
docker: docker.start
	make runtime=docker

.PHONY: docker.start
docker.start: runtime = docker
docker.start: colima.stop rancher.stop podman.stop
	$(TIME) open /Applications/Docker.app
	sleep 10

.PHONY: docker.stop
docker.stop: runtime = docker
docker.stop:
	killall Docker || true
	$(TIME) timeout --foreground -s TERM $(TIMEOUT) bash -c 'while [[ $$(pgrep Docker >/dev/null; echo $$?) = 0 ]]; do echo "stopping Docker Desktop" && sleep 1; done'

# ------------------------------------------------------------------------------
# Colima
# ------------------------------------------------------------------------------

.PHONY: colima
colima: colima.start
	make runtime=colima

.PHONY: colima.start
colima.start: runtime = colima
colima.start: docker.stop rancher.stop podman.stop
	$(TIME) colima start --cpu $(RUNTIME_CPU) --memory $(RUNTIME_MEMORY) --runtime docker

.PHONY: colima.stop
colima.stop: runtime = colima
colima.stop:
	$(TIME) colima stop || true

# ------------------------------------------------------------------------------
# Rancher Desktop
# Rancher Start requires manual intervention. re-run make rancher after manual processing
# ------------------------------------------------------------------------------

.PHONY: rancher
rancher: runtime = rancher
rancher: rancher.start
	make runtime=rancher

.PHONY: rancher.start
rancher.start: runtime = rancher
rancher.start: docker.stop colima.stop podman.stop
	$(TIME) ~/.rd/bin/rdctl start --container-engine=moby --flannel-enabled=false --kubernetes-enabled=false

.PHONY: rancher.stop
rancher.stop: runtime = rancher
rancher.stop:
	$(TIME) ~/.rd/bin/rdctl shutdown || true

# ------------------------------------------------------------------------------
# Podman
# ------------------------------------------------------------------------------

.PHONY: podman
podman: runtime = podman
podman: podman.start
	make runtime=podman

.PHONY: podman.start
podman.start: runtime = podman
podman.start: docker.stop rancher.stop
	podman machine init --cpus $(RUNTIME_CPU) --memory $$(($(RUNTIME_MEMORY)*1000)) --disk-size 60 || true
	$(TIME) podman machine start || true

.PHONY: podman.stop
podman.stop: runtime = podman
podman.stop:
	$(TIME) podman machine stop || true
	podman machine rm --force || true

# ------------------------------------------------------------------------------
# Multipass
# Unlike other alternatives, I can not use it seamlessly for building image. mounting $HOME may required.
# remove from candiate for now
# ------------------------------------------------------------------------------
