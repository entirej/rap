package org.entirej.applicationframework.rwt.renderers.chart;

import org.eclipse.rap.chartjs.AbstarctChartOptions;
import org.entirej.framework.core.EJActionProcessorException;
import org.entirej.framework.core.EJBlock;
import org.entirej.framework.core.EJForm;

public interface EJRWTChartProcessor
{
    public void preRefresh(EJForm form, EJBlock block,AbstarctChartOptions options) throws EJActionProcessorException;
}
