package com.LEO.DNCScrubber.core.dagger;

import com.LEO.DNCScrubber.Scrubber.controller.CsvFileReader;
import com.LEO.DNCScrubber.Scrubber.model.ScreenData;
import com.LEO.DNCScrubber.Scrubber.viewconntroller.DncScrubberViewController;
import com.LEO.DNCScrubber.Scrubber.viewmodel.DncScrubberViewModel;
import com.LEO.DNCScrubber.core.application.DncScrubberApplication;
import com.LEO.DNCScrubber.core.application.DncScrubberApplication_MembersInjector;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.MembersInjector;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DaggerApplicationComponent implements ApplicationComponent {
  private Provider<GsonBuilder> providesGsonBuilderProvider;

  private Provider<Gson> providesGsonProvider;

  private Provider<ScreenData> providesScreenDataProvider;

  private Provider<CsvFileReader> providesCsvFileReaderProvider;

  private Provider<DncScrubberViewModel> providesDncScrubberViewModelProvider;

  private Provider<DncScrubberViewController> providesDncScrubberViewControllerProvider;

  private MembersInjector<DncScrubberApplication> dncScrubberApplicationMembersInjector;

  private DaggerApplicationComponent(Builder builder) {
    assert builder != null;
    initialize(builder);
  }

  public static ApplicationComponent.Builder builder() {
    return new Builder();
  }

  @SuppressWarnings("unchecked")
  private void initialize(final Builder builder) {

    this.providesGsonBuilderProvider =
        DoubleCheck.provider(
            ApplicationModule_ProvidesGsonBuilderFactory.create(builder.applicationModule));

    this.providesGsonProvider =
        DoubleCheck.provider(
            ApplicationModule_ProvidesGsonFactory.create(
                builder.applicationModule, providesGsonBuilderProvider));

    this.providesScreenDataProvider =
        DoubleCheck.provider(
            ApplicationModule_ProvidesScreenDataFactory.create(
                builder.applicationModule, providesGsonProvider));

    this.providesCsvFileReaderProvider =
        DoubleCheck.provider(
            ApplicationModule_ProvidesCsvFileReaderFactory.create(builder.applicationModule));

    this.providesDncScrubberViewModelProvider =
        DoubleCheck.provider(
            ApplicationModule_ProvidesDncScrubberViewModelFactory.create(
                builder.applicationModule,
                providesScreenDataProvider,
                providesCsvFileReaderProvider));

    this.providesDncScrubberViewControllerProvider =
        DoubleCheck.provider(
            ApplicationModule_ProvidesDncScrubberViewControllerFactory.create(
                builder.applicationModule,
                providesDncScrubberViewModelProvider,
                providesScreenDataProvider));

    this.dncScrubberApplicationMembersInjector =
        DncScrubberApplication_MembersInjector.create(providesDncScrubberViewControllerProvider);
  }

  @Override
  public void inject(DncScrubberApplication dncScrubberApplication) {
    dncScrubberApplicationMembersInjector.injectMembers(dncScrubberApplication);
  }

  private static final class Builder implements ApplicationComponent.Builder {
    private ApplicationModule applicationModule;

    private DncScrubberApplication application;

    @Override
    public ApplicationComponent build() {
      if (applicationModule == null) {
        this.applicationModule = new ApplicationModule();
      }
      if (application == null) {
        throw new IllegalStateException(
            DncScrubberApplication.class.getCanonicalName() + " must be set");
      }
      return new DaggerApplicationComponent(this);
    }

    @Override
    public Builder application(DncScrubberApplication dncScrubberApplication) {
      this.application = Preconditions.checkNotNull(dncScrubberApplication);
      return this;
    }
  }
}
