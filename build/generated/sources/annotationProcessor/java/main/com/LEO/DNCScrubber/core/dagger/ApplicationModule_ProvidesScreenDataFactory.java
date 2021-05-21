package com.LEO.DNCScrubber.core.dagger;

import com.LEO.DNCScrubber.Scrubber.model.ScreenData;
import com.google.gson.Gson;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class ApplicationModule_ProvidesScreenDataFactory implements Factory<ScreenData> {
  private final ApplicationModule module;

  private final Provider<Gson> gsonProvider;

  public ApplicationModule_ProvidesScreenDataFactory(
      ApplicationModule module, Provider<Gson> gsonProvider) {
    assert module != null;
    this.module = module;
    assert gsonProvider != null;
    this.gsonProvider = gsonProvider;
  }

  @Override
  public ScreenData get() {
    return Preconditions.checkNotNull(
        module.providesScreenData(gsonProvider.get()),
        "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<ScreenData> create(ApplicationModule module, Provider<Gson> gsonProvider) {
    return new ApplicationModule_ProvidesScreenDataFactory(module, gsonProvider);
  }
}
