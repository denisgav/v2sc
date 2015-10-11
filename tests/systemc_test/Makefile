
MODULE = mips2

TARGET_ARCH = cygwin
SYSTEMC = /usr/local/systemc

CC        = g++
LD        = ld
OBJCOPY   = objcopy
OBJDUMP   = objdump

SRCDIR  = . 

SRCS  = $(foreach dir, $(SRCDIR), $(wildcard $(dir)/*.cpp))
OBJS  = $(patsubst %.cpp, %.o, $(SRCS))

INCLUDE = $(SYSTEMC)/include
LIBDIR = $(SYSTEMC)/lib-$(TARGET_ARCH)
LIBS   = -lsystemc -lm

CC_OPTS = -O0 -Wall -I$(INCLUDE) -I$(SRCDIR)
LD_OPTS = -L$(LIBDIR)

EXEDIR = .
EXE    = $(MODULE).exe

.PHONY : all
all: $(EXE)

$(EXE): $(OBJS)
	$(CC) -o $(EXEDIR)/$(EXE) $(CC_OPTS) $(LD_OPTS) $(OBJS) $(LIBS)
	@echo "*************** Compile finished successfully! *****************"
	@echo 

%.o : %.cpp
	$(CC) $(CC_OPTS) -c $< -o $@

clean :
	rm -rf $(OBJS) $(EXEDIR)/$(EXE)
	
depend :
	$(CC) -I$(INCLUDE) -M $(SRCS) > depend.mk

-include depend.mk

