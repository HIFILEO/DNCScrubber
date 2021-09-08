package com.LEO.DNCScrubber.Scrubber.view;/*
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileSaverView extends Component {
    final static Logger logger = LoggerFactory.getLogger(FileSaverView.class);
    final private  String pattern = "MM-dd-yyyy-HH-mm-ss";
    private JFileChooser fileChooser = new JFileChooser();
    private JFrame frame;
    private File file;
    private boolean userCanceled;
    private boolean fileLoadError;
    private String errorMessage;

    public FileSaverView() {
        //Try and open the running directory first always.
        try {
            fileChooser.setCurrentDirectory(new File((new File(".").getCanonicalPath())));
        } catch (IOException e) {
            logger.warn("File path for current directory failed. Set file chooser to User Home");
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }

        //Title of Dialog
        fileChooser.setDialogTitle("Export Raw Leads To Skip Trace");

        //only allow CSV files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV FILES", "csv");
        fileChooser.setFileFilter(filter);

        frame = new JFrame();
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
    }

    /**
     * Show the chooser
     * <p>
     *  Blocking Call
     *  </p>
     */
    public void showSavDialog() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = new Date(System.currentTimeMillis());
        String fileName = simpleDateFormat.format(date) + "-Raw Leads To Skip Trace.csv";

        userCanceled = false;
        fileLoadError = false;
        errorMessage = null;

        /*
        Blocking Call
        */
        fileChooser.setSelectedFile(new File(fileName));
        int result = fileChooser.showSaveDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                file = fileChooser.getSelectedFile();
                logger.info("Selected File: {}", file.getAbsolutePath());
            } catch (Exception e) {
                logger.error("Failed to load selected file {}", e.getMessage());
                fileLoadError = true;
                errorMessage = e.getMessage();
            }
        } else if (result == JFileChooser.CANCEL_OPTION) {
            logger.info("User Canceled File Chooser");
            userCanceled = true;
        } else if (result == JFileChooser.ERROR_OPTION) {
            logger.warn("Chooser failed with ERROR_OPTION");
            fileLoadError = true;
        } else {
            logger.error("File Chooser returning unknown result");
            fileLoadError = true;
        }

        frame.dispose();
    }

    public File getFile() {
        return file;
    }

    public boolean isUserCanceled() {
        return userCanceled;
    }

    public boolean isFileLoadError() {
        return fileLoadError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
