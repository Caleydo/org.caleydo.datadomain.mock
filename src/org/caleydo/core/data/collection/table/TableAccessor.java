/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.data.collection.table;

/**
 * @author Samuel Gratzl
 *
 */
public class TableAccessor {
	public static void postProcess(NumericalTable table) {
		table.setMin(0);
		table.setMax(1);
		table.normalize();
		table.createDefaultDimensionPerspectives();
		table.createDefaultRecordPerspectives();
	}
}
