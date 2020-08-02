package org.hps.analysis.examples;
//import hep.aida.IAnalysisFactory;
//import hep.aida.IDataPointSet;
//import hep.aida.IDataPointSetFactory;
//import hep.aida.IProfile1D;
//import hep.physics.vec.BasicHep3Matrix;
import hep.physics.vec.Hep3Vector;
//import hep.physics.vec.VecOp;
import static java.lang.Math.*;
//mport static java.lang.Math.atan2;
//import static java.lang.Math.atan;
//import hep.physics.vec.BasicHep3Matrix;
//import hep.physics.vec.Hep3Vector;
//import hep.physics.vec.VecOp;
//import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//import org.hps.recon.ecal.cluster.ClusterUtilities;
//import org.hps.recon.tracking.TrackType;
//import org.hps.recon.tracking.TrackType;
//import org.lcsim.detector.DetectorElementStore;
//import org.lcsim.detector.IDetectorElement;
//import org.lcsim.detector.identifier.IExpandedIdentifier;
//import org.lcsim.detector.identifier.IIdentifier;
//import org.lcsim.detector.identifier.IIdentifierDictionary;
//import org.lcsim.detector.tracker.silicon.HpsSiSensor;
//import org.lcsim.detector.tracker.silicon.SiSensor;
import org.lcsim.util.Driver;
import org.lcsim.util.aida.AIDA;
import org.hps.recon.tracking.*;
import org.lcsim.event.*;
import java.util.Random.*;

public class DataMuonAnalysis extends Driver {
    private final AIDA aida = AIDA.defaultInstance();
    
    public void process(final EventHeader event) {
        // setupSensors(event);
        //System.out.println();
        if (event.hasCollection(ReconstructedParticle.class)){
            analyzeData(event);
        }
        if(event.hasCollection(SimTrackerHit.class) || event.hasCollection(SimCalorimeterHit.class)){
            analyzeMC(event);
        }
        // analyzeV0(event);
    }
    
    
    private void analyzeData(final EventHeader event) {
        final List<ReconstructedParticle> rpList = event.get(ReconstructedParticle.class, "FinalStateParticles");
        List<Cluster> clusters;
        Track t = null;
        String id = null;
        double thetaY = 0;
        double thetaX = 0;
        Hep3Vector pmom = null;
        int pdgId;
        double delThetaX;
        for (final ReconstructedParticle rp : rpList) {
            pmom = rp.getMomentum();
            thetaY = atan2(pmom.y(), pmom.z());//asin(pmom.y() / pmom.magnitude());
            thetaX = atan2(pmom.x(), pmom.z());
            clusters = rp.getClusters();
            pdgId = rp.getParticleIDUsed().getPDG();
            if (pdgId == 11) {
                id = "mu-";
                t = rp.getTracks().get(0);
            }
            if (pdgId == -11){
                id = "mu+";
                t = rp.getTracks().get(0);
            }
            if(pdgId == 22){
                id = "photon";
                break;
            }
            final TrackState st = TrackUtils.getTrackStateAtECal(t);
            if (!clusters.isEmpty()) {
                final Cluster c = clusters.get(0);
                final double[] cPos = c.getPosition();
                final double[] ePos = st.getReferencePoint();
                //double[] ar = st.getMomentum();
                final double tanlam = st.getTanLambda();
                final double phi = st.getPhi();
                //System.out.println(ePos[1]);
                delThetaX = atan2(ePos[1],913) - phi;
                //System.out.println("z= " + ePos[0] + " x= " + ePos[1] + " y= " + ePos[2]);
                if(!Double.isNaN(delThetaX)){
                    //System.out.println(delThetaX);
                    aida.histogram2D(id + "/enDelThetaX",100, 0., 0.3, 100, -0.2, 0.2).fill(c.getEnergy(), delThetaX);
                }
                aida.histogram1D(id + "/tanlam", 100, -2., 2.).fill(tanlam);
                aida.histogram1D(id + "/momentum", 100, 0., 2.).fill(pmom.magnitude());
                aida.histogram1D(id + "/thetaY", 100, -0.1, 0.1).fill(thetaY);
                aida.histogram1D(id + "/thetaX", 100, -0.4, 0.4).fill(thetaX);
                aida.histogram1D(id + "/chi2", 100, 0., 100.).fill(t.getChi2());
                aida.histogram2D(id + "/XYECal",  800, -400.0, 400.0, 300, -150.0, 150.0).fill(ePos[1], ePos[2]);
                aida.histogram2D(id + "/delXdelY", 200,-100.,100.,200,-50.,50.).fill(ePos[1] - cPos[0], ePos[2] - cPos[1]);
                aida.histogram1D(id + "/CaltoTrack", 80, 0., 40.).fill(sqrt(pow(ePos[1] - cPos[0], 2) + pow(ePos[2] - cPos[1], 2)));
                aida.histogram2D(id + "/EnergyVsmom", 100, 0., 3., 100, 0., 3.).fill(c.getEnergy(), pmom.magnitude());
                aida.histogram1D(id + "/Energy\\mom", 100, 0., 1.).fill(c.getEnergy() / pmom.magnitude());
                aida.histogram1D(id + "/energy", 100, 0., 0.3).fill(c.getEnergy());
                aida.histogram1D(id + "/hits",20 ,0.,20.).fill(c.getCalorimeterHits().size());
                aida.histogram2D(id + "/XEn",  320, -270.0, 370.0, 100, 0., 0.3).fill(ePos[1], c.getEnergy());
                if(c.getCalorimeterHits().size() > 0 || c.getCalorimeterHits().size() < 6){
                    aida.histogram1D(id + "/sumEnergy" + c.getCalorimeterHits().size(),100 ,0.,0.3).fill(c.getEnergy());
                }
            }
        } 
    }
    
    private void analyzeMC(final EventHeader event) {
        // List<MCParticle> mcList = event.get(MCParticle.class, "MCParticle");
        final List<SimCalorimeterHit> calHits = event.get(SimCalorimeterHit.class, "EcalHits");
        final List<SimTrackerHit> trackHits = event.get(SimTrackerHit.class, "TrackerHitsECal");
        Random random = new Random();
        //double sigma;
        int pdgId;
        String id;
        double thetaY;
        double thetaX;
        double delx;
        double dely;
        int hitCount = 0;
        double sumEnergy = 0;
        double smearEnergy = 0;
        double delThetaX;
        double[] ar;
        //double thX;
        for (final SimTrackerHit trckHits : trackHits) {
            
            final MCParticle mc = trckHits.getMCParticle();
            pdgId = mc.getPDGID();
            if (pdgId == -13 || pdgId == 13) {
                id = mc.getType().getName();
                if (abs(mc.getOriginZ()) == 7.5) {
                    
                    
                    //aida.histogram1D(id + "/ZOrigin", 100, -10., -5.).fill(mc.getOriginZ());
                    aida.histogram2D(id + "/XYECal", 800, -400.0, 400.0, 300, -150.0, 150.0).fill(trckHits.getPositionVec().x(), trckHits.getPositionVec().y());
                    
                    hitCount=0;
                    sumEnergy=0;
                    for (final SimCalorimeterHit cHits : calHits) {
                        if (trckHits.getMCParticle() == cHits.getMCParticle(0)){
                            if(cHits.getCorrectedEnergy() > 0.01){
                                smearEnergy= cHits.getCorrectedEnergy() + random.nextGaussian() * 0.014;
                                //System.out.println(random.nextGaussian() * 0.02);
                                hitCount++;
                                sumEnergy += cHits.getContributedEnergy(0) + random.nextGaussian() * 0.02;
                                thetaY = atan2(cHits.getMCParticle(0).getMomentum().y(), cHits.getMCParticle(0).getMomentum().z());
                                thetaX = atan2(cHits.getMCParticle(0).getMomentum().x(), cHits.getMCParticle(0).getMomentum().z());
                                ar = trckHits.getMomentum();
                                //System.out.println(ar[0] + " " + ar[1] + " " + ar[2]); 
                                delThetaX = atan2(trckHits.getPositionVec().x(), 913.) - atan2(ar[0], ar[2]);
                                if(!Double.isNaN(delThetaX)){
                                    aida.histogram2D(id + "/enDelThetaX",100, 0., 0.3, 100, -0.2, 0.2).fill(cHits.getContributedEnergy(0), delThetaX);
                                }
                                aida.histogram1D(id + "/momentum", 100, 0., 2.).fill(cHits.getMCParticle(0).getMomentum().magnitude());
                                aida.histogram1D(id + "/thetaY", 100, -0.1, 0.1).fill(thetaY);
                                aida.histogram1D(id + "/thetaX", 100, -0.4, 0.4).fill(thetaX);
                                aida.histogram1D(id + "/phi", 100, -0.4, 0.4).fill(delThetaX);
                                aida.histogram1D(id + "/xTheta", 100, -0.4, 0.4).fill(atan2(ar[1], ar[2]));
                                delx = abs(trckHits.getPositionVec().x())-abs(cHits.getPositionVec().x());
                                dely = abs(trckHits.getPositionVec().y())-abs(cHits.getPositionVec().y());
                                if(abs(dely) < 50){
                                    aida.histogram2D(id + "/delXdelY", 200,-100.,100.,200,-50.,50.).fill(delx,dely);
                                }
                                aida.histogram2D(id + "/XEn",  320, -270.0, 370.0, 100, 0., 0.3).fill(trckHits.getPositionVec().x(), cHits.getContributedEnergy(0));
                                //aida.histogram1D(id + "/energy", 100, 0., 0.3).fill(cHits.getCorrectedEnergy());
                                aida.histogram1D(id + "/energy", 100, 0., 0.3).fill(cHits.getContributedEnergy(0));
                                aida.histogram2D(id + "/EnergyVsmom", 100, 0., 3., 100, 0., 3.).fill(cHits.getCorrectedEnergy(), cHits.getMCParticle(0).getMomentum().magnitude());
                                aida.histogram1D(id + "/Energy\\mom", 100, 0., 1.).fill(cHits.getContributedEnergy(0)/cHits.getMCParticle(0).getMomentum().magnitude());
                            }
                        }
                    }
                    aida.histogram1D(id + "/hits",20, 0., 20.).fill(hitCount);
                    aida.histogram1D(id+"/sumEnergy"+hitCount,100, 0., 0.3).fill(smearEnergy);
                    if (hitCount>0 && hitCount<6) {
                        for (final SimCalorimeterHit cHits : calHits) {
                            if (trckHits.getMCParticle() == cHits.getMCParticle(0)){
                                if(cHits.getCorrectedEnergy() > 0.01){
                                    aida.histogram1D(id+"/corrEnergy"+hitCount,100, 0., 0.3).fill(cHits.getCorrectedEnergy());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}