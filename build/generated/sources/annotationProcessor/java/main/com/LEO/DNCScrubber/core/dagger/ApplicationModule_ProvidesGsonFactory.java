package com.LEO.DNCScrubber.core.dagger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class ApplicationModule_ProvidesGsonFactory implements Factory<Gson> {
  private final ApplicationModule module;

  private final Provider<GsonBuilder> builderProvider;

  public ApplicationModule_ProvidesGsonFactory(
      ApplicationModule module, Provider<GsonBuilder> builderProvider) {
    assert module != null;
    this.module = module;
    assert builderProvider != null;
    this.builderProvider = builderProvider;
  }

  @Override
  public Gson get() {
    return Preconditions.checkNotNull(
        module.providesGson(builderProvider.get()),
        "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<Gson> create(
      ApplicationModule module, Provider<GsonBuilder> builderProvider) {
    return new ApplicationModule_ProvidesGsonFactory(module, builderProvider);
  }
}
