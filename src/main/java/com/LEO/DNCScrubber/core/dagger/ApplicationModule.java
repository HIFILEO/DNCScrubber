package com.LEO.DNCScrubber.core.dagger;

import com.LEO.DNCScrubber.Scrubber.controller.CsvFileController;
import com.LEO.DNCScrubber.Scrubber.controller.CsvFileControllerImpl;
import com.LEO.DNCScrubber.Scrubber.gateway.DatabaseGateway;
import com.LEO.DNCScrubber.Scrubber.gateway.DatabaseGatewayImpl;
import com.LEO.DNCScrubber.Scrubber.model.ScreenData;
import com.LEO.DNCScrubber.Scrubber.viewconntroller.DncScrubberViewController;
import com.LEO.DNCScrubber.Scrubber.viewmodel.DncScrubberViewModel;
import com.LEO.DNCScrubber.core.hibernate.HibernateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class ApplicationModule {

    @Singleton
    @Provides
    public DatabaseGateway providesDatabaseGateway(HibernateUtil hibernateUtil) {
        return new DatabaseGatewayImpl(hibernateUtil);
    }

    @Singleton
    @Provides
    public HibernateUtil providesHibernateUtil() {
        return new HibernateUtil();
    }

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
    public CsvFileController providesCsvFileReader() {
        return new CsvFileControllerImpl();
    }

    @Singleton
    @Provides
    public DncScrubberViewController providesDncScrubberViewController(DncScrubberViewModel dncScrubberViewModel,
                                                                       ScreenData screenData) {
        return new DncScrubberViewController(dncScrubberViewModel, screenData);
    }

    @Singleton
    @Provides
    public DncScrubberViewModel providesDncScrubberViewModel(ScreenData screenData, CsvFileController csvFileController,
                                                             DatabaseGateway databaseGateway) {
        return new DncScrubberViewModel(screenData, csvFileController, databaseGateway);
    }

    @Singleton
    @Provides
    public ScreenData providesScreenData(Gson gson) {
        return new ScreenData(gson);
    }
}
