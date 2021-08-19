package com.LEO.DNCScrubber.Scrubber.gateway;/*
Copyright 2021 Braavos Holdings, LLC

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import com.LEO.DNCScrubber.Scrubber.model.data.ColdRvmLead;
import com.LEO.DNCScrubber.Scrubber.model.data.Person;
import com.LEO.DNCScrubber.Scrubber.model.data.Property;
import io.reactivex.Observable;

/**
 * Gateway for interacting with a database for persistence.
 */
public interface DatabaseGateway {

    /**
     * Write a single {@link ColdRvmLead} to the database. Will update or save.
     * @param coldRvmLead - lead to write
     * @return - {@link Observable} boolean if successful, false otherwise
     */
    Observable<Boolean> writeColdRvmLead(ColdRvmLead coldRvmLead);

    Observable<ColdRvmLead> loadColdRvmLeadByNaturalId(String naturalId);

    Observable<Person> loadPersonByNaturalId(String naturalId);

    Observable<Property> loadPropertyByNaturalId(String naturalId);
}
