package com.LEO.DNCScrubber.core.dagger;

import com.LEO.DNCScrubber.Scrubber.controller.CsvFileReader;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class ApplicationModule_ProvidesCsvFileReaderFactory
    implements Factory<CsvFileReader> {
  private final ApplicationModule module;

  public ApplicationModule_ProvidesCsvFileReaderFactory(ApplicationModule module) {
    assert module != null;
    this.module = module;
  }

  @Override
  public CsvFileReader get() {
    return Preconditions.checkNotNull(
        module.providesCsvFileReader(), "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<CsvFileReader> create(ApplicationModule module) {
    return new ApplicationModule_ProvidesCsvFileReaderFactory(module);
  }
}
