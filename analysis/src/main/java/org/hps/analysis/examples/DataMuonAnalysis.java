package org.hps.analysis.examples;
//import hep.aida.IAnalysisFactory;
//import hep.aida.IDataPointSet;
//import hep.aida.IDataPointSetFactory;
//import hep.aida.IProfile1D;
//import hep.physics.vec.BasicHep3Matrix;
import hep.physics.vec.Hep3Vector;
//import hep.physics.vec.VecOp;
//import static java.lang.Math.abs;
import static java.lang.Math.atan2;
//import hep.physics.vec.BasicHep3Matrix;
//import hep.physics.vec.Hep3Vector;
//import hep.physics.vec.VecOp;
//import static java.lang.Math.atan2;
//import java.util.ArrayList;
import java.util.List;
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
        List<ReconstructedParticle> rpList = event.get(ReconstructedParticle.class, "FinalStateParticles");
        List<Cluster> clusters;
        Track t = null;
        String id = null;
        double thetaY = 0;
        double thetaX = 0;
        Hep3Vector pmom = null;
        int pdgId;
        double sumEnergy = 0;
        for (ReconstructedParticle rp : rpList) {
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
            TrackState st2 = TrackUtils.getTrackStateAtECal(t);
            if (!clusters.isEmpty()) {
                Cluster c = clusters.get(0);
                double[] cPos = c.getPosition();
                aida.histogram1D(id + "/momentum", 100, 0., 2.).fill(pmom.magnitude());
                aida.histogram1D(id + "/thetaY", 100, -0.1, 0.1).fill(thetaY);
                aida.histogram1D(id + "/thetaX", 100, -0.4, 0.4).fill(thetaX);
                aida.histogram1D(id + "/chi2", 100, 0., 100.).fill(t.getChi2());
                double[] ePos = st2.getReferencePoint();
                aida.histogram2D(id + "/XYECal",  800, -400.0, 400.0, 300, -150.0, 150.0).fill(ePos[1], ePos[2]);
                aida.histogram2D(id + "/delXdelY", 200,-100.,100.,200,-50.,50.).fill(ePos[1] - cPos[0], ePos[2] - cPos[1]);
                aida.histogram1D(id + "/CaltoTrack", 80, 0., 40.).fill(Math.sqrt(Math.pow(ePos[1] - cPos[0], 2) + Math.pow(ePos[2] - cPos[1], 2)));
                aida.histogram2D(id + "/EnergyVsmom", 100, 0., 3., 100, 0., 3.).fill(c.getEnergy(), pmom.magnitude());
                aida.histogram1D(id + "/Energy\\mom", 100, 0., 1.).fill(c.getEnergy() / pmom.magnitude());
                aida.histogram1D(id + "/energy", 100, 0., 0.3).fill(c.getEnergy());
                aida.histogram1D(id + "/hits",20 ,0.,20.).fill(c.getCalorimeterHits().size());
                //aida.histogram1D(id + "/sumHits",10 ,0.,10.).fill(c.getCalorimeterHits().get(0).getCorrectedEnergy());
                aida.histogram2D(id + "/XEn",  320, -270.0, 370.0, 100, 0., 0.3).fill(ePos[1], c.getEnergy());
                if(c.getCalorimeterHits().size() > 0 || c.getCalorimeterHits().size() < 6){
                    aida.histogram1D(id + "/sumHits" + c.getCalorimeterHits().size(),100 ,0.,0.3).fill(c.getEnergy());
                }
            }
        }
        
        
        
    }
    
    private void analyzeMC(final EventHeader event) {
        // List<MCParticle> mcList = event.get(MCParticle.class, "MCParticle");
        final List<SimCalorimeterHit> calHits = event.get(SimCalorimeterHit.class, "EcalHits");
        final List<SimTrackerHit> trackHits = event.get(SimTrackerHit.class, "TrackerHitsECal");
        int pdgId;
        String id;
        double thetaY;
        double thetaX;
        double delx;
        double dely;
        for (final SimTrackerHit trckHits : trackHits) {
            
            final MCParticle mc = trckHits.getMCParticle();
            pdgId = mc.getPDGID();
            if (pdgId == -13 || pdgId == 13) {
                id = mc.getType().getName();
                if (Math.abs(mc.getOriginZ()) == 7.5) {
                    
                    
                    //aida.histogram1D(id + "/ZOrigin", 100, -10., -5.).fill(mc.getOriginZ());
                    aida.histogram2D(id + "/XYECal", 800, -400.0, 400.0, 300, -150.0, 150.0).fill(trckHits.getPositionVec().x(), trckHits.getPositionVec().y());
                    
                    int hitCount=0;
                    double sumEnergy = 0;
                    for (final SimCalorimeterHit cHits : calHits) {
                        if (trckHits.getMCParticle() == cHits.getMCParticle(0)){
                            if(cHits.getCorrectedEnergy() > 0.1){
                                hitCount++;
                                sumEnergy += cHits.getContributedEnergy(0);
                                thetaY = atan2(cHits.getMCParticle(0).getMomentum().y(), cHits.getMCParticle(0).getMomentum().z());
                                thetaX = atan2(cHits.getMCParticle(0).getMomentum().x(), cHits.getMCParticle(0).getMomentum().z());
                                aida.histogram1D(id + "/momentum", 100, 0., 2.).fill(cHits.getMCParticle(0).getMomentum().magnitude());
                                aida.histogram1D(id + "/thetaY", 100, -0.1, 0.1).fill(thetaY);
                                aida.histogram1D(id + "/thetaX", 100, -0.4, 0.4).fill(thetaX);
                                delx = Math.abs(trckHits.getPositionVec().x())-Math.abs(cHits.getPositionVec().x());
                                dely = Math.abs(trckHits.getPositionVec().y())-Math.abs(cHits.getPositionVec().y());
                                if(Math.abs(dely) < 50){
                                    aida.histogram2D(id + "/delXdelY", 200,-100.,100.,200,-50.,50.).fill(delx,dely);
                                }
                                //aida.histogram2D(id + "/TimeVsDelMag",100, 0. ,10., 200, 0., 200.).fill(cHits.getTime(),Math.sqrt(Math.pow(delx, 2) + Math.pow(dely, 2)));
                                //aida.histogram2D(id + "/X vs Y Score < 0.1 GeV", 800, -400.0, 400.0, 300, -150.0, 150.0).fill(trckHits.getPositionVec().x(), trckHits.getPositionVec().y());
                                aida.histogram2D(id + "/XEn",  320, -270.0, 370.0, 100, 0., 0.3).fill(trckHits.getPositionVec().x(), cHits.getContributedEnergy(0));
                                aida.histogram1D(id + "/energy", 100, 0., 0.3).fill(cHits.getCorrectedEnergy());
                                aida.histogram2D(id + "/EnergyVsmom", 100, 0., 3., 100, 0., 3.).fill(cHits.getCorrectedEnergy(), cHits.getMCParticle(0).getMomentum().magnitude());
                                aida.histogram1D(id + "/Energy\\mom", 100, 0., 1.).fill(cHits.getContributedEnergy(0)/cHits.getMCParticle(0).getMomentum().magnitude());
                            }
                        }
                    }
                    aida.histogram1D(id + "/hits",20, 0., 20.).fill(hitCount);
                    
                    if (hitCount>0 && hitCount<6) {
                        for (final SimCalorimeterHit cHits : calHits) {
                            if (trckHits.getMCParticle() == cHits.getMCParticle(0)){
                                if(cHits.getCorrectedEnergy() > 0.1){
                                    //if(hitCount == 1){
                                    //    aida.histogram2D(id + "/1HitXvsEn",  320, -270.0, 370.0, 100, 0., 0.3).fill(trckHits.getPositionVec().x(), cHits.getContributedEnergy(0));
                                    //}
                                    aida.histogram1D(id+"/corrEnergy"+hitCount,100, 0., 0.3).fill(cHits.getContributedEnergy(0));
                                    aida.histogram1D(id+"/sumHits"+hitCount,100, 0., 0.3).fill(sumEnergy);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}