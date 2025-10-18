########################################################################################################################
# Makefile for building the C integration test with the ScaFi library.
########################################################################################################################

ifeq ($(OS),Windows_NT)
	EXE = main.exe
	CC = gcc
	SCAFI3_LIB = scafi3.dll scafi3.lib
	LIBLINK = scafi3.lib
	DOWNLOAD_CMD = powershell -Command "Invoke-WebRequest -Uri $(1) -OutFile $(2)"
else ifeq ($(shell uname -s),Darwin)
	EXE = main
    # On MacOS, usually gcc is an alias to clang, so we try to find a real GCC installation
	CC := $(shell \
		if gcc --version 2>/dev/null | grep -q "^gcc"; then \
			echo gcc; \
		else \
			find /opt/homebrew/bin /usr/local/bin -name 'gcc-[0-9]*' 2>/dev/null | sort -V | tail -1 || \
			echo "NOT_FOUND"; \
		fi)
	ifeq ($(CC),NOT_FOUND)
		$(error GCC not found. Install: brew install gcc)
	endif
	SCAFI3_LIB = libscafi3.dylib
	LIBLINK = -L. -Wl,-rpath,. -lscafi3
	DOWNLOAD_CMD = curl -fsSL $(1) -o $(2)
else
	EXE = main
	CC = gcc
	SCAFI3_LIB = libscafi3.so
	LIBLINK = -L. -Wl,-rpath,. -lscafi3
	DOWNLOAD_CMD = curl -fsSL $(1) -o $(2)
endif

# Protobuf dependencies
PROTOBUF_C_VERSION = 1.5.2
PROTOBUF_C_BASE = https://raw.githubusercontent.com/protobuf-c/protobuf-c/v$(PROTOBUF_C_VERSION)/protobuf-c
VENDOR_DIR = vendor
PROTOBUF_C_DIR = $(VENDOR_DIR)/protobuf-c
PROTOBUF_C_SRC = $(PROTOBUF_C_DIR)/protobuf-c.c
PROTOBUF_C_HDR = $(PROTOBUF_C_DIR)/protobuf-c.h

# Detect protobuf files
PROTO_C_FILES := $(wildcard *.pb-c.c)
PROTO_H_FILES := $(wildcard *.pb-c.h)

CFLAGS = -std=gnu11 -Wall #-Wextra

# Configure protobuf settings if .pb-c files are found
ifneq ($(PROTO_C_FILES),) # Protobuf found - add includes and libs
    SOURCES = main.c $(PROTO_C_FILES) $(PROTOBUF_C_SRC)
    PROTOBUF_INCLUDES = -I$(VENDOR_DIR)
	PROTOBUF_DEPS = $(PROTOBUF_C_SRC) $(PROTOBUF_C_HDR)
else # No protobuf
    SOURCES = main.c
    PROTOBUF_INCLUDES =
    PROTOBUF_DEPS =
endif

all: $(EXE)

$(PROTOBUF_C_DIR):
	mkdir -p $(PROTOBUF_C_DIR)

$(PROTOBUF_C_SRC): | $(PROTOBUF_C_DIR)
	@echo "Downloading protobuf-c.c..."
	$(call DOWNLOAD_CMD,$(PROTOBUF_C_BASE)/protobuf-c.c,$(PROTOBUF_C_SRC))

$(PROTOBUF_C_HDR): | $(VENDOR_DIR)
	@echo "Downloading protobuf-c.h..."
	$(call DOWNLOAD_CMD,$(PROTOBUF_C_BASE)/protobuf-c.h,$(PROTOBUF_C_HDR))

$(EXE): $(SOURCES) $(PROTO_H_FILES) $(SCAFI3_LIB) $(PROTOBUF_DEPS)
	$(CC) $(CFLAGS) $(PROTOBUF_INCLUDES) -I. -I$(VENDOR_DIR) $(SOURCES) $(LIBLINK) -o $(EXE)

.PHONY: all clean

clean:
	rm -rf $(EXE) $(VENDOR_DIR)
