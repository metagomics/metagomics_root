package org.uwpr.metagomics.run_upload.dto;

public class ProteinSequenceDTO {

	private int proteinSequenceId;
	private String sequence;
	
	
	public int getProteinSequenceId() {
		return proteinSequenceId;
	}
	public void setProteinSequenceId(int proteinSequenceId) {
		this.proteinSequenceId = proteinSequenceId;
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
	
	
    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof ProteinSequenceDTO)) {
            return false;
        }

        ProteinSequenceDTO protein = (ProteinSequenceDTO) o;

        return proteinSequenceId == protein.getProteinSequenceId();
    }

    @Override
    public int hashCode() {
        return sequence.hashCode();
    }
	
}
