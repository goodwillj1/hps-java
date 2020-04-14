package org.hps.recon.tracking.kalman;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

// Parameters used by the Kalman-Filter pattern recognition and fitting
public class KalmanParams {
    static final int nTries = 2;  // Number of iterations through the entire pattern recognition; not configurable
    int nIterations;              // Number of Kalman-fit iterations in the final fit
    double[] kMax; 
    double[] tanlMax; 
    double[] dRhoMax; 
    double[] dzMax;
    double[] chi2mx1; 
    int minHits0;
    int[] minHits1; 
    double mxChi2Inc; 
    double minChi2IncBad;
    double[] mxResid; 
    double mxResidShare; 
    double mxChi2double;
    int firstLayer;
    int mxShared; 
    int [] minStereo;
    int minAxial;
    double mxTdif;
    double seedCompThr;           // Compatibility threshold for seedTracks helix parameters
    ArrayList<int[]> [] lyrList;
    double [] beamSpot;
    private int[] Swap = {1,0, 3,2, 5,4, 7,6, 9,8, 11,10, 13,12};
    private Logger logger;
    
    KalmanParams() {
        
        logger = Logger.getLogger(KalmanParams.class.getName());;
        
        kMax = new double[nTries];
        tanlMax = new double[nTries];
        dRhoMax = new double[nTries];
        dzMax = new double[nTries];
        chi2mx1 = new double[nTries];
        minHits1 = new int[nTries];
        mxResid = new double[nTries];
        minStereo = new int[nTries];  
        
        // Set all the default values
        // Cut and parameter values (length units are mm, time is ns).
        // The index is the iteration number.
        // The second iteration generally will have looser cuts.
        
        nIterations = 1;    // Number of Kalman filter iterations per track in the final fit
        kMax[0] = 3.0;      // Maximum curvature for seed
        kMax[1] = 6.0;      
        tanlMax[0] = 0.08;  // Maximum tan(lambda) for seed
        tanlMax[1] = 0.12;
        dRhoMax[0] = 15.;   // Maximum dRho at target plane for seed
        dRhoMax[1] = 25.;
        dzMax[0] = 3.;      // Maximum z at target plane for seed
        dzMax[1] = 10.;
        chi2mx1[0] = 8.0;   // Maximum chi**2/#hits for good track
        chi2mx1[1] = 12.0;  
        minHits0 = 6;       // Minimum number of hits in the initial outward filtering (including 5 from the seed)
        minHits1[0] = 7;    // Minimum number of hits for a good track
        minHits1[1] = 6;
        mxChi2Inc = 2.;     // Maximum increment to the chi^2 to add a hit to a completed track 
        minChi2IncBad = 10.; // Threshold for removing a bad hit from a track candidate
        mxResid[0] = 50.;   // Maximum residual, in units of detector resolution, for picking up a hit
        mxResid[1] = 100.;
        mxResidShare = 10.; // Maximum residual, in units of detector resolution, for a hit to be shared
        mxChi2double = 6.;  // Maximum chi^2 increment to keep a shared hit
        minStereo[0] = 4;
        minStereo[1] = 3;   // Minimum number of stereo hits
        minAxial = 2;       // Minimum number of axial hits
        mxShared = 2;       // Maximum number of shared hits
        mxTdif = 30.;       // Maximum time difference of hits in a track
        firstLayer = 0;     // First layer in the tracking system (2 for pre-2019 data)
        seedCompThr = -1.;  // Remove SeedTracks with all Helix params within relative seedCompThr . If -1 do not apply duplicate removal
        
        // Load the default search strategies
        // Index 0 is for the bottom tracker (+z), 1 for the top (-z)
        lyrList = new ArrayList[2];
        for (int i=0; i<2; ++i) {
            lyrList[i] = new ArrayList<int[]>(15);
        }
        final int[] list0 = {6, 7, 8, 9, 10};
        final int[] list1 = {4, 5, 6, 7, 8};
        final int[] list2 = {5, 6, 8, 9, 10};
        final int[] list3 = {5, 6, 7, 8, 10};
        final int[] list4 = { 3, 6, 8, 9, 10 };
        final int[] list5 = { 4, 5, 8, 9, 10 };
        final int[] list6 = { 4, 6, 7, 8, 9 };
        final int[] list7 = { 4, 6, 7, 9, 10 };
        final int[] list8 = { 2, 5, 8, 9, 12};
        final int[] list9 = { 8, 10, 11, 12, 13};
        final int[] list10 = {6, 9, 10, 11, 12};
        final int[] list11 = {6, 7, 9, 10, 12};
        final int[] list12 = {2, 3, 4, 5, 6};
        final int[] list13 = {2, 4, 5, 6, 7};
        final int[] list14 = {6, 7, 8, 10, 11};
        lyrList[0].add(list0);
        lyrList[0].add(list1);
        lyrList[0].add(list2);
        lyrList[0].add(list3);
        lyrList[0].add(list4);
        lyrList[0].add(list5);
        lyrList[0].add(list6);
        lyrList[0].add(list7);
        lyrList[0].add(list8);
        lyrList[0].add(list9);
        lyrList[0].add(list10);
        lyrList[0].add(list11);
        lyrList[0].add(list12);
        lyrList[0].add(list13);
        lyrList[0].add(list14);
        
        // Beam spot defaults to the origin. For now, at least, must be set to the proper value by the user.
        beamSpot = new double[3];
        beamSpot[0] = 0.;
        beamSpot[1] = 0.;
        beamSpot[2] = 0.;
        
        // Swap axial/stereo in list entries for the top tracker
        for (int[] list: lyrList[0]) {
            int [] listTop = new int[5];
            for (int i=0; i<5; ++i) {
                listTop[i] = Swap[list[i]];
            }
            for (int i=0; i<4; ++i) {
                if (listTop[i] > listTop[i+1]) { // Sorting entries. No more than one swap should be necessary.
                    int tmp = listTop[i];
                    listTop[i] = listTop[i+1];
                    listTop[i+1] = tmp;
                }
            }
            lyrList[1].add(listTop);
        }       
    }
    
    public void setFirstLayer(int firstLayer) {
        if (firstLayer != 0 && firstLayer != 2) {
            logger.log(Level.WARNING,String.format("First layer of %d is not valid and is ignored.", firstLayer));
        }
        logger.log(Level.INFO, String.format("Setting the first tracking layer to %d", firstLayer));
        this.firstLayer = firstLayer;
    }
    
    public void setIterations(int N) {
        if (N < 1) {
            logger.log(Level.WARNING,String.format("%d iterations not allowed.", N));
            return;
        }
        logger.log(Level.INFO,String.format("Setting the number of Kalman Filter iterations to %d.", N));
        nIterations = N;
    }
    
    public void setMxChi2Inc(double mxC) {
        if (mxC <= 1.) {
            logger.log(Level.WARNING,String.format("Maximum chi^2 increment must be at least unity. %8.2f not valid.", mxC));
            return;
        }
        logger.log(Level.INFO,String.format("Maximum chi^2 increment to add a hit to a track to %8.2f.", mxC));
        mxChi2Inc = mxC;
    }
    
    public void setMxChi2double(double mxDb) {
        if (mxDb <= 0.) {
            logger.log(Level.WARNING,String.format("Maximum chi^2 increment of shared hit of %8.2f not allowed.", mxDb));
            return;
        }
        logger.log(Level.INFO,String.format("Setting the maximum chi^2 increment of shared hit to %8.2f sigma.", mxDb));
        mxChi2double = mxDb;  
    }
    
    public void setMinChi2IncBad(double mnB) {
        if (mnB <= 3.0) {
            logger.log(Level.WARNING,String.format("Minimum chi^2 increment to remove a bad hit must be at least 3. %8.2f not valid.", mnB));
            return;
        }
        logger.log(Level.INFO,String.format("Setting the minimum chi^2 increment to remove a bad hit to %8.2f.", mnB));
        minChi2IncBad = mnB;        
    }
    
    public void setMxResidShare(double mxSh) {
        if (mxSh <= 0.) {
            logger.log(Level.WARNING,String.format("Maximum residual of shared hit of %8.2f not allowed.", mxSh));
            return;
        }
        logger.log(Level.INFO,String.format("Setting the maximum residual for a shared hit to %8.2f sigma.", mxSh));
        mxResidShare = mxSh;  
    }
    
    public void setMaxK(double kMx) {
        if (kMx <= 0.) {
            logger.log(Level.WARNING,String.format("Max 1/pt of %8.2f not allowed.", kMx));
            return;
        }
        logger.log(Level.INFO,String.format("Setting the maximum 1/pt to %8.2f.", kMx));
        kMax[1] = kMx;
        kMax[0] = Math.min(kMax[0], 0.6*kMx);
    }
    
    public void setMxResid(double mxR) {
        if (mxR <= 1.) {
            logger.log(Level.WARNING,String.format("Max resid of %8.2f not allowed.", mxR));
            return;
        }
        logger.log(Level.INFO,String.format("Setting the maximum residual to pick up hits to %8.2f sigma.", mxR));
        mxResid[1] = mxR;
        mxResid[0] = Math.min(mxResid[0], 0.5*mxR);
    }
    
    public void setMaxTanL(double tlMx) {
        if (tlMx <= 0.) {
            logger.log(Level.WARNING,String.format("Max seed tan(lambda) of %8.2f not allowed.", tlMx));
            return;
        }
        logger.log(Level.INFO,String.format("Setting the maximum seed tan(lambda) to %8.2f.", tlMx));
        tanlMax[1] = tlMx;
        tanlMax[0] = Math.min(tanlMax[0], 0.8*tlMx);
    }
    
    public void setMaxdRho(double dMx) {
        if (dMx <= 0.0) {
            logger.log(Level.WARNING,String.format("Max dRho of %8.2f not allowed.", dMx));
            return;
        }
        logger.log(Level.INFO,String.format("Setting the maximum dRho to %8.2f mm.", dMx));
        dRhoMax[1] = dMx;
        dRhoMax[0] = Math.min(dRhoMax[0], 0.6*dMx);
    }
    
    public void setMaxdZ(double zMx) {
        if (zMx <= 0.0) {
            logger.log(Level.WARNING,String.format("Max dZ of %8.2f not allowed.", zMx));
            return;
        }
        logger.log(Level.INFO,String.format("Setting the maximum dz to %8.2f mm.", zMx));
        dzMax[1] = zMx;
        dzMax[0] = Math.min(dzMax[0], 0.6*zMx);
    }
    
    public void setMaxChi2(double xMx) {
        if (xMx <= 0.) {
            logger.log(Level.WARNING,String.format("Max chi2 of %8.2f not allowed.", xMx));
            return;
        }
        logger.log(Level.INFO,String.format("Setting the maximum chi^2/hit to %8.2f.", xMx));
        chi2mx1[1] = xMx;
        chi2mx1[0] = Math.min(chi2mx1[0], 0.6*xMx);
    }
    
    public void setMinHits(int minH) {
        if (minH < 5) {
            logger.log(Level.WARNING,String.format("Minimum number of hits = %d not allowed.", minH));
            return;
        }
        logger.log(Level.INFO,String.format("Setting the minimum number of hits to %d.", minH));
        minHits1[1] = minH;
        minHits1[0] = Math.max(minHits1[0], minH+1);
    }
    
    public void setMinStereo(int minS) {
        if (minS < 3) {
            logger.log(Level.WARNING,String.format("Minimum number of stereo hits = %d not allowed.", minS));
            return;
        }
        logger.log(Level.INFO,String.format("Setting the minimum number of stereo hits to %d.", minS));
        minStereo[1] = minS;
        minStereo[0] = Math.max(minStereo[0], minS+1);
    }
    
    public void setMaxShared(int mxSh) {
        logger.log(Level.INFO,String.format("Setting the maximum number of shared hits to %d.", mxSh));
        mxShared = mxSh;
    }
    
    public void setMaxTimeRange(double mxT) {
        logger.log(Level.INFO,String.format("Setting the maximum time range for hits on a track to %8.2f ns.", mxT));
        mxTdif = mxT;
    }

    public void setSeedCompThr(double seedComp_Thr) {      
        if (seedComp_Thr < 0.) {
            logger.log(Level.INFO, "SeedTracks duplicate removal is turned off.");
            return;
        }
        logger.log(Level.INFO, String.format("Setting the SeedTracks duplicate removal threshold to %f percent.",seedComp_Thr*100.));
        seedCompThr = seedComp_Thr;
    }

    public void setBeamSpot(double spot) {
        beamSpot[0] = 0.;
        beamSpot[1] = spot;
        beamSpot[2] = 0.;
        logger.log(Level.INFO, String.format("Setting the beam spot location to %f %f %f", beamSpot[0], beamSpot[1], beamSpot[2]));       
    }
    
    public void clrStrategies() {
        logger.log(Level.INFO,String.format("Clearing all lists of search strategies.."));
        lyrList[0].clear();
        lyrList[1].clear();
    }
    
    // Add a search strategy for the bottom and top trackers
    public boolean addStrategy(int [] list) {
        if (!addStrategy(list,0)) return false;
        // Swap axial/stereo in list entries for the top tracker
        int [] listTop = new int[5];
        for (int i=0; i<5; ++i) {
            listTop[i] = Swap[list[i]];
        }
        for (int i=0; i<4; ++i) {
            if (listTop[i] > listTop[i+1]) { // Sorting entries. No more than one swap should be necessary.
                int tmp = listTop[i];
                listTop[i] = listTop[i+1];
                listTop[i+1] = tmp;
            }
        } 
        if (!addStrategy(listTop,1)) return false;
        return true;
    }
    
    // Add a search strategy for just the bottom or top tracker
    public boolean addStrategy(int [] list, int topBottom) {
        int nAxial = 0;
        int nStereo = 0;
        for (int i=0; i<5; ++i) {
            if (list[i]%2 == 0) {
                if (topBottom == 0) nStereo++;
                else nAxial++;
            } else {
                if (topBottom == 0) nAxial++;
                else nStereo++;
            }
        }
        if (nAxial != 2 || nStereo != 3) {
            logger.log(Level.WARNING,String.format("addStrategy: Invalid search strategy for topBottom=%d: %d %d %d %d %d", 
                    topBottom, list[0],list[1],list[2],list[3],list[4]));
            return false;
        }
        for (int [] oldList : lyrList[topBottom]) {
            int nMatch = 0;
            for (int i=0; i<5; ++i) {
                if (oldList[i] == list[i]) nMatch++;
            }
            if (nMatch == 5) {
                logger.log(Level.WARNING,String.format("addStrategy: strategy %d %d %d %d %d is already in the list", list[0],list[1],list[2],list[3],list[4]));
                return false;
            }
        }
        logger.log(Level.INFO,String.format("addStrategy: adding search strategy %d %d %d %d %d for topBottom=%d", 
                list[0],list[1],list[2],list[3],list[4],topBottom));
        lyrList[topBottom].add(list);
        return true;
    }
}
