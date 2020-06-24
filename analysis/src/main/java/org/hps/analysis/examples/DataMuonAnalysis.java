package org.hps.analysis.examples;
import org.lcsim.util.Driver;
//import org.lcsim.util.aida.AIDA;
import java.util.List;

//import org.hps.analysis.util.CalcAccFromMadGraphWithDetector;
//import org.lcsim.util.*;
import org.lcsim.event.*;

public class DataMuonAnalysis extends Driver {
    //private final AIDA aida = AIDA.defaultInstance();

    public void process(final EventHeader event) {
        // setupSensors(event);
        analyzeData(event);
        // analyzeV0(event);
    }


    private void analyzeData(final EventHeader event) {
        final List<CalorimeterHit> calHits = event.get(CalorimeterHit.class, "EcalCalHits");
        final List<RawTrackerHit> rawHits = event.get(RawTrackerHit.class, "SVTRawTrackerHits");
        List<ReconstructedParticle> rpList = event.get(ReconstructedParticle.class, "FinalStateParticles");
        for (final CalorimeterHit cHits : calHits) {
            //System.out.println(
            //System.out.println("x = " + cHits.getPositionVec().x() + "y = " + cHits.getPositionVec().y() + "z = " + cHits.getPositionVec().z());
            //aida.histogram2D(id + "/xVsyScore", 800, -400.0, 400.0, 300, -150.0, 150.0).fill(trckHits.getPositionVec().x(), trckHits.getPositionVec().y());
        }
        for (final RawTrackerHit rwHits : rawHits) {
            //System.out.println(rwHits.
        }
    }
}