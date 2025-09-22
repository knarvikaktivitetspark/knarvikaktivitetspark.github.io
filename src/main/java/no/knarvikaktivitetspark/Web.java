package no.knarvikaktivitetspark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Web {

    public static final File HOME_DIR = resolveHomeDir();

    public static final File TEMPLATES_DIR = new File(HOME_DIR, "templates");
    public static final File TARGET_DIR = new File(HOME_DIR, "target");
    public static final File IMAGES_DIR = new File(HOME_DIR, "images");
    public static final File RESOURCES_DIR = new File(HOME_DIR, "resources");
    public static final File DATA_DIR = new File(HOME_DIR, "data");

    private static ExecutorService EXECUTOR = Executors.newFixedThreadPool(1);

    private static Map<String, Object> data = new HashMap<>();

    public static void build() {
        Web.copyNpmResources();
        Web.copyImages();
        Web.copyResources();
        Web.loadData();
        Web.copyPages();
    }

    public static void serve() throws IOException {
        Web.browserSync();

        new Thread(() -> watch(TEMPLATES_DIR, Web::build)).start();
        new Thread(() -> watch(new File(TEMPLATES_DIR, "nytt"), Web::build)).start();
        new Thread(() -> watch(IMAGES_DIR, Web::copyImages)).start();
        new Thread(() -> watch(RESOURCES_DIR, Web::copyResources)).start();
        new Thread(() -> watch(DATA_DIR, () -> { loadData(); build(); })).start();
    }

    public static void copyPages() {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_34);
            cfg.setDirectoryForTemplateLoading(TEMPLATES_DIR);
            cfg.setDefaultEncoding(StandardCharsets.UTF_8.toString());

            BufferedReader br = new BufferedReader(new FileReader(new File(HOME_DIR, "pages")));
            for (String page = br.readLine(); page != null; page = br.readLine()) {
                page = page.trim();
                if (page.startsWith("#")) {
                    continue;
                }

                Template template = cfg.getTemplate(page + ".ftl");

                if (!TARGET_DIR.isDirectory()) TARGET_DIR.mkdir();

                try (Writer out = new FileWriter(new File(TARGET_DIR, page + ".html"))) {
                    template.process(data, out);
                }
            }

            System.out.println("Built pages");
        } catch (Exception e) {
            throw new WebBuildException("Failed to build pages", e);
        }
    }

    public static void copyImages() {
        Arrays.stream(IMAGES_DIR.listFiles(file -> file.getName().endsWith(".png") || file.getName().endsWith(".svg"))).forEach(f -> copy(f, new File(TARGET_DIR, f.getName())));
        System.out.println("Copied images");
    }

    public static void copyResources() {
        Arrays.stream(RESOURCES_DIR.listFiles()).forEach(f -> copy(f, new File(TARGET_DIR, f.getName())));
        System.out.println("Copied resources");
    }

    public static void copyNpmResources() {
        try {
            File nodeModules = new File(HOME_DIR, "node_modules");
            Properties properties = new Properties();
            properties.load(new FileReader(new File(HOME_DIR, "npm-include.properties")));
            properties.forEach((k, v) -> copy(new File(nodeModules, (String) k), new File(TARGET_DIR, (String) v)));
            System.out.println("Copied NPM resources");
        } catch (IOException e) {
            throw new WebBuildException("Failed to copy NPM resources", e);
        }
    }

    public static void watch(File directory, Runnable runnable) {
        System.out.println("Watching " + directory.getAbsolutePath());
        Path path = directory.toPath();
        try (WatchService service =  path.getFileSystem().newWatchService()) {
            path.register(service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

            while (true) {
                WatchKey take = service.take();
                take.pollEvents().clear();
                EXECUTOR.submit(new CatchExceptionRunner(runnable));
                take.reset();
            }
        } catch (Exception e) {
            throw new WebBuildException("Failed to watch files", e);
        }
    }

    public static void loadData() {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            Plan plan = objectMapper.readValue(new File(DATA_DIR, "plan.yml"), Plan.class);
            data.put("plan", plan);
        } catch (IOException e) {
            throw new WebBuildException("Failed to load data", e);
        }

        List<News> news = Arrays.stream(new File(TEMPLATES_DIR, "nytt").listFiles(f -> f.getName().endsWith(".ftl")))
                .map(File::getName)
                .sorted(Comparator.reverseOrder())
                .map(n -> new News(n.substring(0, 8), n))
                .toList();
        data.put("newsEntries", news);
    }

    public static void browserSync() throws IOException {
        new ProcessBuilder("browser-sync", "start", "--server", "--index", "index.html", "--files", "*.*")
                .directory(TARGET_DIR)
                .start();
        System.out.println("Started browser sync");
    }

    private static void copy(File source, File target) {
        try {
            if (!target.getParentFile().isDirectory()) target.getParentFile().mkdirs();
            FileUtils.copyFile(source, target);
        } catch (IOException e) {
            throw new WebBuildException("Failed to copy file", e);
        }
    }

    private static File resolveHomeDir() {
        return new File(System.getProperty("user.dir"));
    }

    public static class CatchExceptionRunner implements Runnable {

        private final Runnable runnable;

        public CatchExceptionRunner(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
