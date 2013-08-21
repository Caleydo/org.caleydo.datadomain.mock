/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.mock;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AValueFactory {
	public abstract float nextFloat();

	public abstract int nextInt(int n);

	public double getMin() {
		return 0;
	}

	public double getMax() {
		return 1;
	}

	public double getCenter() {
		return 0;
	}
}
