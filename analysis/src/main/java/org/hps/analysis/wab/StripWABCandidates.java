package org.hps.analysis.wab;

import static java.lang.Math.abs;
import java.util.List;
import org.hps.recon.ecal.cluster.ClusterUtilities;
import org.hps.recon.tracking.TrackUtils;
import org.lcsim.detector.DetectorElementStore;
import org.lcsim.detector.IDetectorElement;
import org.lcsim.detector.identifier.IExpandedIdentifier;
import org.lcsim.detector.identifier.IIdentifier;
import org.lcsim.detector.identifier.IIdentifierDictionary;
import org.lcsim.detector.tracker.silicon.HpsSiSensor;
import org.lcsim.detector.tracker.silicon.SiSensor;
import org.lcsim.event.CalorimeterHit;
import org.lcsim.event.Cluster;
import org.lcsim.event.EventHeader;
import org.lcsim.event.RawTrackerHit;
import org.lcsim.event.ReconstructedParticle;
import org.lcsim.event.RelationalTable;
import org.lcsim.event.Track;
import org.lcsim.event.TrackerHit;
import org.lcsim.util.Driver;
import org.lcsim.util.aida.AIDA;

/**
 *
 * @author Norman A. Graf
 */
public class StripWABCandidates extends Driver {

    private boolean _writeRunAndEventNumbers = false;
    private boolean _stripBothFiducial = false;
    private boolean _onlyPhotonFiducial = false;
    private double _energyCut = 0.85;
    private int _nHitsOnTrack = 5;
    private int _nReconstructedParticles = 2;
    private AIDA aida = AIDA.defaultInstance();

    RelationalTable hitToStrips;
    RelationalTable hitToRotated;

    private int _numberOfEventsWritten = 0;

    protected void process(EventHeader event) {
        boolean skipEvent = true;
        // get the ReconstructedParticles in this event
        List<ReconstructedParticle> rps = event.get(ReconstructedParticle.class, "FinalStateParticles");
        // now add in the FEE candidates
        rps.addAll(event.get(ReconstructedParticle.class, "OtherElectrons"));

        // get the electron and photon
        // for now start with only 2 ReconstructedParticles in the event...
        if (rps.size() <= _nReconstructedParticles) {
            ReconstructedParticle electron = null;
            ReconstructedParticle photon = null;
            for (ReconstructedParticle rp : rps) {
                // require the electron to have an associated ECal cluster
                if (rp.getParticleIDUsed().getPDG() == 11 && rp.getClusters().size() == 1) {
                    electron = rp;
                }
                if (rp.getParticleIDUsed().getPDG() == 22) {
                    photon = rp;
                }
            }
            // do we have one (and only one) of each?
            if (electron != null && photon != null) {
                double eEnergy = electron.getEnergy();
                double eMomentum = electron.getMomentum().magnitude();
                Cluster eClus = electron.getClusters().get(0);
                double eClusEnergy = eClus.getEnergy();
                boolean electronIsFiducial = isFiducial(ClusterUtilities.findSeedHit(eClus));
                String eDir = electronIsFiducial ? "electron fiducial" : "electron non-fiducial";
                double eTime = ClusterUtilities.getSeedHitTime(eClus);
                Track t = electron.getTracks().get(0);
                int nHits = t.getTrackerHits().size();
                double pEnergy = photon.getEnergy();
                Cluster pClus = photon.getClusters().get(0);
                boolean photonIsFiducial = isFiducial(ClusterUtilities.findSeedHit(pClus));
                double pTime = ClusterUtilities.getSeedHitTime(pClus);
                double eSum = eEnergy + pEnergy;
                aida.histogram1D("Electron + Photon cluster Esum", 100, 0., 3.0).fill(eSum);
                aida.histogram1D("Electron momentum + photon Energy", 100, 0., 3.0).fill(eMomentum + pEnergy);
                aida.histogram1D("Electron energy", 100, 0., 3.0).fill(eEnergy);
                aida.histogram1D("Electron cluster energy", 100, 0., 3.0).fill(eClusEnergy);
                aida.histogram1D("Electron momentum", 100, 0., 3.0).fill(eMomentum);
                aida.histogram1D("Electron eOverP", 100, 0., 2.0).fill(eEnergy / eMomentum);
                aida.histogram2D("Electron eOverP vs P", 100, 0., 3.0, 100, 0., 2.).fill(eMomentum, eEnergy / eMomentum);
                aida.histogram2D("Electron momentum vs Electron energy", 100, 0., 3.0, 100, 0., 3.0).fill(eMomentum, eEnergy);
                aida.histogram1D("Photon Energy", 100, 0., 3.0).fill(pEnergy);
                aida.histogram2D("Electron energy vs Photon energy", 100, 0., 3.0, 100, 0., 3.0).fill(eEnergy, pEnergy);
                aida.histogram2D("Electron momentum vs Photon energy", 100, 0., 3.0, 100, 0., 3.0).fill(eMomentum, pEnergy);
                aida.histogram2D("Electron Cluster x vs y", 200, -200., 200., 100, -100., 100.).fill(eClus.getPosition()[0], eClus.getPosition()[1]);
                aida.histogram2D("Photon Cluster x vs y", 200, -200., 200., 100, -100., 100.).fill(pClus.getPosition()[0], pClus.getPosition()[1]);
                aida.histogram1D("Cluster delta time", 100, -5., 5.).fill(eTime - pTime);
                aida.histogram2D("Electron Cluster y vs Photon Cluster y", 100, -100., 100., 100, -100., 100.).fill(eClus.getPosition()[1], pClus.getPosition()[1]);
                if (eSum >= _energyCut && electron.getTracks().get(0).getTrackerHits().size() >= _nHitsOnTrack) // electron
                {
                    if (abs(eTime - pTime) < 2.) {
                        if (eClus.getPosition()[1] * pClus.getPosition()[1] < 0.) {
                            // have good candidates
                            // let's setup up a few things for more detailed analyses
                            setupSensors(event);
                            hitToStrips = TrackUtils.getHitToStripsTable(event);
                            hitToRotated = TrackUtils.getHitToRotatedTable(event);
                            analyzeHitlayers(electron);
                            aida.histogram1D("Final " + nHits + " hits Electron momentum + photon Energy", 100, 0., 3.0).fill(eMomentum + pEnergy);
                            aida.histogram1D("Final " + nHits + " hits Electron Energy + photon Energy", 100, 0., 3.0).fill(eEnergy + pEnergy);
                            // Passed all cuts, let's write this event
                            skipEvent = false;
                            aida.tree().mkdirs(eDir);
                            aida.tree().cd(eDir);
                            aida.histogram1D("Electron momentum " + eDir, 100, 0., 3.0).fill(eMomentum);
                            aida.histogram1D("Electron eOverP " + eDir, 100, 0., 2.0).fill(eEnergy / eMomentum);
                            aida.histogram2D("Electron eOverP vs P " + eDir, 100, 0., 3.0, 100, 0., 2.).fill(eMomentum, eEnergy / eMomentum);
                            aida.histogram2D("Electron momentum vs Electron energy " + eDir, 100, 0., 3.0, 100, 0., 3.0).fill(eMomentum, eEnergy);
                            aida.tree().cd("..");
                            //Are we also requiring both clusters to be fiducial?
                            if (_stripBothFiducial) {
                                if (!electronIsFiducial || !photonIsFiducial) {
                                    skipEvent = true;
                                }
                            }
                            // Are we only requiring the photon to be fiducial?
                            if (_onlyPhotonFiducial) {
                                if (!photonIsFiducial) {
                                    skipEvent = true;
                                }
                            }

                            if (electronIsFiducial && photonIsFiducial) {
                                aida.histogram1D("Fiducial " + nHits + " hits Electron momentum + Fiducial photon Energy", 100, 0., 3.0).fill(eMomentum + pEnergy);
                                aida.histogram1D("Fiducial " + nHits + " hits Electron energy + Fiducial photon Energy", 100, 0., 3.0).fill(eEnergy + pEnergy);
                            }
                            if (photonIsFiducial) {
                                aida.histogram1D("Final " + nHits + " hits Electron momentum + Fiducial photon Energy", 100, 0., 3.0).fill(eMomentum + pEnergy);
                                aida.histogram1D("Final " + nHits + " hits Electron Energy + Fiducial photon Energy", 100, 0., 3.0).fill(eEnergy + pEnergy);
                            }
                        }
                    }

                }
            }
        }
        if (skipEvent) {
            throw new Driver.NextEventException();
        } else {
            if (_writeRunAndEventNumbers) {
                System.out.println(event.getRunNumber() + " " + event.getEventNumber());
            }
            _numberOfEventsWritten++;
        }
    }

    /**
     * Electrons having energy below the cut will be rejected.
     *
     * @param cut
     */
    public void setEnergyCut(double cut) {
        _energyCut = cut;
    }

    /**
     * Tracks having fewer than the number of hits will be rejected.
     *
     * @param cut
     */
    public void setNumberOfHitsOnTrack(int cut) {
        _nHitsOnTrack = cut;
    }

    /**
     * Events having more than the number of ReconstructedParticles will be
     * rejected.
     *
     * @param cut
     */
    public void setNumberOfReconstructedParticles(int cut) {
        _nReconstructedParticles = cut;
    }

    /**
     * Write out run and event numbers of events passing the cuts if desired
     *
     * @param b
     */
    public void setWriteRunAndEventNumbers(boolean b) {
        _writeRunAndEventNumbers = b;
    }

    public void setStripBothFiducial(boolean b) {
        _stripBothFiducial = b;
    }

    public void setOnlyPhotonFiducial(boolean b) {
        _onlyPhotonFiducial = b;
    }

    public boolean isFiducial(CalorimeterHit hit) {
        int ix = hit.getIdentifierFieldValue("ix");
        int iy = hit.getIdentifierFieldValue("iy");
        // Get the x and y indices for the cluster.
        int absx = Math.abs(ix);
        int absy = Math.abs(iy);

        // Check if the cluster is on the top or the bottom of the
        // calorimeter, as defined by |y| == 5. This is an edge cluster
        // and is not in the fiducial region.
        if (absy == 5) {
            return false;
        }

        // Check if the cluster is on the extreme left or right side
        // of the calorimeter, as defined by |x| == 23. This is also
        // an edge cluster and is not in the fiducial region.
        if (absx == 23) {
            return false;
        }

        // Check if the cluster is along the beam gap, as defined by
        // |y| == 1. This is an internal edge cluster and is not in the
        // fiducial region.
        if (absy == 1) {
            return false;
        }

        // Lastly, check if the cluster falls along the beam hole, as
        // defined by clusters with -11 <= x <= -1 and |y| == 2. This
        // is not the fiducial region.
        if (absy == 2 && ix <= -1 && ix >= -11) {
            return false;
        }

        // If all checks fail, the cluster is in the fiducial region.
        return true;
    }

    private void analyzeHitlayers(ReconstructedParticle rp) {
        Track t = rp.getTracks().get(0);
        String topOrBottom = isTopTrack(t) ? " top " : " bottom ";
        double p = rp.getMomentum().magnitude();
        int nHits = t.getTrackerHits().size();
        aida.histogram1D(topOrBottom + " track number of hits", 10, 0., 10.).fill(nHits);
//        System.out.println("Track has " + nHits + " hits");

        for (TrackerHit hit : TrackUtils.getStripHits(t, hitToStrips, hitToRotated)) {
            List rthList = hit.getRawHits();
            int layerNumber = ((RawTrackerHit) rthList.get(0)).getLayerNumber();
            aida.histogram1D(topOrBottom + " " + nHits + " track hit layer number", 20, 0., 20.).fill(layerNumber);
//            System.out.println(" hit in layer " + layerNumber);
            aida.histogram2D(topOrBottom + " " + nHits + "-hit Track hit layer number vs track momentum", 14, 0.5, 14.5, 100, 0., 2.).fill(layerNumber, p);
        }
    }

    private boolean isTopTrack(Track t) {
        List<TrackerHit> hits = t.getTrackerHits();
        int n[] = {0, 0};
        int nHits = hits.size();
        for (TrackerHit h : hits) {
            HpsSiSensor sensor = ((HpsSiSensor) ((RawTrackerHit) h.getRawHits().get(0)).getDetectorElement());
            if (sensor.isTopLayer()) {
                n[0] += 1;
            } else {
                n[1] += 1;
            }
        }
        if (n[0] == nHits && n[1] == 0) {
            return true;
        }
        if (n[1] == nHits && n[0] == 0) {
            return false;
        }
        throw new RuntimeException("mixed top and bottom hits on same track");

    }

    private void setupSensors(EventHeader event) {
        List<RawTrackerHit> rawTrackerHits = event.get(RawTrackerHit.class, "SVTRawTrackerHits");
        EventHeader.LCMetaData meta = event.getMetaData(rawTrackerHits);
        // Get the ID dictionary and field information.
        IIdentifierDictionary dict = meta.getIDDecoder().getSubdetector().getDetectorElement().getIdentifierHelper().getIdentifierDictionary();
        int fieldIdx = dict.getFieldIndex("side");
        int sideIdx = dict.getFieldIndex("strip");
        for (RawTrackerHit hit : rawTrackerHits) {
            // The "side" and "strip" fields needs to be stripped from the ID for sensor lookup.
            IExpandedIdentifier expId = dict.unpack(hit.getIdentifier());
            expId.setValue(fieldIdx, 0);
            expId.setValue(sideIdx, 0);
            IIdentifier strippedId = dict.pack(expId);
            // Find the sensor DetectorElement.
            List<IDetectorElement> des = DetectorElementStore.getInstance().find(strippedId);
            if (des == null || des.size() == 0) {
                throw new RuntimeException("Failed to find any DetectorElements with stripped ID <0x" + Long.toHexString(strippedId.getValue()) + ">.");
            } else if (des.size() == 1) {
                hit.setDetectorElement((SiSensor) des.get(0));
            } else {
                // Use first sensor found, which should work unless there are sensors with duplicate IDs.
                for (IDetectorElement de : des) {
                    if (de instanceof SiSensor) {
                        hit.setDetectorElement((SiSensor) de);
                        break;
                    }
                }
            }
            // No sensor was found.
            if (hit.getDetectorElement() == null) {
                throw new RuntimeException("No sensor was found for hit with stripped ID <0x" + Long.toHexString(strippedId.getValue()) + ">.");
            }
        }
    }

    @Override
    protected void endOfData() {
        System.out.println("Wrote " + _numberOfEventsWritten + " events");
    }

}
