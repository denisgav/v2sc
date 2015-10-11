
#include "reg_int.h"
#include <iostream>

SC_MODULE(testRegInt)
{
    sc_in<bool> clk;
    sc_out<reg_int> out;

    reg_int v1, v2;
    int iv1, iv2;
    int count;

    void test()
    {
        count ++;
        if(count > 50)
            count = 0;

        switch(count)
        {
        case 0:
            v1 = iv1;
            v2 = iv2;
            cout << "0-v1: " << v1 << endl;
            break;
        case 1:
            v1 = v2;
            cout << "1-v1: " << v1 << endl;
            break;
        case 2:
            v1 += iv1;
            cout << "2-v1: " << v1 << endl;
            break;
        case 3:
            v1 ++;
            cout << "3-v1: " << v1 << endl;
            break;
        case 4:
            v1 >>= iv1;
            cout << "4-v1: " << v1 << endl;
            break;
        case 5:
            cout << "5-v1: " << v1 << endl;
            break;
        case 6:
            cout << "6-v1: " << std::hex << v1 << endl;
            break;
        }
     }

    SC_CTOR(testRegInt)
    {
        SC_METHOD(test);
        sensitive << clk.pos();

        count = -1;
        iv1 = 2;
        iv2 = 5;
    }
};

void test_reg_int()
{
    sc_clock clk("clock", 10, SC_NS);
    sc_signal<reg_int> regInt;
    reg_int aaa(20);
    reg_int bbb(aaa);
    reg_int ccc = 5;

    ccc = 1;
    ccc += 1;
    ccc ++;
    ++ ccc;
    ccc = ccc + 1;
    ccc = ccc-1;
    ccc = ccc*1;
    ccc = ccc/1;

    if(ccc == 1)
        printf("ok\n");
    if(ccc > 1)
        printf("ok\n");
    if(ccc < 1)
        printf("ok\n");
    if(ccc >= 1)
        printf("ok\n");
    if(ccc <= 1)
        printf("ok\n");
    if(1 == ccc)
        printf("ok\n");
    if(1 > ccc)
        printf("ok\n");
    if(1 < ccc)
        printf("ok\n");
    if(1 >= ccc)
        printf("ok\n");
    if(1 <= ccc)
        printf("ok\n");

    testRegInt testreg("testRegUint");
    testreg.clk(clk);
    testreg.out(regInt);

    cout << "start: test_reg_int" << endl;
    sc_start(500);
    cout << "end: test_reg_int" << endl;

    sc_stop();
}
