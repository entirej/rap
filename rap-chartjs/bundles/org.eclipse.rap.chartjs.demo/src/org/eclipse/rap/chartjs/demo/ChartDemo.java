/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.chartjs.demo;

import org.eclipse.rap.chartjs.Chart;
import org.eclipse.rap.chartjs.ChartOptions;
import org.eclipse.rap.chartjs.ChartPointData;
import org.eclipse.rap.chartjs.ChartRowData;
import org.eclipse.rap.chartjs.ChartStyle;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class ChartDemo extends AbstractEntryPoint {

	private static final String[] MONTHS = new String[] { "January", "February", "March", "April", "May", "June",
			"July" };
	private final ChartOptions options = new ChartOptions();;
	private ChartStyle chartStyle1;
	private ChartStyle chartStyle2;
	private ChartStyle chartStyle3;
	private ChartStyle chartStyle4;
	private ChartStyle chartStyle5;
	private int update = 1;
	private final Chart[] charts = new Chart[6];

	@Override
	protected void createContents(Composite parent) {
		parent.setLayout(new GridLayout(3, true));
		createChartStyles();
		for (int i = 0; i < charts.length; i++) {
			createChart(parent, i);
		}
		createControls(parent);
	}

	private ChartPointData createPointData() {
		return new ChartPointData().addPoint(nr(), chartStyle3).addPoint(nr(), chartStyle4).addPoint(nr(), chartStyle5)
				.addPoint(nr(), chartStyle1).addPoint(nr(), chartStyle2);
	}

	private ChartRowData createRowData() {
		return new ChartRowData(MONTHS).addRow(new float[] { nr(), nr(), nr(), nr(), nr(), nr(), nr() }, chartStyle1)
				.addRow(new float[] { nr(), nr(), nr(), nr(), nr(), nr(), nr() }, chartStyle2);
	}

	private void createChartStyles() {
		chartStyle1 = new ChartStyle(220, 220, 220, 0.8f);
		chartStyle1.setPointColor(new RGB(100, 220, 100));
		chartStyle2 = new ChartStyle(151, 187, 205, 0.7f);
		chartStyle3 = new ChartStyle(250, 187, 105, 0.9f);
		chartStyle4 = new ChartStyle(50, 187, 205, 0.9f);
		chartStyle4.setStrokeColor(new RGB(0, 0, 0));
		chartStyle5 = new ChartStyle(10, 17, 133, 0.9f);
	}

	private void createChart(Composite parent, int no) {
		Chart chart = new Chart(parent, SWT.NONE);
		chart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		charts[no] = chart;
		renderChart(no);
	}

	private void renderChart(int no) {
		switch (no) {
		case 0:
			charts[no].drawBarChart(createRowData(), options);
			break;
		case 1:
			charts[no].drawLineChart(createRowData(), options);
			break;
		case 2:
			charts[no].drawRadarChart(createRowData(), options);
			break;
		case 3:
			charts[no].drawPolarAreaChart(createPointData(), options);
			break;
		case 4:
			charts[no].drawPieChart(createPointData(), options);
			break;
		case 5:
			charts[no].drawDoughnutChart(createPointData(), options);
			break;
		}
	}

	private void createControls(final Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalSpan = 3;
		composite.setLayoutData(layoutData);
		RowLayout layout = new RowLayout();
		layout.center = true;
		composite.setLayout(layout);
		createOption(composite, "Animation", options.getAnimation(), new BooleanSetter() {
			@Override
			public void setOption(boolean value) {
				options.setAnimation(value);
			}
		});
		createOption(composite, "ShowToolTips", options.getShowToolTips(), new BooleanSetter() {
			@Override
			public void setOption(boolean value) {
				options.setShowToolTips(value);
			}
		});
		createOption(composite, "ScaleBeginAtZero", options.getScaleBeginAtZero(), new BooleanSetter() {
			@Override
			public void setOption(boolean value) {
				options.setScaleBeginAtZero(value);
			}
		});
		createOption(composite, "BezierCurve", options.getBezierCurve(), new BooleanSetter() {
			@Override
			public void setOption(boolean value) {
				options.setBezierCurve(value);
			}
		});
		createOption(composite, "ShowFill", options.getShowFill(), new BooleanSetter() {
			@Override
			public void setOption(boolean value) {
				options.setShowFill(value);
			}
		});
		createOption(composite, "ScaleShowLabels", options.getScaleShowLabels(), new BooleanSetter() {
			@Override
			public void setOption(boolean value) {
				options.setScaleShowLabels(value);
			}
		});
		createOption(composite, "PointDotRadius", options.getPointDotRadius(), new IntSetter() {
			@Override
			public void setOption(int value) {
				options.setPointDotRadius(value);
			}
		});
		createOption(composite, "StrokeWidth", options.getStrokeWidth(), new IntSetter() {
			@Override
			public void setOption(int value) {
				options.setStrokeWidth(value);
			}
		});
		createOption(composite, " | Chart No.", update, new IntSetter() {
			@Override
			public void setOption(int value) {
				update = value;
			}
		});
		Button drawButton = new Button(composite, SWT.PUSH);
		drawButton.setText("Draw!");
		drawButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				renderChart(update - 1);
			}
		});
		Button clearButton = new Button(composite, SWT.PUSH);
		clearButton.setText("Clear!");
		clearButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				charts[update - 1].clear();
			}
		});
	}

	private void createOption(Composite parent, String name, boolean initial, final BooleanSetter booleanSetter) {
		final Button button = new Button(parent, SWT.CHECK);
		button.setText(name + "   ");
		button.setSelection(initial);
		button.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				booleanSetter.setOption(button.getSelection());
			}
		});
	}

	private void createOption(Composite parent, String name, int initial, final IntSetter intSetter) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText("   " + name + ":");
		final Text text = new Text(parent, SWT.BORDER);
		text.setText(String.valueOf(initial));
		text.addListener(SWT.Modify, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					intSetter.setOption(Integer.parseInt(text.getText()));
				} catch (NumberFormatException ex) {
				}
			}
		});
	}

	private abstract class BooleanSetter {
		abstract void setOption(boolean value);
	}

	private abstract class IntSetter {
		abstract void setOption(int value);
	}

	private int nr() {
		int factor = options.getScaleBeginAtZero() ? 100 : 1000;
		int offset = Math.round(factor * 0.1f);
		return offset + (int) (Math.random() * (factor - offset));
	}

}
