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
    
    private AIDA aida = AIDA.defaultInstance();
    
    public void process(EventHeader event) {
        //setupSensors(event);
        analyzeMC(event);
        //analyzeV0(event);
    }
    
    private void analyzeMC(EventHeader event) {
        //List<MCParticle> mcList = event.get(MCParticle.class, "MCParticle");
        List<SimCalorimeterHit> calHits = event.get(SimCalorimeterHit.class, "EcalHits");
        List<SimTrackerHit> trackHits = event.get(SimTrackerHit.class, "TrackerHitsECal");
        int pdgId;
        String id;
        for (SimTrackerHit trckHits : trackHits) {
            MCParticle mc = trckHits.getMCParticle();
            pdgId = mc.getPDGID();
            
            //System.out.println("prod = " + mc.getProductionTime());
            if (pdgId == 13) {
                if (event.hasCollection(SimCalorimeterHit.class, "EcalHits") && event.hasCollection(SimTrackerHit.class, "TrackerHitsECal")) {
                    id= mc.getType().getName();
                    //System.out.println("zorigin " + mc.getOriginZ());
                    if(mc.getOriginZ() == -7.5){
                        //System.out.println("zorigin " + mc.getOriginZ());
                        aida.histogram1D(id + "/ZOrigin", 100, -10., -5.).fill(mc.getOriginZ());
                        aida.histogram2D(id + "/xVsyScore", 100, -5., 5., 100, -5., 5.).fill(mc.getOriginX(),mc.getOriginY());
                        //aida.histogram2D(id + "/TimeVsScorePos",100, -0.1,0.1,200,-10.,10.).fill(mc.getProductionTime(),mc.getOriginZ());
                        for(SimCalorimeterHit cHits : calHits){
                            MCParticle cMc = cHits.getMCParticle(0);
                            aida.histogram2D(id + "/xVsyECal", 320, -270.0, 370.0, 90, -90.0, 90.0).fill(cMc.getOriginX(),cMc.getOriginY());
                            if(cMc.getOriginZ() == -7.5){
                                aida.histogram2D(id + "/DelxVsDelY", 100,-2.,2.,100,-2.,2.).fill(mc.getOriginX()-cMc.getOriginX(),mc.getOriginY()-cMc.getOriginY());
                            }
                            aida.histogram2D(id + "/TimeVsDelZ",100, -0.1,0.1,200,-10.,10.).fill(mc.getProductionTime(),mc.getOriginZ()-cMc.getOriginZ());
                        }
                    }
                }
            }
        }
    }
}
