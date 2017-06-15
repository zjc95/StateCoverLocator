/**
 * Copyright (C) SEI, PKU, PRC. - All Rights Reserved.
 * Unauthorized copying of this file via any medium is
 * strictly prohibited Proprietary and Confidential.
 * Written by Jiajun Jiang<jiajun.jiang@pku.edu.cn>.
 */

package locator.core;

/**
 * @author Jiajun
 * @date Jun 13, 2017
 */
public class Ochiai extends Algorithm {

	@Override
	public String getName() {
		return "Ochiai";
	}
	
	/**
	 * (failed(s) / sqrt(totalFailed * (failed(s) + passed(s)))
	 */
	@Override
	public double getScore(int fcover, int pcover, int totalFailed,
			int totalPassed) {
		return fcover * 1.0 / Math.sqrt(totalFailed * (fcover + pcover));
	}

}