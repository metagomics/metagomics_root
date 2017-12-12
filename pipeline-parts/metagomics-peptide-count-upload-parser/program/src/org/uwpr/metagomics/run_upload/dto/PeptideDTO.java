package org.uwpr.metagomics.run_upload.dto;

public class PeptideDTO {

	private int id;
	private String sequence;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
        if (!(o instanceof PeptideDTO)) {
            return false;
        }

        PeptideDTO otherPeptide = (PeptideDTO) o;

        return id == otherPeptide.getId();
    }

    @Override
    public int hashCode() {
        return sequence.hashCode();
    }
	
}
