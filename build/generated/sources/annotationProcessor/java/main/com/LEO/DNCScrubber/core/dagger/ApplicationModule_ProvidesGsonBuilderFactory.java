package com.LEO.DNCScrubber.core.dagger;

import com.google.gson.GsonBuilder;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class ApplicationModule_ProvidesGsonBuilderFactory implements Factory<GsonBuilder> {
  private final ApplicationModule module;

  public ApplicationModule_ProvidesGsonBuilderFactory(ApplicationModule module) {
    assert module != null;
    this.module = module;
  }

  @Override
  public GsonBuilder get() {
    return Preconditions.checkNotNull(
        module.providesGsonBuilder(), "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<GsonBuilder> create(ApplicationModule module) {
    return new ApplicationModule_ProvidesGsonBuilderFactory(module);
  }
}
