import java.util.*;
public class FingerprintEntry
{
    private String key;
    private int hash;
    private ArrayList<Long> cTimeSamples = new ArrayList<Long>();
    private ArrayList<String> songNames = new ArrayList<String>();

    /**
     * creates new fingerprint entry
     * 
     * @param fp fingerprint
     */
    public FingerprintEntry(Fingerprint fp) {
        key = fp.getKey();
        cTimeSamples.add(fp.getCTimeSample());
        songNames.add(fp.getSongName());
        hash = hashC();
    }

    
    /**
     * creates hashcode for fingerprint entry
     * 
     * @return hashcode
     */
    public int hashC() {
        int hashVal = 0;

        for( int i = 0; i < key.length( ); i++ )
            hashVal = 37 * hashVal + key.charAt( i );

        hashVal %= cTimeSamples.size();
        if( hashVal < 0 )
            hashVal += cTimeSamples.size();

        return hashVal;
    }

    /**
     * adds fingerprint entry data to another fingerprint entry
     * 
     * @param fpe fingerprint entry
     */
    public void add(FingerprintEntry fpe) {
        cTimeSamples.add(fpe.getCTimeSample());
        songNames.add(fpe.getSongName());
    }

    /**
     * returns key
     * 
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * returns hash
     * 
     * @return hash
     */
    public int getHash() {
        return hash;
    }

    /**
     * returns cTimeSamples
     * 
     * @return cTimeSamples arraylist
     */
    public ArrayList<Long> getCTimeSamples() {
        return cTimeSamples;
    }

    /**
     * returns cTimeSample
     * 
     * @return cTimeSample
     */
    public long getCTimeSample() {
        return cTimeSamples.get(0);
    }
    
    /**
     * returns song name
     * 
     * @return song name
     */
    public String getSongName() {
        return songNames.get(0);
    }
}
