package org.hps.analysis.examples;

//import hep.aida.IAnalysisFactory;
//import hep.aida.IDataPointSet;
//import hep.aida.IDataPointSetFactory;
//import hep.aida.IProfile1D;
//import hep.physics.vec.BasicHep3Matrix;
//import hep.physics.vec.Hep3Vector;
//import hep.physics.vec.VecOp;
//import static java.lang.Math.abs;
//import static java.lang.Math.atan2;
import hep.physics.vec.BasicHep3Matrix;
import hep.physics.vec.Hep3Vector;
import hep.physics.vec.VecOp;
import static java.lang.Math.atan2;
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

/**
 *
 * @author goodwill
 */
public class TrackPlot extends Driver {

    private final BasicHep3Matrix beamAxisRotation = new BasicHep3Matrix();
    private AIDA aida = AIDA.defaultInstance();

    /**
     *
     * @param event
     */
    @Override
    public void process(EventHeader event) {
        //setupSensors(event);
        analyzeRP(event);
        analyzeV0(event);
    }

    private void analyzeRP(EventHeader event) {
        List<ReconstructedParticle> rpList = event.get(ReconstructedParticle.class, "FinalStateParticles");
        List<Cluster> clusters;
        //List<Double> times = new ArrayList<Double>();
        for (ReconstructedParticle rp : rpList) {
            int pdgId;
            //int nHits = t.getTrackerHits().size();
            Hep3Vector pmom = rp.getMomentum();
            double thetaY = atan2(pmom.y(), pmom.z());//asin(pmom.y() / pmom.magnitude());
            clusters = rp.getClusters();
            
            pdgId = rp.getParticleIDUsed().getPDG();
            if (pdgId == 11) {
                
                //String trackType = TrackType.isGBL(rp.getType()) ? "GBL " : "MatchedTrack ";
//            if (!TrackType.isGBL(rp.getType())) {
//                continue;
//            }
                String id = "electron";
                Track t = rp.getTracks().get(0);
                TrackState st2 = TrackUtils.getTrackStateAtECal(t);
                if (!clusters.isEmpty()) {
                    Cluster c = clusters.get(0);
                    
                    double[] cPos = c.getPosition();
                    aida.histogram1D(id + "/TrackMomentum", 100, 0., 6.).fill(pmom.magnitude());
                    aida.histogram1D(id + "/Theta", 100, -0.1, 0.1).fill(thetaY);
                    aida.histogram1D(id + "/Chi2", 100, 0., 100.).fill(t.getChi2());
                    double[] ePos = st2.getReferencePoint();
                    aida.histogram2D(id + "/xVsyAtECAl", 320, -270.0, 370.0, 90, -90.0, 90.0).fill(ePos[1], ePos[2]);
                    aida.histogram2D(id + "/delxVsdely", 200, -100., 100., 40, -20.0, 20.0).fill(ePos[1] - cPos[0], ePos[2] - cPos[1]);
                    aida.histogram1D(id + "/CaltoTrack", 80, 0., 40.).fill(Math.sqrt(Math.pow(ePos[1] - cPos[0], 2) + Math.pow(ePos[2] - cPos[1], 2)));
                    aida.histogram2D(id + "/EnergyVsmom", 100, 0., 3., 100, 0., 3.).fill(c.getEnergy(), pmom.magnitude());
                    aida.histogram1D(id + "/Energy\\mom", 100, 0., 1.).fill(c.getEnergy() / pmom.magnitude());
                    aida.histogram1D(id + "/Energy", 100, 0., 3.).fill(c.getEnergy());
                    aida.histogram1D(id + "/Momentum", 100, 0., 3.).fill(pmom.magnitude());
                    aida.histogram1D( id + "hits",100,0.,3.).fill(c.getCalorimeterHits().size());
                }
            }
            if (pdgId == -11) {
                String id = "positron";
                Track t = rp.getTracks().get(0);
                TrackState st2 = TrackUtils.getTrackStateAtECal(t);
                if (!clusters.isEmpty()) {
                    Cluster c = clusters.get(0);
                    double[] cPos = c.getPosition();
                    aida.histogram1D(id + "/TrackMomentum", 100, 0., 6.).fill(pmom.magnitude());
                    aida.histogram1D(id + "/Theta", 100, -0.1, 0.1).fill(thetaY);
                    aida.histogram1D(id + "/Chi2", 100, 0., 100.).fill(t.getChi2());
                    double[] ePos = st2.getReferencePoint();
                    aida.histogram2D(id + "/xvsyAtECAl", 320, -270.0, 370.0, 90, -90.0, 90.0).fill(ePos[1], ePos[2]);
                    aida.histogram2D(id + "/delXvsDelY", 200, -100., 100., 40, -20.0, 20.0).fill(ePos[1] - cPos[0], ePos[2] - cPos[1]);
                    aida.histogram1D(id + "/CaltoTrack", 80, 0., 40.).fill(Math.sqrt(Math.pow(ePos[1] - cPos[0], 2) + Math.pow(ePos[2] - cPos[1], 2)));
                    aida.histogram2D(id + "/EnergyVsmom", 100, 0., 3., 100, 0., 3.).fill(c.getEnergy(), pmom.magnitude());
                    aida.histogram1D(id + "/Energy\\mom", 100, 0., 1.).fill(c.getEnergy() / pmom.magnitude());
                    aida.histogram1D(id + "/Energy", 100, 0., 3.).fill(c.getEnergy());
                    aida.histogram1D(id + "/Momentum", 100, 0., 3.).fill(pmom.magnitude());
                    
                }
            }
        }
    }

    private void analyzeV0(EventHeader event) {
        List<ReconstructedParticle> v0List = event.get(ReconstructedParticle.class, "UnconstrainedV0Candidates");
        for (ReconstructedParticle v0 : v0List) {
            Vertex uncVert = v0.getStartVertex();
            Hep3Vector pVtxRot = VecOp.mult(beamAxisRotation, v0.getMomentum());
            Hep3Vector vtxPosRot = VecOp.mult(beamAxisRotation, uncVert.getPosition());
            aida.histogram1D("A4: v0 x", 50, -5., 5.).fill(vtxPosRot.x());
            aida.histogram1D("A4: v0 y", 50, -5., 5.).fill(vtxPosRot.y());
            aida.histogram1D("A4: v0 z", 50, -25., 0.).fill(vtxPosRot.z());
        }
    }
}
