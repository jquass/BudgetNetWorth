package com.jonquass.budgetnetworth.data.csv;

import com.jonquass.budgetnetworth.core.upload.Upload;
import com.jonquass.budgetnetworth.core.upload.UploadContext;
import com.jonquass.budgetnetworth.core.upload.UploadEgg;
import com.jonquass.budgetnetworth.core.upload.UploadStatus;
import com.jonquass.budgetnetworth.core.upload.header.UploadHeader;
import com.jonquass.budgetnetworth.core.upload.header.UploadHeaderEgg;
import com.jonquass.budgetnetworth.core.upload.row.UploadRow;
import com.jonquass.budgetnetworth.core.upload.row.UploadRowEgg;
import com.jonquass.budgetnetworth.data.jdbi.upload.UploadManager;
import com.jonquass.budgetnetworth.data.jdbi.upload.db.manager.UploadDbManager;
import com.jonquass.budgetnetworth.data.jdbi.upload.db.manager.UploadHeaderDbManager;
import com.jonquass.budgetnetworth.data.jdbi.upload.db.manager.UploadRowDbManager;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class CsvReader {

    private static final Logger LOG = LoggerFactory.getLogger(CsvReader.class);

    private static final Pattern PATTERN_FILENAME = Pattern.compile(".*filename=\"(?<filename>.*)");


    private final UploadDbManager uploadDbManager;
    private final UploadHeaderDbManager uploadHeaderDbManager;
    private final UploadRowDbManager uploadRowDbManager;
    private final UploadManager uploadManager;

    @Inject
    CsvReader(UploadDbManager uploadDbManager,
              UploadHeaderDbManager uploadHeaderDbManager,
              UploadRowDbManager uploadRowDbManager,
              UploadManager uploadManager) {
        this.uploadDbManager = uploadDbManager;
        this.uploadHeaderDbManager = uploadHeaderDbManager;
        this.uploadRowDbManager = uploadRowDbManager;
        this.uploadManager = uploadManager;
    }

    private static boolean isLineListEmpty(List<String> lineList) {
        return lineList.isEmpty() || lineList.getFirst().isEmpty();
    }

    public Optional<UploadContext> stageUpload(long accountId, File csvFile) {
        Upload upload;
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            String webKitBoundary = null;
            String filename = "unknown";
            String[] nextLine;

            LOG.info("Processing Filer Headers");
            while ((nextLine = reader.readNext()) != null) {
                LOG.debug("Header {}", Arrays.toString(nextLine));

                List<String> lineList = List.of(nextLine);
                if (isLineListEmpty(lineList)) {
                    break;
                }

                String line = lineList.stream().findFirst().orElseThrow();
                if (line.contains("WebKitFormBoundary")) {
                    webKitBoundary = line;
                } else if (line.startsWith("Content-Disposition")) {
                    Matcher m = PATTERN_FILENAME.matcher(line);
                    if (m.find()) {
                        filename = m.group("filename");
                    }
                }
            }

            upload = uploadDbManager.insert(UploadEgg.builder()
                    .setAccountId(accountId)
                    .setFilename(filename)
                    .setUploadStatus(UploadStatus.STAGING)
                    .build());
            LOG.debug("Upload {}", upload);

            LOG.info("Processing CSV Headers");
            if ((nextLine = reader.readNext()) != null) {
                List<String> lineList = List.of(nextLine);

                for (String header : lineList) {
                    UploadHeaderEgg uploadHeaderEgg = UploadHeaderEgg.builder()
                            .setUploadId(upload.getId())
                            .setValue(header)
                            .build();
                    UploadHeader uploadHeader = uploadHeaderDbManager.insert(uploadHeaderEgg);
                    LOG.info("Upload Header {}", uploadHeader);
                }

            }

            LOG.info("Processing Body");
            while ((nextLine = reader.readNext()) != null) {
                List<String> lineList = List.of(nextLine);
                if (isLineListEmpty(lineList)) {
                    break;
                }

                UploadRowEgg uploadRowEgg = UploadRowEgg.builder()
                        .setUploadId(upload.getId())
                        .setFullRow(String.join(",", lineList))
                        .build();

                UploadRow uploadRow = uploadRowDbManager.insert(uploadRowEgg);
                LOG.debug("Upload Row {}", uploadRow);
            }

            LOG.info("Processing Footer");
            while ((nextLine = reader.readNext()) != null) {
                List<String> lineList = List.of(nextLine);
                if (isLineListEmpty(lineList)) {
                    break;
                }

                String line = lineList.stream().findFirst().orElseThrow();
                if (line.contains("WebKitFormBoundary")) {
                    if (webKitBoundary != null && !line.contains(webKitBoundary)) {
                        LOG.error("Unexpected end boundary {}, expected {}", line, webKitBoundary);
                    }
                }
            }
        } catch (IOException | CsvValidationException e) {
            LOG.error("Exception Thrown Staging Upload", e);
            return Optional.empty();
        }

        LOG.info("Finished Staging Upload {}", upload);
        return uploadManager.getUploadContext(upload.getId());
    }

}
