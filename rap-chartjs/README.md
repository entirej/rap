# RAP Chart.js

Chart.js 4 custom widget for Eclipse RAP.

This source was imported from the upstream `chartjs4` branch at commit
`cd6bec559a5e8dd04b029e6eaec1534d034d7af4`. The production widget is built
with Maven from `bundles/org.eclipse.rap.chartjs`; the legacy demo still targets
the earlier widget API and is retained only as upstream reference.

Build and install the widget from this directory:

```shell
../mvnw clean install
```

The Maven artifact is
`org.eclipse.rap.incubator:org.eclipse.rap.chartjs:4.0.08` and targets Java 21
and Eclipse RAP 4.7.0.

The Java widget is published under the Eclipse Public License 1.0 (see
`LICENSE`). The bundled Chart.js library is available under the MIT license
(see `bundles/org.eclipse.rap.chartjs/docs/chartjs-LICENSE.md`).
