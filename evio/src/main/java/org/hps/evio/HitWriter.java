package org.hps.evio;

import org.jlab.coda.jevio.EventBuilder;
import org.lcsim.event.EventHeader;

/**
 * Interface to be implemented by classes that convert LCSim data to recon data
 * formats.
 *
 * @author Sho Uemura <meeg@slac.stanford.edu>
 * @version $Id: HitWriter.java,v 1.1 2012/07/30 23:31:45 meeg Exp $
 */
public interface HitWriter {

    //checks whether the event contains data readable by this writer
    public boolean hasData(EventHeader event);

    //writes data from the event to the EVIO builder
    public void writeData(EventHeader event, EventBuilder builder);

    //writes data from the event to the EVIO builder
    public void writeData(EventHeader event, EventHeader toEvent);

    /**
     * Set the amount of printouts generated by the writer.
     * 0 = silent, 1 = normal, 2+ = debug
     * @param verbosity
     */
    public void setVerbosity(int verbosity);
}
