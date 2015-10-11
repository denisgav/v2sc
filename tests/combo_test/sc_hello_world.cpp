#include <iostream>
using namespace std;

// All systemc modules should include systemc.h header file
#include "systemc.h"
//Include VHDL hello world module
#include "hello_world.h"
// Hello_world is module name
SC_MODULE (hello_world_sc) {
  hello_world hello_vhdl;

  SC_CTOR (hello_world_sc) : hello_vhdl("hello_vhdl") {
    // Nothing in constructor 
  }
  void say_hello() {
    //Print "Hello World" to the console.
    cout << "Hello World from systemc."<<endl;
  }
};

// sc_main in top level function like in C++ main
int sc_main(int argc, char* argv[]) {
  hello_world_sc hello_sc("HELLO_SC");
  // Print the hello world
  hello_sc.say_hello();

  //VHDL module instantiation
  hello_world hello_vhdl("HELLO_VHDL");
  //hello_vhdl.process_stimulus();

  sc_start(33.4, SC_PS); // run for 33.4 picosecond. SC_FS, SC_PS, SC_NS, SC_US, SC_MS, SC_SEC

  return(0);
}
