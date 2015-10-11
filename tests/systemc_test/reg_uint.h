/*
 * reg_uint: inheritance of sc_signal
 * operate the same as sc_uint, but can be used as signal
 */

#ifndef __REG_UINT_H__
#define __REG_UINT_H__

#include <systemc.h>

using namespace sc_dt;

template<int W>
class reg_uint : public sc_signal<sc_uint<W> >, public sc_value_base
{
    friend class sc_uint_bitref_r;
    friend class sc_uint_bitref;
    friend class sc_uint_subref_r;
    friend class sc_uint_subref;
    friend class sc_uint_base;
public:

    // constructors

    reg_uint()
        : sc_signal<sc_uint<W> >()
    { }

    explicit reg_uint( uint_type v )
        : sc_signal<sc_uint<W> >()
    { sc_signal<sc_uint<W> >::m_cur_val = v; }

    reg_uint( const reg_uint<W>& a )
        : sc_signal<sc_uint<W> >()
    { sc_signal<sc_uint<W> >::m_cur_val = a.read(); }

    explicit reg_uint( const sc_uint_base& a )
        : sc_signal<sc_uint<W> >()
    { sc_signal<sc_uint<W> >::m_cur_val = a; }

    explicit reg_uint( const sc_uint_subref_r& a )
        : sc_signal<sc_uint<W> >()
    { sc_signal<sc_uint<W> >::m_cur_val = a; }

    template< class T >
    explicit reg_uint( const sc_generic_base<T>& a )
        : sc_signal<sc_uint<W> >()
    { sc_signal<sc_uint<W> >::m_cur_val = a; }

    explicit reg_uint( const sc_signed& a )
        : sc_signal<sc_uint<W> >()
    { sc_signal<sc_uint<W> >::m_cur_val = a; }

    explicit reg_uint( const sc_unsigned& a )
        : sc_signal<sc_uint<W> >()
    { sc_signal<sc_uint<W> >::m_cur_val = a; }

    explicit reg_uint( const sc_bv_base& a )
        : sc_signal<sc_uint<W> >()
    { sc_signal<sc_uint<W> >::m_cur_val = a; }

    explicit reg_uint( const sc_lv_base& a )
        : sc_signal<sc_uint<W> >()
    { sc_signal<sc_uint<W> >::m_cur_val = a; }

    reg_uint( const char* a )
        : sc_signal<sc_uint<W> >()
    { sc_signal<sc_uint<W> >::m_cur_val = a; }

    explicit reg_uint( unsigned long a )
        : sc_signal<sc_uint<W> >()
    { sc_signal<sc_uint<W> >::m_cur_val = a; }

    explicit reg_uint( long a )
        : sc_signal<sc_uint<W> >()
    { sc_signal<sc_uint<W> >::m_cur_val = a; }

    explicit reg_uint( unsigned int a )
        : sc_signal<sc_uint<W> >()
    { sc_signal<sc_uint<W> >::m_cur_val = a; }

    explicit reg_uint( int a )
        : sc_signal<sc_uint<W> >()
    { sc_signal<sc_uint<W> >::m_cur_val = a; }

    explicit reg_uint( int64 a )
        : sc_signal<sc_uint<W> >()
    { sc_signal<sc_uint<W> >::m_cur_val = a; }

    explicit reg_uint( double a )
        : sc_signal<sc_uint<W> >()
    { sc_signal<sc_uint<W> >::m_cur_val = a; }


    // assignment operators

    reg_uint<W>& operator = ( uint_type v )
    { sc_signal<sc_uint<W> >::m_cur_val = v; return *this; }

    reg_uint<W>& operator = ( const sc_uint_base& a )
    { sc_signal<sc_uint<W> >::m_cur_val = a; return *this; }

    reg_uint<W>& operator = ( const sc_uint_subref_r& a )
    { sc_signal<sc_uint<W> >::m_cur_val = a; return *this; }

    reg_uint<W>& operator = ( const reg_uint<W>& a )
    { sc_signal<sc_uint<W> >::m_cur_val = a.read(); return *this; }

    template<class T>
    reg_uint<W>& operator = ( const sc_generic_base<T>& a )
    { sc_signal<sc_uint<W> >::m_cur_val = a; return *this; }

    reg_uint<W>& operator = ( const sc_signed& a )
    { sc_signal<sc_uint<W> >::m_cur_val = a; return *this; }

    reg_uint<W>& operator = ( const sc_unsigned& a )
    { sc_signal<sc_uint<W> >::m_cur_val = a; return *this; }

    reg_uint<W>& operator = ( const sc_bv_base& a )
    { sc_signal<sc_uint<W> >::m_cur_val = a; return *this; }

    reg_uint<W>& operator = ( const sc_lv_base& a )
    { sc_signal<sc_uint<W> >::m_cur_val = a; return *this; }

    reg_uint<W>& operator = ( const char* a )
    { sc_signal<sc_uint<W> >::m_cur_val = a; return *this; }

    reg_uint<W>& operator = ( unsigned long a )
    { sc_signal<sc_uint<W> >::m_cur_val = a; return *this; }

    reg_uint<W>& operator = ( long a )
    { sc_signal<sc_uint<W> >::m_cur_val = a; return *this; }

    reg_uint<W>& operator = ( unsigned int a )
    { sc_signal<sc_uint<W> >::m_cur_val = a; return *this; }

    reg_uint<W>& operator = ( int a )
    { sc_signal<sc_uint<W> >::m_cur_val = a; return *this; }

    reg_uint<W>& operator = ( int64 a )
    { sc_signal<sc_uint<W> >::m_cur_val = a; return *this; }

    reg_uint<W>& operator = ( double a )
    { sc_signal<sc_uint<W> >::m_cur_val = a; return *this; }


    // arithmetic assignment operators

    reg_uint<W>& operator += ( uint_type v )
    { sc_signal<sc_uint<W> >::m_cur_val += v; return *this; }

    reg_uint<W>& operator -= ( uint_type v )
    { sc_signal<sc_uint<W> >::m_cur_val -= v; return *this; }

    reg_uint<W>& operator *= ( uint_type v )
    { sc_signal<sc_uint<W> >::m_cur_val *= v; return *this; }

    reg_uint<W>& operator /= ( uint_type v )
    { sc_signal<sc_uint<W> >::m_cur_val /= v; return *this; }

    reg_uint<W>& operator %= ( uint_type v )
    { sc_signal<sc_uint<W> >::m_cur_val %= v; return *this; }

    // bitwise assignment operators

    reg_uint<W>& operator &= ( uint_type v )
    { sc_signal<sc_uint<W> >::m_cur_val &= v; return *this; }

    reg_uint<W>& operator |= ( uint_type v )
    { sc_signal<sc_uint<W> >::m_cur_val |= v; return *this; }

    reg_uint<W>& operator ^= ( uint_type v )
    { sc_signal<sc_uint<W> >::m_cur_val ^= v; return *this; }


    reg_uint<W>& operator <<= ( uint_type v )
    { sc_signal<sc_uint<W> >::m_cur_val <<= v; return *this; }

    reg_uint<W>& operator >>= ( uint_type v )
    { sc_signal<sc_uint<W> >::m_cur_val >>= v; return *this; }

    // prefix and postfix increment and decrement operators

    sc_uint<W> operator ++ () // prefix
    { return sc_signal<sc_uint<W> >::m_cur_val ++; }

    const sc_uint<W> operator ++ ( int ) // postfix
    { return ++ sc_signal<sc_uint<W> >::m_cur_val; }

    sc_uint<W> operator -- () // prefix
    { return sc_signal<sc_uint<W> >::m_cur_val --;}

    const sc_uint<W> operator -- ( int ) // postfix
    { return -- sc_signal<sc_uint<W> >::m_cur_val; }

    // relational operators

    friend bool operator == ( const reg_uint& a, const reg_uint& b )
    { return a.read() == b.read(); }
    friend bool operator != ( const reg_uint& a, const reg_uint& b )
    { return a.read() != b.read(); }
    friend bool operator <  ( const reg_uint& a, const reg_uint& b )
    { return a.read() < b.read(); }
    friend bool operator <= ( const reg_uint& a, const reg_uint& b )
    { return a.read() <= b.read(); }
    friend bool operator >  ( const reg_uint& a, const reg_uint& b )
    { return a.read() > b.read(); }
    friend bool operator >= ( const reg_uint& a, const reg_uint& b )
    { return a.read() >= b.read(); }

    // bit selection

    sc_uint_bitref&         operator [] ( int i )
    { return sc_signal<sc_uint<W> >::m_cur_val[i]; }

    const sc_uint_bitref_r& operator [] ( int i ) const
    { return sc_signal<sc_uint<W> >::read()[i];}

    sc_uint_bitref&         bit( int i )
    { return sc_signal<sc_uint<W> >::m_cur_val.bit(i); }

    const sc_uint_bitref_r& bit( int i ) const
    { return sc_signal<sc_uint<W> >::read().bit(i); }


    // part selection

    sc_uint_subref&         operator () ( int left, int right )
    { return sc_signal<sc_uint<W> >::m_cur_val(left, right); }

    const sc_uint_subref_r& operator () ( int left, int right ) const
    { return sc_signal<sc_uint<W> >::read()(left, right); }

    sc_uint_subref&         range( int left, int right )
    { return sc_signal<sc_uint<W> >::m_cur_val.range(left, right); }

    const sc_uint_subref_r& range( int left, int right ) const
    { return sc_signal<sc_uint<W> >::read().range(left, right); }


    // bit access, without bounds checking or sign extension

    bool test( int i ) const
    { return sc_signal<sc_uint<W> >::m_cur_val.test(i); }

    void set( int i )
    { sc_signal<sc_uint<W> >::m_cur_val.set(i); }

    void set( int i, bool v )
    { sc_signal<sc_uint<W> >::m_cur_val.set(i, v); }


    // capacity

    int length() const
    { return sc_signal<sc_uint<W> >::m_cur_val.length(); }

    // reduce methods

    bool and_reduce() const
    { return sc_signal<sc_uint<W> >::m_cur_val.and_reduce(); }

    bool nand_reduce() const
    { return ( ! and_reduce() ); }

    bool or_reduce() const
    { return sc_signal<sc_uint<W> >::m_cur_val.or_reduce(); }

    bool nor_reduce() const
    { return ( ! or_reduce() ); }

    bool xor_reduce() const
    { return sc_signal<sc_uint<W> >::m_cur_val.xor_reduce(); }

    bool xnor_reduce() const
    { return ( ! xor_reduce() ); }

    // implicit conversion to uint_type
    operator uint_type() const
    { return sc_signal<sc_uint<W> >::m_cur_val; }

    // explicit conversions
    uint_type value() const
    { return operator uint_type(); }


    int to_int() const
    { return sc_signal<sc_uint<W> >::m_cur_val.to_int(); }

    unsigned int to_uint() const
    { return sc_signal<sc_uint<W> >::m_cur_val.to_uint(); }

    long to_long() const
    { return sc_signal<sc_uint<W> >::m_cur_val.to_long(); }

    unsigned long to_ulong() const
    { return sc_signal<sc_uint<W> >::m_cur_val.to_ulong(); }

    int64 to_int64() const
    { return sc_signal<sc_uint<W> >::m_cur_val.to_int64(); }

    uint64 to_uint64() const
    { return sc_signal<sc_uint<W> >::m_cur_val.to_uint64(); }

    double to_double() const
    { return sc_signal<sc_uint<W> >::m_cur_val.to_double(); }

    // concatenation support

    virtual int concat_length(bool* xz_present_p) const
    { return sc_signal<sc_uint<W> >::m_cur_val.concat_length(xz_present_p); }

    virtual bool concat_get_ctrl( sc_digit* dst_p, int low_i ) const
    { return sc_signal<sc_uint<W> >::m_cur_val.concat_get_ctrl(dst_p, low_i); }

    virtual bool concat_get_data( sc_digit* dst_p, int low_i ) const
    { return sc_signal<sc_uint<W> >::m_cur_val.concat_get_data(dst_p, low_i); }

    virtual uint64 concat_get_uint64() const
    { return sc_signal<sc_uint<W> >::m_cur_val.concat_get_uint64(); }

    virtual void concat_set(int64 src, int low_i)
    { sc_signal<sc_uint<W> >::m_cur_val.concat_set(src, low_i); }

    virtual void concat_set(const sc_signed& src, int low_i)
    { sc_signal<sc_uint<W> >::m_cur_val.concat_set(src, low_i); }

    virtual void concat_set(const sc_unsigned& src, int low_i)
    { sc_signal<sc_uint<W> >::m_cur_val.concat_set(src, low_i); }

    virtual void concat_set(uint64 src, int low_i)
    { sc_signal<sc_uint<W> >::m_cur_val.concat_set(src, low_i); }

};


#endif /* __REG_UINT_H__ */
