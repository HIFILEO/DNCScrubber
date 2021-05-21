package com.LEO.DNCScrubber.core.dagger;

import com.LEO.DNCScrubber.Scrubber.model.ScreenData;
import com.LEO.DNCScrubber.Scrubber.viewconntroller.DncScrubberViewController;
import com.LEO.DNCScrubber.Scrubber.viewmodel.DncScrubberViewModel;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class ApplicationModule_ProvidesDncScrubberViewControllerFactory
    implements Factory<DncScrubberViewController> {
  private final ApplicationModule module;

  private final Provider<DncScrubberViewModel> dncScrubberViewModelProvider;

  private final Provider<ScreenData> screenDataProvider;

  public ApplicationModule_ProvidesDncScrubberViewControllerFactory(
      ApplicationModule module,
      Provider<DncScrubberViewModel> dncScrubberViewModelProvider,
      Provider<ScreenData> screenDataProvider) {
    assert module != null;
    this.module = module;
    assert dncScrubberViewModelProvider != null;
    this.dncScrubberViewModelProvider = dncScrubberViewModelProvider;
    assert screenDataProvider != null;
    this.screenDataProvider = screenDataProvider;
  }

  @Override
  public DncScrubberViewController get() {
    return Preconditions.checkNotNull(
        module.providesDncScrubberViewController(
            dncScrubberViewModelProvider.get(), screenDataProvider.get()),
        "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<DncScrubberViewController> create(
      ApplicationModule module,
      Provider<DncScrubberViewModel> dncScrubberViewModelProvider,
      Provider<ScreenData> screenDataProvider) {
    return new ApplicationModule_ProvidesDncScrubberViewControllerFactory(
        module, dncScrubberViewModelProvider, screenDataProvider);
  }
}
