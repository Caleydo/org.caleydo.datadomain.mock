/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.mock;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorBrewer;

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

	public String getIDType(EDimension dim) {
		return "mock_" + dim.name().toLowerCase();
	}

	public String getIDCategory(EDimension dim) {
		return "mock_" + dim.name().toLowerCase();
	}

	/**
	 * @return
	 */
	public Color nextColor() {
		ColorBrewer b = ColorBrewer.Set3;
		int max = b.getSizes().last();
		return b.get(max, nextInt(max));
	}

	/**
	 * @return
	 */
	public String nextString() {
		return String.valueOf(nextInt(100));
	}
}
