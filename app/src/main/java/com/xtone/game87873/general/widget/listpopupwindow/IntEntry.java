package com.xtone.game87873.general.widget.listpopupwindow;

/**
 * 
 * @author: chenyh
 * @Date: 2014-9-10上午9:17:30
 */
public class IntEntry implements java.io.Serializable {
	private static final long serialVersionUID = -8499721149061103585L;

	private final int key;
	private int value;

	public IntEntry(int key,int value){
		this.key = key;
		this.value = value;
	}

	/**
	 * Returns the key corresponding to this entry.
	 * 
	 * @return the key corresponding to this entry
	 */
	public int getKey() {
		return key;
	}

	/**
	 * Returns the value corresponding to this entry.
	 * 
	 * @return the value corresponding to this entry
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Replaces the value corresponding to this entry with the specified value.
	 * 
	 * @param value
	 *            new value to be stored in this entry
	 * @return the old value corresponding to the entry
	 */
	public int setValue(int value) {
		int oldValue = this.value;
		this.value = value;
		return oldValue;
	}

	/**
	 * Compares the specified object with this entry for equality. Returns
	 * {@code true} if the given object is also a map entry and the two entries
	 * represent the same mapping. More formally, two entries {@code e1} and
	 * {@code e2} represent the same mapping if
	 * 
	 * <pre>
	 * (e1.getKey() == null ? e2.getKey() == null : e1.getKey().equals(e2.getKey()))
	 * 		&amp;&amp; (e1.getValue() == null ? e2.getValue() == null : e1.getValue()
	 * 				.equals(e2.getValue()))
	 * </pre>
	 * 
	 * This ensures that the {@code equals} method works properly across
	 * different implementations of the {@code Map.Entry} interface.
	 * 
	 * @param o
	 *            object to be compared for equality with this map entry
	 * @return {@code true} if the specified object is equal to this map entry
	 * @see #hashCode
	 */
	public boolean equals(Object o) {
		if (!(o instanceof IntEntry))
			return false;
		IntEntry e = (IntEntry) o;
		return key==e.getKey()&& value==e.getValue();
	}

	/**
	 * Returns the hash code value for this map entry. The hash code of a map
	 * entry {@code e} is defined to be:
	 * 
	 * <pre>
	 * (e.getKey() == null ? 0 : e.getKey().hashCode())
	 * 		&circ; (e.getValue() == null ? 0 : e.getValue().hashCode())
	 * </pre>
	 * 
	 * This ensures that {@code e1.equals(e2)} implies that
	 * {@code e1.hashCode()==e2.hashCode()} for any two Entries {@code e1} and
	 * {@code e2}, as required by the general contract of
	 * {@link Object#hashCode}.
	 * 
	 * @return the hash code value for this map entry
	 * @see #equals
	 */
	public int hashCode() {
		return key
				^ value;
	}

	/**
	 * Returns a String representation of this map entry. This implementation
	 * returns the string representation of this entry's key followed by the
	 * equals character ("<tt>=</tt>") followed by the string representation of
	 * this entry's value.
	 * 
	 * @return a String representation of this map entry
	 */
	public String toString() {
		return key + "=" + value;
	}
	
	public String toPostUrl(){
		if(getKey()==Integer.MIN_VALUE||getValue()==Integer.MIN_VALUE){
			return "";
		}
		return "~"+getKey()+"x"+getValue();
	}

}