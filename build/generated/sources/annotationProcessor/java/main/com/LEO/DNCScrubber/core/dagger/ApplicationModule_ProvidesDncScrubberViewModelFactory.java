package com.LEO.DNCScrubber.core.dagger;

import com.LEO.DNCScrubber.Scrubber.controller.CsvFileReader;
import com.LEO.DNCScrubber.Scrubber.model.ScreenData;
import com.LEO.DNCScrubber.Scrubber.viewmodel.DncScrubberViewModel;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class ApplicationModule_ProvidesDncScrubberViewModelFactory
    implements Factory<DncScrubberViewModel> {
  private final ApplicationModule module;

  private final Provider<ScreenData> screenDataProvider;

  private final Provider<CsvFileReader> csvFileReaderProvider;

  public ApplicationModule_ProvidesDncScrubberViewModelFactory(
      ApplicationModule module,
      Provider<ScreenData> screenDataProvider,
      Provider<CsvFileReader> csvFileReaderProvider) {
    assert module != null;
    this.module = module;
    assert screenDataProvider != null;
    this.screenDataProvider = screenDataProvider;
    assert csvFileReaderProvider != null;
    this.csvFileReaderProvider = csvFileReaderProvider;
  }

  @Override
  public DncScrubberViewModel get() {
    return Preconditions.checkNotNull(
        module.providesDncScrubberViewModel(screenDataProvider.get(), csvFileReaderProvider.get()),
        "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<DncScrubberViewModel> create(
      ApplicationModule module,
      Provider<ScreenData> screenDataProvider,
      Provider<CsvFileReader> csvFileReaderProvider) {
    return new ApplicationModule_ProvidesDncScrubberViewModelFactory(
        module, screenDataProvider, csvFileReaderProvider);
  }
}
