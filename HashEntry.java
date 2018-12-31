
/**
 * Write a description of class HashEntry here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */

class HashEntry<AnyType> {
    public AnyType element;   // the element
    public boolean isActive;  // false if marked deleted

    public HashEntry(AnyType e) {
        this(e, true);
    }

    public HashEntry(AnyType e, boolean i) {
        element = e;
        isActive = i;
    }

    public void updateEntry(AnyType x) {
        // System.out.println("Got here!");
        if (element instanceof FingerprintEntry &&
            x instanceof FingerprintEntry) {
            FingerprintEntry fpEl = (FingerprintEntry) element;
            FingerprintEntry xEl = (FingerprintEntry) x;
            fpEl.add(xEl);
        }
    }
    
    public AnyType getElement() { return element; }

}
