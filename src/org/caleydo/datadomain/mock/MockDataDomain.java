/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.ArrayUtils;
import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.CategoricalColumn;
import org.caleydo.core.data.collection.column.NumericalColumn;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoricalContainer;
import org.caleydo.core.data.collection.column.container.FloatContainer;
import org.caleydo.core.data.collection.column.container.IntContainer;
import org.caleydo.core.data.collection.table.CategoricalTable;
import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.collection.table.TableAccessor;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDCreator;
import org.caleydo.core.id.IDTypeInitializer;
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorBrewer;

import com.google.common.primitives.Ints;

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

	/**
	 * create a new numerical data domain of the given size
	 *
	 * @param numCols
	 * @param numRows
	 * @param r
	 * @return
	 */
	public static MockDataDomain createNumerical(int numCols, int numRows, Random r) {
		DataSetDescription dataSetDescription = createDataSetDecription(r);
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
		TableAccessor.postProcess(table, 0, 1);

		return dataDomain;
	}

	/**
	 * create a new numerical integer data domain of the given size
	 *
	 * @param numCols
	 * @param numRows
	 * @param r
	 * @param max
	 * @return
	 */
	public static MockDataDomain createNumericalInteger(int numCols, int numRows, Random r, int max) {
		DataSetDescription dataSetDescription = createDataSetDecription(r);
		dataSetDescription.setDataDescription(createNumericalIntegerDataDecription(max));
		DataDescription dataDescription = dataSetDescription.getDataDescription();
		MockDataDomain dataDomain = createDataDomain(dataSetDescription);

		NumericalTable table = new NumericalTable(dataDomain);

		table.setDataCenter(0.0);


		for (int i = 0; i < numCols; ++i) {
			IntContainer container = new IntContainer(numRows);
			NumericalColumn<IntContainer, Integer> column = new NumericalColumn<>(dataDescription);
			column.setRawData(container);
			for (int j = 0; j < numRows; ++j) {
				container.add(r.nextInt(max));
			}
			table.addColumn(column);
		}

		dataDomain.setTable(table);
		TableAccessor.postProcess(table, 0, max);

		return dataDomain;
	}

	/**
	 * create a new categorical homogeneous data domain with the given size and categories
	 *
	 * @param numCols
	 * @param numRows
	 * @param r
	 * @param categories
	 *            a list of possible categories
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static MockDataDomain createCategorical(int numCols, int numRows, Random r, String... categories) {
		DataSetDescription dataSetDescription = createDataSetDecription(r);
		dataSetDescription.setDataDescription(createCategoricalDataDecription(categories));

		DataDescription dataDescription = dataSetDescription.getDataDescription();
		MockDataDomain dataDomain = createDataDomain(dataSetDescription);

		CategoricalTable<String> table = new CategoricalTable<>(dataDomain);

		table.setCategoryDescritions((CategoricalClassDescription<String>) dataDescription.getCategoricalClassDescription());

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

	/**
	 * create and register a new record grouping using the given group sizes
	 *
	 * @param dataDomain
	 * @param groups
	 *            the group sizes if the sum if less that the number of records the last group will be automatically
	 *            computed
	 * @return a table perspective with the new perspective and the default one
	 */
	public static TablePerspective addRecGrouping(MockDataDomain dataDomain, int... groups) {
		return addGrouping(dataDomain, true, true, groups);
	}

	public TablePerspective addRecGrouping(boolean fillOut, int... groups) {
		return addGrouping(this, true, fillOut, groups);
	}

	public static TablePerspective addDimGrouping(MockDataDomain dataDomain, int... groups) {
		return addGrouping(dataDomain, false, true, groups);
	}

	public TablePerspective addDimGrouping(boolean fillOut, int... groups) {
		return addGrouping(this, false, fillOut, groups);
	}

	private static TablePerspective addGrouping(MockDataDomain dataDomain, boolean isRecord, boolean fillOut,
			int... groups) {
		Table table = dataDomain.getTable();
		int total = isRecord ? table.depth() : table.size();
		int sum = sum(groups);
		if (sum < total && fillOut) {
			groups = ArrayUtils.add(groups, total - sum); // fill out
			sum = total;
		} else if (sum > total)
			throw new IllegalStateException("have more groups that items");

		PerspectiveInitializationData data = new PerspectiveInitializationData();
		data.setLabel(groups.length + " grouping");
		VirtualArray va = (isRecord ? table.getDefaultRecordPerspective(false) : table
				.getDefaultDimensionPerspective(false)).getVirtualArray();

		List<Integer> samples = new ArrayList<>();
		int acc = 0;
		for (int group : groups) {
			samples.add(va.get(acc));
			acc += group;
		}
		data.setData(new ArrayList<>(va.getIDs().subList(0, sum)), Ints.asList(groups), samples);

		return registerAndGet(dataDomain, isRecord, data);
	}

	private static TablePerspective registerAndGet(MockDataDomain dataDomain, boolean isRecord,
			PerspectiveInitializationData data) {
		Table table = dataDomain.getTable();
		Perspective p = new Perspective(dataDomain, isRecord ? dataDomain.getRecordIDType()
				: dataDomain.getDimensionIDType());
		p.init(data);
		if (isRecord) {
			table.registerRecordPerspective(p);
			return dataDomain.getTablePerspective(p.getPerspectiveID(), table.getDefaultDimensionPerspective(false)
					.getPerspectiveID());
		} else {
			table.registerDimensionPerspective(p);
			return dataDomain.getTablePerspective(table.getDefaultRecordPerspective(false).getPerspectiveID(),
					p.getPerspectiveID());
		}
	}

	/**
	 * @param groups
	 * @return
	 */
	private static int sum(int[] groups) {
		int sum = 0;
		for (int g : groups)
			sum += g;
		return sum;
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

	private static DataSetDescription createDataSetDecription(Random r) {
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

	private static DataDescription createNumericalIntegerDataDecription(int max) {
		DataDescription d = new DataDescription(EDataClass.NATURAL_NUMBER, EDataType.INTEGER);
		NumericalProperties numericalProperties = new NumericalProperties();
		numericalProperties.setMax(Float.valueOf(max));
		d.setNumericalProperties(numericalProperties);
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
