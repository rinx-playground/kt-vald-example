SHELL = bash
VALDREPO = github.com/vdaas/vald

VALD_DIR ?= vald
DEPS_ROOT ?= vendor

.PHONY: all
all: build

.PHONY: build
build: \
	dependencies
	./gradlew build

.PHONY: run
run:
	./gradlew run

.PHONY: clean
clean:
	rm -rf $(VALD_DIR)
	rm -rf $(DEPS_ROOT)

dependencies: \
	$(VALD_DIR) \
	$(DEPS_ROOT)

$(VALD_DIR):
	git clone --depth 1 https://$(VALDREPO) $(VALD_DIR)
	rm -rf $(VALD_DIR)/apis/proto/filter \
		$(VALD_DIR)/apis/proto/gateway \
		$(VALD_DIR)/apis/proto/payload
	find $(VALD_DIR)/apis/proto/* -name '*.proto' | \
		xargs sed -i -E "s%github.com/gogo/googleapis/%%g"
	find $(VALD_DIR)/apis/proto/* -name '*.proto' | \
		xargs sed -i -E "s%^.*gogoproto.*$$%%g"

$(DEPS_ROOT): \
	$(PWD)/$(DEPS_ROOT)/src/github.com/envoyproxy/protoc-gen-validate

$(PWD)/$(DEPS_ROOT)/src/github.com/envoyproxy/protoc-gen-validate:
	mkdir -p $@
	git clone \
		--depth 1 \
		https://github.com/envoyproxy/protoc-gen-validate \
		$(PWD)/$(DEPS_ROOT)/src/github.com/envoyproxy/protoc-gen-validate
	rm -rf $(PWD)/$(DEPS_ROOT)/src/github.com/envoyproxy/protoc-gen-validate/example-workspace \
		$(PWD)/$(DEPS_ROOT)/src/github.com/envoyproxy/protoc-gen-validate/java \
		$(PWD)/$(DEPS_ROOT)/src/github.com/envoyproxy/protoc-gen-validate/tests
