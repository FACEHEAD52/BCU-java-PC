package util.entity.data;

import util.Data;

public interface MaskAtk {

	public default int getDire() {
		return 1;
	}

	public int getLongPoint();

	public int[] getProc(int ind);

	public int getShortPoint();

	public default int getTarget() {
		return Data.TCH_N;
	}

}