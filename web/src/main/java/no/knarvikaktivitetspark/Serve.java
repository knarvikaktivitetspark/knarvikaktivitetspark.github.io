package no.knarvikaktivitetspark;

import org.apache.commons.io.FileUtils;

import java.io.IOException;

public class Serve {

    public static void main(String[] args) throws IOException {
        FileUtils.deleteDirectory(Web.TARGET_DIR);
        Web.TARGET_DIR.mkdir();
        Web.build();
        Web.serve();
    }

}