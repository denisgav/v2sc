#ifndef __HELLO_WORLD_H__
#define __HELLO_WORLD_H__

#include <systemc.h>


SC_MODULE(hello_world)
{
    void process_stimulus(void);



    SC_CTOR(hello_world)
    {
        SC_THREAD(process_stimulus);


    }

};

#endif  // #ifndef __HELLO_WORLD_H__
