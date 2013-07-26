/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.mock;

import java.util.Random;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.CategoricalColumn;
import org.caleydo.core.data.collection.column.NumericalColumn;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoricalContainer;
import org.caleydo.core.data.collection.column.container.FloatContainer;
import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.TableAccessor;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.id.IDCreator;
import org.caleydo.core.id.IDTypeInitializer;
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorBrewer;

/**
 * a mock data domain use factory methods to create some data
 * 
 * @author Samuel Gratzl
 * 
 */
@XmlType
@XmlRootElement
public class MockDataDomain extends ATableBasedDataDomain {
	public final static String DATA_DOMAIN_TYPE = "org.caleydo.datadomain.mock";

	/**
	 * Constructor.
	 */
	public MockDataDomain() {

		super(DATA_DOMAIN_TYPE, DATA_DOMAIN_TYPE + DataDomainManager.DATA_DOMAIN_INSTANCE_DELIMITER
				+ IDCreator.createPersistentID(MockDataDomain.class));
	}

	public static MockDataDomain createNumerical(int numCols, int numRows, Random r) {
		DataSetDescription dataSetDescription = createDataSetDecription();
		dataSetDescription.setDataDescription(createNumericalDataDecription());

		DataDescription dataDescription = dataSetDescription.getDataDescription();
		MockDataDomain dataDomain = createDataDomain(dataSetDescription);

		NumericalTable table = new NumericalTable(dataDomain);

		table.setDataCenter(0.0);

		for (int i = 0; i < numCols; ++i) {
			FloatContainer container = new FloatContainer(numRows);
			NumericalColumn<FloatContainer, Float> column = new NumericalColumn<>(dataDescription);
			column.setRawData(container);
			for (int j = 0; j < numRows; ++j) {
				container.add(r.nextFloat());
			}
			table.addColumn(column);
		}

		dataDomain.setTable(table);
		TableAccessor.postProcess(table);

		return dataDomain;
	}

	public static MockDataDomain createCategorical(int numCols, int numRows, Random r, String... categories) {
		DataSetDescription dataSetDescription = createDataSetDecription();
		dataSetDescription.setDataDescription(createCategoricalDataDecription(categories));

		DataDescription dataDescription = dataSetDescription.getDataDescription();
		MockDataDomain dataDomain = createDataDomain(dataSetDescription);

		NumericalTable table = new NumericalTable(dataDomain);

		table.setDataCenter(0.0);

		for (int i = 0; i < numCols; ++i) {
			CategoricalContainer<String> container = new CategoricalContainer<>(numRows, EDataType.STRING, null);
			CategoricalColumn<String> column = new CategoricalColumn<>(dataDescription);
			column.setRawData(container);
			for (int j = 0; j < numRows; ++j) {
				container.add(categories[r.nextInt(categories.length)]);
			}
			table.addColumn(column);
		}

		dataDomain.setTable(table);
		TableAccessor.postProcess(table);

		return dataDomain;
	}

	private static MockDataDomain createDataDomain(DataSetDescription dataSetDescription) {
		IDTypeInitializer.initIDs(dataSetDescription);

		MockDataDomain dataDomain = new MockDataDomain();
		dataDomain.setDataSetDescription(dataSetDescription);
		dataDomain.init();
		Thread thread = new Thread(dataDomain, DATA_DOMAIN_TYPE);
		thread.start();
		DataDomainManager.get().register(dataDomain);
		return dataDomain;
	}

	private static DataSetDescription createDataSetDecription() {
		DataSetDescription d = new DataSetDescription();
		d.setColor(Color.BLUE.brighter());
		d.setDataSetName("Mock");
		d.setTransposeMatrix(false);
		d.setColumnIDSpecification(new IDSpecification("mock_col", "mock_col"));
		d.setRowIDSpecification(new IDSpecification("mock_row", "mock_row"));

		return d;
	}

	private static DataDescription createNumericalDataDecription() {
		DataDescription d = new DataDescription(EDataClass.REAL_NUMBER, EDataType.FLOAT);
		return d;
	}

	private static DataDescription createCategoricalDataDecription(String[] categories) {
		DataDescription d = new DataDescription(EDataClass.CATEGORICAL, EDataType.STRING);
		CategoricalClassDescription<String> c = new CategoricalClassDescription<>(EDataType.STRING);
		for (int i = 0; i < categories.length; ++i) {
			c.addCategoryProperty(categories[i], categories[i], Color.DARK_GRAY);
		}
		c.applyColorScheme(ColorBrewer.Set3, null, false);
		d.setCategoricalClassDescription(c);
		return d;
	}
}
