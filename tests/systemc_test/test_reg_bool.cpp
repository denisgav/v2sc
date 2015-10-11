
#include "reg_bool.h"
#include <iostream>

SC_MODULE(testRegBool)
{
    sc_in<bool> clk;
    sc_out<reg_bool> out;

    reg_bool v1, v2;
    bool iv1, iv2;
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
            cout << "5-v1: " << v1 << endl;
            break;
        case 3:
            cout << "6-v1: " << std::hex << v1 << endl;
            break;
        }
     }

    SC_CTOR(testRegBool)
    {
        SC_METHOD(test);
        sensitive << clk.pos();

        count = -1;
        iv1 = 2;
        iv2 = 5;
    }
};

void test_reg_bool()
{
    sc_clock clk("clock", 10, SC_NS);
    sc_signal<reg_bool> regBool;
    reg_bool aaa(1);
    reg_bool bbb(aaa);
    reg_bool ccc = 5;

    ccc = 1;
    cout << "1." << ccc << endl;
    ccc = !ccc;
    cout << "2." << ccc << endl;
    ccc = ~ccc;
    cout << "3." << ccc << endl;
    ccc = "0";
    cout << "4." << ccc << endl;
    ccc = '1';
    cout << "5." << ccc << endl;
    ccc &= 0;
    cout << "6." << ccc << endl;
    ccc |= 0;
    cout << "7." << ccc << endl;

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

    testRegBool testreg("testReBool");
    testreg.clk(clk);
    testreg.out(regBool);

    cout << "start: test_reg_bool" << endl;
    sc_start(500);
    cout << "end: test_reg_bool" << endl;

    sc_stop();
}
