package com.LEO.DNCScrubber.core.dagger;

import com.LEO.DNCScrubber.core.application.DncScrubberApplication;
import dagger.BindsInstance;
import dagger.Component;

import javax.inject.Singleton;

/**
 * Application-level Dagger2 {@link Component}.
 */
@Singleton
@Component(
        modules = {
                ApplicationModule.class
        })
public interface ApplicationComponent {

    /**
     * This is the custom builder for injecting the ApplicationModule with objects that it needs.
     * This eliminates the need for a constructor in the module.
     *
     * <p>
     * Help from - https://proandroiddev.com/dagger-2-component-builder-1f2b91237856
     * </p>
     */
    @Component.Builder
    interface Builder {

        //Note - If you want to pass Application to constructors of provide methods this is what you do.
        //Note - If you want to pass Application to constructors of provide methods, you'll need to add it or cast.
        @BindsInstance
        Builder application(DncScrubberApplication dncScrubberApplication);

        ApplicationComponent build();

    }

    void inject(DncScrubberApplication dncScrubberApplication);
}
