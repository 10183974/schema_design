INCPATH		= -I ../ulib/include -I ../gflags/include
LIBPATH		= -L ../gflags/lib -L ../ulib/lib

EXTRALIB	?=

CXXFLAGS	?= -O3 -fPIC -W -Wall
LDFLAGS		?= -static $(EXTRALIB) -lgflags -lpthread -lgsl -lgslcblas -lulib
DEBUG		?=

CM_OBJS		= $(addsuffix .o, $(basename $(wildcard src/*.cpp)))

CM		= bin/cm

.cpp.o:
	@echo -e "  CXX\t$@"
	@$(CXX) $(INCPATH) $(CXXFLAGS) -c -o "$@" "$<"

all: $(CM)

release: $(CM)
	@strip -s $(CM)

$(CM): $(CM_OBJS)
	@echo -e "  GEN\t$(CM)"	
	@$(CXX) -o $(CM) $(CM_OBJS) $(LIBPATH) $(LDFLAGS)

clean:
	@rm -rf $(CM) $(CM_OBJS)
	@find . -name "*~" | xargs rm -rf

.PHONY: all clean release
