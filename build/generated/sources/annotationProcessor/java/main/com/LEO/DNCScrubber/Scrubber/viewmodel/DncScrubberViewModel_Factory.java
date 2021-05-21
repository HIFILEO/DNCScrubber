package com.LEO.DNCScrubber.Scrubber.viewmodel;

import com.LEO.DNCScrubber.Scrubber.controller.CsvFileReader;
import com.LEO.DNCScrubber.Scrubber.model.ScreenData;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DncScrubberViewModel_Factory implements Factory<DncScrubberViewModel> {
  private final Provider<ScreenData> screenDataProvider;

  private final Provider<CsvFileReader> csvFileReaderProvider;

  public DncScrubberViewModel_Factory(
      Provider<ScreenData> screenDataProvider, Provider<CsvFileReader> csvFileReaderProvider) {
    assert screenDataProvider != null;
    this.screenDataProvider = screenDataProvider;
    assert csvFileReaderProvider != null;
    this.csvFileReaderProvider = csvFileReaderProvider;
  }

  @Override
  public DncScrubberViewModel get() {
    return new DncScrubberViewModel(screenDataProvider.get(), csvFileReaderProvider.get());
  }

  public static Factory<DncScrubberViewModel> create(
      Provider<ScreenData> screenDataProvider, Provider<CsvFileReader> csvFileReaderProvider) {
    return new DncScrubberViewModel_Factory(screenDataProvider, csvFileReaderProvider);
  }
}
