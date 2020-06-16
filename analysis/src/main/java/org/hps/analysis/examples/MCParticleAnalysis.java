/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package org.hps.analysis.examples;

//import static java.lang.Math.atan2;
import java.util.List;
import org.lcsim.util.Driver;
import org.lcsim.util.aida.AIDA;
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
        for (final SimTrackerHit trckHits : trackHits) {
            final MCParticle mc = trckHits.getMCParticle();
            pdgId = mc.getPDGID();
            if (pdgId == 13) {
                id = mc.getType().getName();
                if (Math.abs(mc.getOriginZ()) == 7.5) {
                    aida.histogram1D(id + "/ZOrigin", 100, -10., -5.).fill(mc.getOriginZ());
                    aida.histogram2D(id + "/xVsyScore", 320, -270.0, 370.0, 90, -90.0, 90.0).fill(trckHits.getPositionVec().x(), trckHits.getPositionVec().y());
                    for (final SimCalorimeterHit cHits : calHits) {
                        if (mc.equals(cHits.getMCParticle(0))){
                            aida.histogram2D(id + "/xVsyECal", 320, -270.0, 370.0, 90, -90.0, 90.0).fill(cHits.getPositionVec().x(),cHits.getPositionVec().y());
                            aida.histogram2D(id + "/DelxVsDelY", 200,-100.,100.,200,-100.,100.).fill(Math.abs(trckHits.getPositionVec().x())-Math.abs(cHits.getPositionVec().x()),Math.abs(trckHits.getPositionVec().y())-Math.abs(cHits.getPositionVec().y()));
                            aida.histogram2D(id + "/TimeVsDelMag",100, 0. ,10., 200, 0., 200.).fill(cHits.getTime(),Math.abs(cHits.getPositionVec().magnitude())-Math.abs(trckHits.getPositionVec().magnitude()));
                        }
                    }
                }
            }
        }
    }
}
