package com.LEO.DNCScrubber.util;/*
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Helper for reading json data files from src/test/resources.
 */
public class TestResourceFileHelper {
    private static final String RESOURCE_PATH = "/src/test/resources/";

    /**
     * Private constructor to guard against instantiation.
     */
    private TestResourceFileHelper() {
    }

    /**
     * Return the contents of the file with the provided fileName from the test resource directory.
     *
     * @param callerClass - test class making the request.
     * @param fileName - The name of the file in the test resource directory.
     * @return The contents of the file with the provided fileName from the test resource directory.
     * @throws Exception if something goes wrong.
     */
    public static String getFileContentAsString(Object callerClass, String fileName) throws Exception {
        ClassLoader classLoader = callerClass.getClass().getClassLoader();
        File jsonFile = new File(classLoader.getResource(fileName).getFile());
        FileInputStream inputStream = new FileInputStream(jsonFile);
        StringBuilder contents = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = br.readLine()) != null) {
            contents.append(line);
        }
        return contents.toString();
    }
}

