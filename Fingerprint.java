public class Fingerprint
{
    private String key, songName;
    private long cTimeSample;
    

    /**
     * creates new fingerprint
     * 
     * @param ti1 time1
     * @param ti2 time2
     * @param fi1 frequency1
     * @param fi2 frequency2
     * @param tc time sample
     * @param sn song name
     * @param step step
     * @param sr step ratio
     */
    public Fingerprint(int ti1, int fi1, int ti2, int fi2, long tc, String sn, float step, int sr) {
        cTimeSample = tc;
        songName = sn;
        int tIdxDiff = ti2 - ti1;
        int fIdxDiff = fi2 - fi1;
        double tDiff = tIdxDiff * step / sr;
        key = "";
        key += (tDiff + fIdxDiff);
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
     * return song name
     * 
     * @return song name
     */
    public String getSongName() {
        return songName;
    }
    
    /**
     * returns time sample
     * 
     * @return time sample
     */
    public long getCTimeSample() {
        return cTimeSample;
    }
}
