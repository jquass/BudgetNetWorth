package com.jonquass.budgetnetworth.data.html;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hubspot.algebra.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Singleton
public class HtmlReader {

    @Inject
    HtmlReader() {

    }

    public Result<String, String> readHtml(HtmlFile htmlFile) {
        InputStream ioStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(htmlFile.getFileName());

        if (ioStream == null) {
            return Result.err("File not found: " + htmlFile.getFileName());
        }

        StringBuilder output = new StringBuilder();
        try (
                InputStreamReader isr = new InputStreamReader(ioStream);
                BufferedReader br = new BufferedReader(isr);
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                output.append(line);
            }
            ioStream.close();
        } catch (IOException e) {
            return Result.err(e.getLocalizedMessage());
        }

        return Result.ok(output.toString());
    }

    public enum HtmlFile {
        DASHBOARD("dashboard.html");

        private final String fileName;

        HtmlFile(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }
    }
}
