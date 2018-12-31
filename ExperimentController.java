
/**
 * Sets up a hash table representing frequency content of several songs
 * and then queries it with some query hashes to simulate the Shazam
 * automatic music identification software.
 *
 * @author Tom Collins
 * @version 11/10/2018
 */

import java.util.*;
import java.io.*;

public class ExperimentController {
    // Instance variables.
    // Some DSP parameters.
    float step = 1024;
    int fs = 44100;
    // Most audio files contain 44,100 samples per second. The
    // following array contains the cumulative samples at which each of
    // the analyzed songs begin. The formula
    // cumuSamp[i] + tIdx*step
    // should be used when defining the cTimeSample value for a
    // fingerprint in song i, where tIdx is the time index of the first
    // pair.
    long[] cumuSamp = new long[] {0, 15137466, 23642641, 35555473, 47278225, 55347985};
    String[] fnams = new String[] {
            "02 The Greatest Man That Ever Lived (Variations On a Shaker Hymn)",
            "12 La Vie En Rose excerpt",
            "Jaymes Young - Don't You Know 44p1 kHz",
            "Kendrick Lamar - HUMBLE",
            "Labyrinth",
            "Pixies __ Where Is My Mind"
        };

    // Some fingerprinting parameters.
    float timeThresMin = (float) 0.1;
    float timeThresMax = 1; //had to make this larger
    int pidxThresMin = 0;
    int pidxThresMax = 150;

    // Make a new QPHT.
    QuadraticProbingHashTable<FingerprintEntry> allSongs
    = new QuadraticProbingHashTable<FingerprintEntry>();

    /**
     * MAIN!
     */
    public static void main(String[] args) {
        ExperimentController EC = new ExperimentController();
        EC.run();
    }

    /**
     * runs program and outputs estimates.csv
     */
    public void run() {
        // Construct fingerprint database.
        for (int i = 0; i < fnams.length; i++) {
            System.out.println("On " + fnams[i]);
            int[][] pks = spectralPeakReader("data/allSongs/" + fnams[i] + ".csv");
            createFingerprints(pks, cumuSamp[i], fnams[i]);
        } 

        // Query fingerprint database.
        String[] fnam_est = new String[50];    
        for (int i = 0; i < 50; i++) {
            fnam_est[i] = histogram(matchFingerprints(spectralPeakReader("data/snippet_batch/" + (i + 1) + ".csv")));
        }

        // Write the estimates to file.
        try {
            File estimates = new File("estimates.csv");
            PrintWriter pw_e = new PrintWriter(estimates);
            for (int i = 0; i < 50; i++) {
                pw_e.println(fnam_est[i]);
            }
            pw_e.close();
        }
        catch(Exception e) { }
    }

    /**
     * Reads csv file, and returns a 2d array of frequency and time
     * 
     * @param pathToCSVFile csv file path
     * @return 2d array of frequencies and times
     */
    public int[][] spectralPeakReader(String pathToCSVFile) {
        int[][] IJ;
        // I'm just initializing this to suppress the error.

        String line = "";
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(pathToCSVFile));
            int count = 0;
            while ((line = br.readLine()) != null) {
                count++;
            }
            br.close();
            IJ = new int[count][2];
            int c = 0;
            BufferedReader br2 = new BufferedReader(new FileReader(pathToCSVFile));
            while ((line = br2.readLine()) != null) {
                String[] l = line.split(",");
                for(int i = 0; i < 2; i++) {
                    IJ[c][i] = Integer.parseInt(l[i]);
                    IJ[c][i] = Integer.parseInt(l[i]);
                }
                c++;
            }
            br2.close();
        } 
        catch (Exception e) {
            IJ = new int[0][0];
            System.out.println(e);
        }
        return IJ;
    }

    /**
     * uses pairs of peaks to create fingerpirnts and adds them to allSong
     * 
     * @param IJ 2d array of frequencies and times
     * @param cTSamp time sample
     * @param sNam song name
     */
    public void createFingerprints(int[][] IJ, long cTSamp, String sNam) {
        int nfps = 0;
        for(int ii = 0; ii < IJ.length; ii++) {
            int jj = ii + 1;
            int[] ind1 = IJ[ii];
            while(jj < IJ.length) {
                int[] ind2 = IJ[jj];
                float timeDiff = (ind2[1] - ind1[1]) * step/fs;
                int freqDiff = Math.abs(ind2[0] - ind1[0]);
                if(timeDiff > timeThresMin && timeDiff < timeThresMax && freqDiff > pidxThresMin && freqDiff < pidxThresMax) {
                    Fingerprint fp = new Fingerprint(ind1[1], ind1[0], ind2[1], ind2[0], (long) ind1[1],sNam, step, fs);
                    FingerprintEntry fpe = new FingerprintEntry(fp);
                    nfps++;

                    allSongs.insert(fpe);
                }
                if(timeDiff >= timeThresMax) {
                    jj = IJ.length - 1;
                }
                jj++;
            }
        }
        System.out.println(nfps);
    }

    /**
     * uses pairs of preaks to create fingerprints and matches them to a song
     * 
     * @param IJ 2d array of frequencies and times
     * @return 2d arraylist of matching fingerprints
     */
    public ArrayList<ArrayList<Long>> matchFingerprints(int[][] IJ) {
        ArrayList<ArrayList<Long>> outArr = new ArrayList<ArrayList<Long>>();
        ArrayList<Long> outArr1 = new ArrayList<Long>();
        ArrayList<Long> outArr2 = new ArrayList<Long>();
        for(int ii = 0; ii < IJ.length; ii++) {
            int jj = ii + 1;
            int[] ind1 = IJ[ii];
            while(jj < IJ.length) {
                int[] ind2 = IJ[jj];
                float timeDiff = (ind2[1] - ind1[1]) * step/fs;
                int freqDiff = Math.abs(ind2[0] - ind1[0]);
                if(timeDiff > timeThresMin && timeDiff < timeThresMax && Math.abs(freqDiff) > pidxThresMin && Math.abs(freqDiff) < pidxThresMax) {
                    Fingerprint fp = new Fingerprint(ind1[1], ind1[0], ind2[1], ind2[0], ind1[1]  * Math.round(step), "blah", step, fs);
                    FingerprintEntry fpe = new FingerprintEntry(fp);
                    FingerprintEntry possibMatch = allSongs.contains(fpe);
                    if (possibMatch != null) {
                        //System.out.println("There's a match!");
                        ArrayList<Long> currTimeSamples = possibMatch.getCTimeSamples();
                        outArr1.addAll(currTimeSamples);
                        for(int p = 0; p < currTimeSamples.size(); p++) {
                            outArr2.add(fp.getCTimeSample());
                        }
                    }
                }
                if(timeDiff >= timeThresMax) {
                    jj = IJ.length - 1;
                }
                jj++;
            }
        }
        outArr.add(outArr1);
        outArr.add(outArr2);
        return outArr;
    }

    // Calculates a histogram for a transformation of the matching
    // fingerprints data, finds the maximum count in this histogram,
    // then works out and returns the name of the song to which this
    // maximum corresponds.
    public String histogram(ArrayList<ArrayList<Long>> m) {
        int nbins = 1000;
        // Sense checks for input.
        if (m.size() != 2 || m.get(0).size() != m.get(1).size()) {
            new Exception("Problem with dimensions of input array m.");
        }
        // Get the minimum and maximum matches in the transformed data.
        Long[] transf = new Long[m.get(0).size()];
        for (int j = 0; j < m.get(0).size(); j++) {
            transf[j] = m.get(0).get(j) - m.get(1).get(j);
        }

        int amin = argmin(transf);
        int amax = argmax(transf);
        // Define histogram edges.
        float binWidth = (transf[amax] - transf[amin])/nbins;
        float[] edges = new float[nbins + 1];
        // Could just be ints but enables easy reuse of argmax below.
        Long[] count = new Long[nbins];
        for (int i = 0; i < nbins; i++) {
            edges[i] = transf[amin] + binWidth*i;
            // Initialize count[i] to zero. (Avoids NPE below.)
            count[i] = (long) 0;
        }
        edges[nbins] = transf[amax];
        // Go over the transformed data and put each datum in the
        // appropriate histogram bin.
        for (int j = 0; j < transf.length; j++) {
            int i = 0;
            while (i < nbins) {
                if (transf[j] >= edges[i] && transf[j] < edges[i + 1]) {
                    count[i]++;
                    i = nbins - 1;
                }
                i++;
            }
        }
        // Find bin with maximum count.
        amax = argmax(count);
        // Finally, work out which song this corresponds to.
        double winningSample = edges[amax] + binWidth/2;
        int winningSongIdx = 0;
        int i = cumuSamp.length - 1;
        while (i >= 0) {
            if (winningSample >= cumuSamp[i]) {
                winningSongIdx = i;
                i = 0;
            }
            i--;
        }
        return fnams[winningSongIdx];
    }

    // Helper functions for histogram.
    public static int argmin(Long[] arr) {
        int idx = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[idx]) {
                idx = i;
            }
        }
        return idx;
    }

    public static int argmax(Long[] arr) {
        int idx = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[idx]) {
                idx = i;
            }
        }
        return idx;
    }
}
