package com.LEO.DNCScrubber.core.application;

import com.LEO.DNCScrubber.Scrubber.viewconntroller.DncScrubberViewController;
import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DncScrubberApplication_MembersInjector
    implements MembersInjector<DncScrubberApplication> {
  private final Provider<DncScrubberViewController> dncScrubberViewControllerProvider;

  public DncScrubberApplication_MembersInjector(
      Provider<DncScrubberViewController> dncScrubberViewControllerProvider) {
    assert dncScrubberViewControllerProvider != null;
    this.dncScrubberViewControllerProvider = dncScrubberViewControllerProvider;
  }

  public static MembersInjector<DncScrubberApplication> create(
      Provider<DncScrubberViewController> dncScrubberViewControllerProvider) {
    return new DncScrubberApplication_MembersInjector(dncScrubberViewControllerProvider);
  }

  @Override
  public void injectMembers(DncScrubberApplication instance) {
    if (instance == null) {
      throw new NullPointerException("Cannot inject members into a null reference");
    }
    instance.dncScrubberViewController = dncScrubberViewControllerProvider.get();
  }

  public static void injectDncScrubberViewController(
      DncScrubberApplication instance,
      Provider<DncScrubberViewController> dncScrubberViewControllerProvider) {
    instance.dncScrubberViewController = dncScrubberViewControllerProvider.get();
  }
}
