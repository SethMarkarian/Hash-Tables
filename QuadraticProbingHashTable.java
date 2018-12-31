
/**
 * Quadratic probing hash table class. Adapted from
 * https://users.cs.fiu.edu/~weiss/dsaajava3/code/QuadraticProbingHashTable.java
 *
 * @author Tom Collins
 * @version 11/10/2018
 */

public class QuadraticProbingHashTable<AnyType> {
    
    // Instance variables.
    private static final int DEFAULT_TABLE_SIZE = 101;

    public HashEntry<AnyType>[] array; // The array of elements
    private int occupied;               // The number of occupied cells
    private int theSize;                // Current size

    /**
     * Constructors.
     */
    public QuadraticProbingHashTable() {
        this(DEFAULT_TABLE_SIZE);
    }

    public QuadraticProbingHashTable(int size) {
        allocateArray(size);
        doClear();
    }

    /**
     * Methods.
     */
    public boolean insert(AnyType x) {
        int currentPos = myhash(x);
        if (x instanceof FingerprintEntry) {
            FingerprintEntry w = (FingerprintEntry) x;
            if (w.getHash() == 49686) {
                System.out.println("currentPos = " + currentPos);
            }
        }
        if (array[currentPos] != null) {
            // System.out.println("Got to a non-null index.");
            if (x instanceof FingerprintEntry) {
                FingerprintEntry u = (FingerprintEntry) array[currentPos].getElement();
                FingerprintEntry v = (FingerprintEntry) x;
                if (u.getKey().equals(v.getKey())) {
                    // Equal keys, so want to analyze them as
                    // instances of the same thing. Use
                    // updateEntry().
                    // System.out.println("Got to an update.");
                    array[currentPos].updateEntry(x);
                }
                else {
                    // Collision.
                    // System.out.println("Got to a collision.");
                    currentPos = findPos(x);
                }
            }
            // Just assume regular hash and a collision.
            currentPos = findPos(x);
            
        }
        
        // Old.
        // int currentPos = findPos(x);
        // if(isActive(currentPos)) {
            // array[currentPos].updateEntry(x);
            // // In the original, this is just:
            // // return false;
            // // But here we need to edit the entry to reflect that we
            // // found another event that hashes to the same thing.
        // }

        if(array[currentPos] == null) {
            // System.out.println("Got to a null index.");
            ++occupied;
            array[currentPos] = new HashEntry<>(x, true);
            theSize++;
        }
        
        // Rehash.
        if(occupied > array.length/2) {
            rehash();
        }
        
        return true;
    }
    
    /**
     * Expand the hash table.
     */
    private void rehash(){
        System.out.println("Rehashing. Old theSize = " + theSize);
        HashEntry<AnyType>[] oldArray = array;

        // Create a new double-sized, empty table.
        allocateArray(2*oldArray.length);
        occupied = 0;
        theSize = 0;

        // Copy table over.
        for(HashEntry<AnyType> entry : oldArray) {
            if(entry != null && entry.isActive) {
                insert(entry.element);
            }
        }
   }

    /**
     * Method that performs quadratic probing resolution.
     * @param x the item to search for.
     * @return the position where the search terminates.
     */
    public int findPos(AnyType x) {
        int offset = 1;
        int currentPos = myhash(x);
        
        while(array[currentPos] != null &&
              !array[currentPos].element.equals(x)) {
            currentPos += offset;  // Compute ith probe.
            offset += 2;
            if(currentPos >= array.length) {
                currentPos -= array.length;
            }
        }
        
        return currentPos;
    }

    /**
     * Remove from the hash table.
     * @param x the item to remove.
     * @return true if item removed
     */
    public boolean remove(AnyType x) {
        int currentPos = findPos(x);
        if(isActive(currentPos)) {
            array[currentPos].isActive = false;
            theSize--;
            return true;
        }
        else {
            return false;
        }
    }
    
    public int size() { return theSize; }
    
    public int capacity( ) { return array.length; }

    /**
     * Find an item in the hash table.
     * @param x the item to search for.
     * @return the matching item.
     */
    public AnyType contains(AnyType x) {
        int currentPos = myhash(x);
        if (isActive(currentPos)) {
            return array[currentPos].getElement();
        }
        else {
            return null;
        }
    }
    // Old. The use of findPos() seems problematic, given the
    // description of the method.
    // public boolean contains(AnyType x) {
    //     int currentPos = findPos(x);
    //     return isActive(currentPos);
    // }

    /**
     * Return true if currentPos exists and is active.
     * @param currentPos the result of a call to findPos.
     * @return true if currentPos is active.
     */
    private boolean isActive(int currentPos) {
        return array[currentPos] != null && array[currentPos].isActive;
    }

    /**
     * Make the hash table logically empty.
     */
    public void makeEmpty() {
        doClear();
    }

    private void doClear() {
        occupied = 0;
        for(int i = 0; i < array.length; i++) {
            array[i] = null;
        }
    }
    
    public int myhash(AnyType x) {
        int hashVal;
        if (x instanceof FingerprintEntry) {
            FingerprintEntry y = (FingerprintEntry) x;
            hashVal = y.hashC();
            // if (y.getHash() == 49686) {
            //     System.out.println("y = " + y);
            // }
        }
        else {
            hashVal = x.hashCode();
        }
        // System.out.println("hashVal = " + hashVal);

        hashVal %= array.length;
        if(hashVal < 0) {
            hashVal += array.length;
        }

        return hashVal;
    }
    
    /**
     * Internal method to allocate array.
     * @param arraySize the size of the array.
     */
    private void allocateArray(int arraySize) {
        array = new HashEntry[nextPrime(arraySize)];
    }

    /**
     * Internal method to find a prime number at least as large as n.
     * @param n the starting number (must be positive).
     * @return a prime number larger than or equal to n.
     */
    private static int nextPrime(int n) {
        if(n%2 == 0) {
            n++;
        }
        while(!isPrime(n)) { n += 2; }
        return n;
    }

    /**
     * Internal method to test if a number is prime. Not an efficient
     * algorithm.
     * @param n the number to test.
     * @return the result of the test.
     */
    private static boolean isPrime( int n ) {
        if(n == 2 || n == 3) {
            return true;
        }
        
        if(n == 1 || n % 2 == 0) {
            return false;
        }

        for(int i = 3; i*i <= n; i += 2) {
            if(n%i == 0) {
                return false;
            }
        }
        return true;
    }

}
