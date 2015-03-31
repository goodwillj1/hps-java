package org.hps.evio;

/**
 *	A set of static utility methods used to decode SVT data.
 * 
 * 	@author Omar Moreno <omoreno1@ucsc.edu>
 *	@date November 20, 2014
 */
public class SvtEvioUtils {

	//-----------------//
	//--- Constants ---//
	//-----------------//
	
	private static final int TOTAL_SAMPLES = 6;
    public static final int  SAMPLE_MASK = 0xFFFF;
   
    // TODO: Move these to constants class
    public static final int APV25_PER_HYBRID = 5;
    public static final int CHANNELS_PER_APV25 = 128;
	
	//--- Test Run ---//
	private static final int TEST_RUN_SAMPLE_HEADER_INDEX = 0; 
    public static final int  FPGA_MASK = 0xFFFF;
    public static final int  HYBRID_MASK = 0x3;
    public static final int  TEST_RUN_CHANNEL_MASK = 0x7F;

    //--- Engineering Run ---//
    private static final int ENG_RUN_SAMPLE_HEADER_INDEX = 3; 
    private static final int FEB_MASK = 0xFF;
    private static final int FEB_HYBRID_MASK = 0x3;
    private static final int ENG_RUN_APV_MASK = 0x7;
    private static final int ENG_RUN_CHANNEL_MASK = 0x7F;
    private static final int ENG_RUN_APV_HEADER_MASK = 0x1;
    private static final int ENG_RUN_APV_TAIL_MASK = 0x1;
    
    /**
     * 	Extract and return the FPGA ID associated with the samples.
     * 	Note: This method should only be used when looking at test run data.
     * 
	 *	@param data - sample block of data
     * 	@return An FPGA ID in the range 0-7
     */
	public static int getFpgaID(int[] data) { 
		return data[TEST_RUN_SAMPLE_HEADER_INDEX] & FPGA_MASK; 
	}

    /**
     * 	Extract and return the hybrid ID associated with the samples 
     * 	Note: This method should only be used when looking at test run data.
     * 
	 * 	@param data : sample block of data
     * 	@return A hybrid number in the range 0-2
     */
    public static int getHybridID(int[] data) {
        return (data[TEST_RUN_SAMPLE_HEADER_INDEX] >>> 28) & HYBRID_MASK;
    }
	
    /**
     *	Extract and return the channel number associated with the samples
     * 	Note: This method should only be used when looking at test run data.
     * 
	 * 	@param data : sample block of data
     * 	@return A channel number in the range 0-127
     */
    public static int getTestRunChannelNumber(int[] data) {
        return (data[TEST_RUN_SAMPLE_HEADER_INDEX] >>> 16) & TEST_RUN_CHANNEL_MASK;
    }
    
    /**
     *	Extract and return the front end board (FEB) ID associated with the
     *	samples
     *	
	 * 	@param data : sample block of data
	 * 	@return A FEB ID in the range 0-10
     */
    public static int getFebID(int[] data) { 
    	return (data[ENG_RUN_SAMPLE_HEADER_INDEX] >>> 8) & FEB_MASK; 
    }
    
    /**
     * 	Extract and return the front end board (FEB) hybrid ID associated with 
     * 	the samples
	 *
	 * 	@param data : sample block of data
	 * 	@return A FEB hybrid ID in the range 0-3
     */
    public static int getFebHybridID(int[] data) { 
    	return (data[ENG_RUN_SAMPLE_HEADER_INDEX] >>> 26) & FEB_HYBRID_MASK; 
    }

    /**
     *  Extract and return the APV ID associated with the samples.
     *  
     *  @param data : sample block of data
     *  @return An APV ID in the range of 0-4
     */
    public static int getApv(int[] data) { 
        return (data[ENG_RUN_SAMPLE_HEADER_INDEX] >>> 23) & ENG_RUN_APV_MASK; 
    }
    
    /**
     *	Extract and return the channel number associated with the samples
     * 
	 * 	@param data : sample block of data
     * 	@return A channel number in the range 0-127
     */
    public static int getChannelNumber(int[] data) {
        return (data[ENG_RUN_SAMPLE_HEADER_INDEX] >>> 16) & ENG_RUN_CHANNEL_MASK;
    }
   
    /**
     *  Extract the physical channel number associated with the samples
     *  
     *  @param data : sample block of data
     *  @return A channel number in the range 0-639
     * 	@throws RuntimeException if the physical channel number is out of range
     */
    public static int getPhysicalChannelNumber(int[] data) {

        // Extract the channel number from the data
        int channel = SvtEvioUtils.getChannelNumber(data);
        
        // Extract the APV ID from the data
        int apv = SvtEvioUtils.getApv(data);
    
        // Get the physical channel number
        int physicalChannel = (APV25_PER_HYBRID - apv - 1) * CHANNELS_PER_APV25 + channel;
       
        // Check that the physical channel number is valid.  If not, throw an exception
        if (physicalChannel < 0 || physicalChannel >= APV25_PER_HYBRID * CHANNELS_PER_APV25) {
            throw new RuntimeException("Physical channel " + physicalChannel + " is outside of valid range!");
        }
        return physicalChannel;
    }
    
    /**
     *  Check if the samples are APV headers
     *  
     * 
     *  @param data : sample block of data
     *  @return true if the samples belong to APV headers, false otherwise
     */
    public static boolean isApvHeader(int[] data) {
        if (((data[ENG_RUN_SAMPLE_HEADER_INDEX] >>> 30) & ENG_RUN_APV_HEADER_MASK) == 1) return true;
        return false;
    }
    
    /**
     *  Check if the samples are APV tails
     *  
     * 
     *  @param data : sample block of data
     *  @return true if the samples belong to APV tails, false otherwise
     */
    public static boolean isApvTail(int[] data) {
        if (((data[ENG_RUN_SAMPLE_HEADER_INDEX] >>> 29) & ENG_RUN_APV_TAIL_MASK) == 1) return true;
        return false;
    }
    
    /**
     * 	Extract and return the nth SVT sample.
     * 
     * 	@param sampleN : The sample number of interest. Valid values are 0 to 5
	 * 	@param data : sample block of data
     * 	@throws RuntimeException if the sample number is out of range
     * 	@return ADC value of the nth sample
     * 
     */
    public static int getSample(int sampleN, int[] data) {

        switch (sampleN) {
            case 0:
                return data[0] & SAMPLE_MASK;
            case 1:
                return (data[0] >>> 16) & SAMPLE_MASK;
            case 2:
                return data[1] & SAMPLE_MASK;
            case 3:
                return (data[1] >>> 16) & SAMPLE_MASK;
            case 4:
                return data[2] & SAMPLE_MASK;
            case 5:
                return (data[2] >>> 16) & SAMPLE_MASK;
            default:
                throw new RuntimeException("Invalid sample number! Valid range of values for n are from 0 - 5");
        }
    }

    /**
     *	Extract and return all SVT samples as an array 
     * 
	 * 	@param data : sample block of data
     * 	@return An array containing all SVT Shaper signal samples
     */
    public static short[] getSamples(int[] data) {
        short[] samples = new short[TOTAL_SAMPLES];
        // Get all SVT Samples
        for (int sampleN = 0; sampleN < TOTAL_SAMPLES; sampleN++) {
            samples[sampleN] = (short) getSample(sampleN, data);
        }
        return samples;
    }

    /**
     *	Private constructor to prevent the class from being instantiated.
     */
    private SvtEvioUtils(){}; 
}
