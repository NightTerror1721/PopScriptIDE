package kp.ps.utils;

/**
 *
 * @author Marc
 */
public class Pointer<T>
{
    private T value;
    
    public Pointer(T value)
    {
        this.value = value;
    }
    public Pointer() { this(null); }
    
    public final boolean isNull() { return value == null; }
    public final void setNull() { value = null; }
    
    public final void set(T value) { this.value = value; }
    public final T get() { return value; }
}
