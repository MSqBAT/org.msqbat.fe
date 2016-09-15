# MSqBAT Feature Extraction
## Feature Extraction for High Resolution LS-MS Data

### Build from source

0. `git clone git@github.com:MSqBAT/org.msqbat.datamodel.git`
0. `git clone git@github.com:MSqBAT/org.msqbat.fe.git`
0. `cd org.msqbat.fe/build/org.msqbat.fe.aggregator`
0. `mvn package`

### API Entry  point

    final ExtractorTightener exti = new ExtractorTightener();
	exti.setExtractor(new FeatureExtractorMassGrid(new SettingsFeatureExtraction()));
	final TightenerMass tm = new TightenerMass();
	if (preferencesManager != null) {
      tm.setMassShift(preferencesManager.getToleranceMz().get());
      tm.setPpm(preferencesManager.isToleranceMzPpm().get());
  	}
    final TightenerGaps tg = new TightenerGaps();
	// if (preferencesManager != null) {
	// tg.setMaxGapSize(preferencesManager.getToleranceScanNumber().get());
  	// }
	exti.addTightener(tg);
	exti.addTightener(tm);
	final SampleIons result2 = exti.extractFeatures((SampleIons) s);