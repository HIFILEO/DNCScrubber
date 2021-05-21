package com.LEO.DNCScrubber.core.dagger;

import com.LEO.DNCScrubber.Scrubber.controller.CsvFileReader;
import com.LEO.DNCScrubber.Scrubber.controller.CsvFileReaderImpl;
import com.LEO.DNCScrubber.Scrubber.model.ScreenData;
import com.LEO.DNCScrubber.Scrubber.viewconntroller.DncScrubberViewController;
import com.LEO.DNCScrubber.Scrubber.viewmodel.DncScrubberViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import javafx.stage.Screen;

import javax.inject.Singleton;

@Module
public class ApplicationModule {

    @Singleton
    @Provides
    public Gson providesGson(GsonBuilder builder) {
        return builder.create();
    }

    @Singleton
    @Provides
    public GsonBuilder providesGsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder;
    }

    @Singleton
    @Provides
    public CsvFileReader providesCsvFileReader() {
        return new CsvFileReaderImpl();
    }

    @Singleton
    @Provides
    public DncScrubberViewController providesDncScrubberViewController(DncScrubberViewModel dncScrubberViewModel,
                                                                       ScreenData screenData) {
        return new DncScrubberViewController(dncScrubberViewModel, screenData);
    }

    @Singleton
    @Provides
    public DncScrubberViewModel providesDncScrubberViewModel(ScreenData screenData, CsvFileReader csvFileReader) {
        return new DncScrubberViewModel(screenData, csvFileReader);
    }

    @Singleton
    @Provides
    public ScreenData providesScreenData(Gson gson) {
        return new ScreenData(gson);
    }
}
