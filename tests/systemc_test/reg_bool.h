/*
 * reg_bool: inheritance of sc_signal and sc_concat_bool
 * operate the same as bool, but can be used as signal
 */

#ifndef __REG_BOOL_H__
#define __REG_BOOL_H__

#include <systemc.h>

using namespace sc_dt;

class reg_bool : public sc_signal<bool>
{
    friend class sc_uint_bitref_r;
    friend class sc_uint_bitref;
    friend class sc_uint_subref_r;
    friend class sc_uint_subref;
    friend class sc_uint_base;

public:

    // constructors
    reg_bool()
        : sc_signal<bool>()
    { }

    reg_bool( bool v )
        : sc_signal<bool>()
    { sc_signal<bool>::m_cur_val = v; }

    reg_bool( const reg_bool& a )
        : sc_signal<bool>()
    { sc_signal<bool>::m_cur_val = a.read(); }

    reg_bool( unsigned int a )
        : sc_signal<bool>()
    { sc_signal<bool>::m_cur_val = (bool)a; }

    reg_bool( int a )
        : sc_signal<bool>()
    { sc_signal<bool>::m_cur_val = (bool)a; }

    reg_bool( char a )
        { sc_signal<bool>::m_cur_val = (a == '0' ? 0 : 1);}

    reg_bool( const sc_uint_base& a )
        : sc_signal<bool>()
    { sc_signal<bool>::m_cur_val = a.to_int(); }

    reg_bool( const sc_uint_subref_r& a )
        : sc_signal<bool>()
    { sc_signal<bool>::m_cur_val = a.to_int(); }


    reg_bool( const char *a )
        : sc_signal<bool>()
    { if(a != NULL)
        sc_signal<bool>::m_cur_val = (a[0] == '0' ? 0 : 1); }


    // assignment operators

    reg_bool& operator = ( bool v )
    { sc_signal<bool>::m_cur_val = v; return *this; }

    reg_bool& operator = ( uint_type v )
    { sc_signal<bool>::m_cur_val = (bool)v; return *this; }

    reg_bool& operator = ( const sc_uint_base& a )
    { sc_signal<bool>::m_cur_val = a.to_int(); return *this; }

    reg_bool& operator = ( const sc_uint_subref_r& a )
    { sc_signal<bool>::m_cur_val = a.to_int(); return *this; }

    reg_bool& operator = ( unsigned long a )
    { sc_signal<bool>::m_cur_val = (bool)a; return *this; }

    reg_bool& operator = ( long a )
    { sc_signal<bool>::m_cur_val = (bool)a; return *this; }

    reg_bool& operator = ( unsigned int a )
    { sc_signal<bool>::m_cur_val = (bool)a; return *this; }

    reg_bool& operator = ( int a )
    { sc_signal<bool>::m_cur_val = (bool)a; return *this; }

    reg_bool& operator = ( int64 a )
    { sc_signal<bool>::m_cur_val = (bool)a; return *this; }

    reg_bool& operator = ( double a )
    { sc_signal<bool>::m_cur_val = (bool)a; return *this; }

    reg_bool& operator = ( char a )
    { sc_signal<bool>::m_cur_val = (a == '0' ? 0 : 1); return *this; }

    reg_bool& operator = ( const char *a )
    { if(a != NULL)
        sc_signal<bool>::m_cur_val = (a[0] == '0' ? 0 : 1);
      return *this; }

    // bitwise assignment operators

    reg_bool& operator &= ( bool v )
    { sc_signal<bool>::m_cur_val &= (bool)v; return *this; }

    reg_bool& operator |= ( bool v )
    { sc_signal<bool>::m_cur_val |= (bool)v; return *this; }

    reg_bool& operator ^= ( bool v )
    { sc_signal<bool>::m_cur_val ^= (bool)v; return *this; }

    bool operator ! ()
    { return !sc_signal<bool>::read(); }

    bool operator ~ ()
    { return !sc_signal<bool>::read(); }
};

inline
const
sc_dt::sc_concatref& operator , (const reg_bool& a, const reg_bool& b)
{
    const sc_dt::sc_concat_bool* a_p;      // Proxy for boolean value.
    const sc_dt::sc_concat_bool* b_p;      // Proxy for boolean value.
    sc_dt::sc_concatref*         result_p; // Proxy for the concatenation.

    a_p = sc_dt::sc_concat_bool::allocate(a.read());
    b_p = sc_dt::sc_concat_bool::allocate(b.read());
    result_p = sc_dt::sc_concatref::m_pool.allocate();
    result_p->initialize( *a_p, *b_p );
    return *result_p;
}

#endif /* __REG_BOOL_H__ */
