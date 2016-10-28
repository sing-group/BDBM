package es.uvigo.ei.sing.bdbm.fasta;

public class SequenceLengthConfiguration {
	public static SequenceLengthConfiguration buildNoChanges() {
		return new SequenceLengthConfiguration(-1);
	}
	
	public static SequenceLengthConfiguration buildRemoveLineBreaks() {
		return new SequenceLengthConfiguration(0);
	}
	
	public static SequenceLengthConfiguration buildChangeFragmentLength(int length) {
		if (length <= 0)
			throw new IllegalArgumentException("length must be a positive number");
		
		return new SequenceLengthConfiguration(length);
	}
	
	
	private final int fragmentLength;
	
	private SequenceLengthConfiguration(int fragmentLength) {
		this.fragmentLength = fragmentLength;
	}
	
	public boolean isNoChange() {
		return this.fragmentLength < 0;
	}
	
	public boolean isRemoveLineBreaks() {
		return this.fragmentLength == 0;
	}
	
	public boolean isChangeFragmentLength() {
		return this.fragmentLength > 0;
	}
	
	public int getFragmentLength() {
		return this.fragmentLength;
	}
}
