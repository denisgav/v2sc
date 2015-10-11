/*
 * reg_int: inheritance of sc_signal and sc_uint_base
 * operate the same as int, but can be used as signal
 */

#ifndef __REG_INT_H__
#define __REG_INT_H__

#include <systemc.h>

using namespace sc_dt;

class reg_int : public sc_signal<int>
{
public:

    // constructors
    reg_int()
        : sc_signal<int>()
    {  }

    reg_int( int v )
        : sc_signal<int>()
    { sc_signal<int>::m_cur_val = v; }

    reg_int( const reg_int& a )
        : sc_signal<int>()
    { sc_signal<int>::m_cur_val = a.read(); }

    reg_int( unsigned int a )
        : sc_signal<int>()
    { sc_signal<int>::m_cur_val = (int)a; }


    // assignment operators
    reg_int& operator = ( int v )
    { sc_signal<int>::m_cur_val = v; return *this; }

    // arithmetic assignment operators
    reg_int& operator += ( int v )
    { sc_signal<int>::m_cur_val += v; return *this; }

    reg_int& operator -= ( int v )
    { sc_signal<int>::m_cur_val -= v; return *this; }

    reg_int& operator *= ( int v )
    { sc_signal<int>::m_cur_val *= v; return *this; }

    reg_int& operator /= ( int v )
    { sc_signal<int>::m_cur_val /= v; return *this; }

    reg_int& operator %= ( int v )
    { sc_signal<int>::m_cur_val %= v; return *this; }


    // bitwise assignment operators

    reg_int& operator &= ( int v )
    { sc_signal<int>::m_cur_val &= v; return *this; }

    reg_int& operator |= ( int v )
    { sc_signal<int>::m_cur_val |= v; return *this; }

    reg_int& operator ^= ( int v )
    { sc_signal<int>::m_cur_val ^= v; return *this; }


    reg_int& operator <<= ( int v )
    { sc_signal<int>::m_cur_val <<= v; return *this; }

    reg_int& operator >>= ( int v )
    { sc_signal<int>::m_cur_val >>= v; return *this; }


    // prefix and postfix increment and decrement operators

    int operator ++ () // prefix
    { return sc_signal<int>::m_cur_val ++; }

    const int operator ++ ( int ) // postfix
    { return ++ sc_signal<int>::m_cur_val; }

    int operator -- () // prefix
    { return sc_signal<int>::m_cur_val --; }

    const int operator -- ( int ) // postfix
    { return -- sc_signal<int>::m_cur_val; }

    //operator int() const
    //{ return sc_signal<int>::m_cur_val; }
};

#endif /* __REG_INT_H__ */
