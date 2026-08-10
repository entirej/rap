rap
===

Contains the EntireJ RAP and RCP client frameworks with extensions for RAP mobile

The `rap-chartjs` module contains the source and Maven build for the Chart.js 4
RAP custom widget used by `entirej-rwt-rap`.

Build the widget before the dependent RAP framework module:

```shell
./mvnw -f rap-chartjs/pom.xml clean install
./mvnw -f entirej-rwt-rap/pom.xml clean install
```
