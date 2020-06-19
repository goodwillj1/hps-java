package org.hps.analysis.examples;

//import static java.lang.Math.atan2;
import java.util.List;
import org.lcsim.util.Driver;
import org.lcsim.util.aida.AIDA;
import static java.lang.Math.atan2;

//import org.hps.analysis.ecal.EcalHitPlots;
//import org.hps.analysis.ecal.EcalHitPlots;
//import org.hps.recon.tracking.*;
import org.lcsim.event.*;
//import hep.physics.vec.*;
//import java.util.Arrays;
//import java.io.*;

/**
*
* @author goodwill
*/
public class MCParticleAnalysis extends Driver {
    
    private final AIDA aida = AIDA.defaultInstance();
    
    public void process(final EventHeader event) {
        // setupSensors(event);
        analyzeMC(event);
        // analyzeV0(event);
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


                    aida.histogram1D(id + "/ZOrigin", 100, -10., -5.).fill(mc.getOriginZ());
                    aida.histogram2D(id + "/xVsyScore", 800, -400.0, 400.0, 300, -150.0, 150.0).fill(trckHits.getPositionVec().x(), trckHits.getPositionVec().y());

                    int hitCount=0;
                    for (final SimCalorimeterHit cHits : calHits) {
                        if (trckHits.getMCParticle() == cHits.getMCParticle(0)){

                            hitCount++;

                            thetaY = atan2(cHits.getMCParticle(0).getMomentum().y(), cHits.getMCParticle(0).getMomentum().z());
                            thetaX = atan2(cHits.getMCParticle(0).getMomentum().x(), cHits.getMCParticle(0).getMomentum().z());
                            aida.histogram1D(id + "/momentum", 100, 0., 2.).fill(cHits.getMCParticle(0).getMomentum().magnitude());
                            aida.histogram1D(id + "/contEnergy", 100, 0., 0.3).fill(cHits.getContributedEnergy(0));
                            aida.histogram1D(id + "/corrEnergy", 100, 0., 0.3).fill(cHits.getCorrectedEnergy());
                            //aida.histogram2D(id + "/enVsx", 100, -0.5, 0.5, 320, -270.0, 370.0).fill(cHits.getPositionVec().x(), cHits.getContributedEnergy(0));
                            aida.histogram2D(id + "/xVsen",  320, -270.0, 370.0, 100, 0., 0.3).fill(trckHits.getPositionVec().x(), cHits.getContributedEnergy(0));
                            aida.histogram1D(id + "/thetaY", 100, -0.1, 0.1).fill(thetaY);
                            aida.histogram1D(id + "/thetaX", 100, -0.4, 0.4).fill(thetaX);
                            aida.histogram2D(id + "/xVsyECal", 800, -400.0, 400.0, 300, -150.0, 150.0).fill(cHits.getPositionVec().x(),cHits.getPositionVec().y());
                            delx = Math.abs(trckHits.getPositionVec().x())-Math.abs(cHits.getPositionVec().x());
                            dely = Math.abs(trckHits.getPositionVec().y())-Math.abs(cHits.getPositionVec().y());
                            aida.histogram2D(id + "/DelxVsDelY", 200,-100.,100.,200,-50.,50.).fill(delx,dely);
                            aida.histogram2D(id + "/TimeVsDelMag",100, 0. ,10., 200, 0., 200.).fill(cHits.getTime(),Math.sqrt(Math.pow(delx, 2) + Math.pow(dely, 2)));
                            if(cHits.getCorrectedEnergy() < 0.1){
                                aida.histogram2D(id + "/X vs Y Score < 0.1 GeV", 800, -400.0, 400.0, 300, -150.0, 150.0).fill(trckHits.getPositionVec().x(), trckHits.getPositionVec().y());
                            }
                        }
                    }
                    aida.histogram1D(id + "/numHits",20, 0., 20.).fill(hitCount);


                    if (hitCount>0 && hitCount<6) {
                        for (final SimCalorimeterHit cHits : calHits) {
                            if (trckHits.getMCParticle() == cHits.getMCParticle(0)){
                                aida.histogram1D(id+"/corrEnergy"+hitCount,8, 0., 8.).fill(hitCount);
                            }
                        }
                    }
                    

                }
            }
        }
    }
}
