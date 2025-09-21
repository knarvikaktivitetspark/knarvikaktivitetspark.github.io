package no.knarvikaktivitetspark;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Main {

    private static final File templates =  new File("templates");
    private static final File target =  new File("target");
    private static final File images =  new File("images");
    private static final File resources =  new File("resources");

    public static void main(String[] args) throws IOException {
        copyNpmResources();
        copyImages();
        copyResources();
        build();
        browserSync();

        new Thread(() -> watch(templates, Main::build)).start();
        new Thread(() -> watch(images, Main::copyImages)).start();
        new Thread(() -> watch(resources, Main::copyResources)).start();
    }

    public static void build() {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_34);
            cfg.setDirectoryForTemplateLoading(templates);
            cfg.setDefaultEncoding(StandardCharsets.UTF_8.toString());

            Map<String, String> map = new HashMap<>();

            Template template = cfg.getTemplate("index.ftl");

            if (!target.isDirectory()) target.mkdir();

            try (Writer out = new FileWriter(new File(target, "index.html"))) {
                template.process(map, out);
            }

            System.out.println("Built pages");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyImages() {
        Arrays.stream(images.listFiles(file -> file.getName().endsWith(".png") || file.getName().endsWith(".svg"))).forEach(f -> copy(f, new File(target, f.getName())));
        System.out.println("Copied images");
    }

    public static void copyResources() {
        Arrays.stream(resources.listFiles()).forEach(f -> copy(f, new File(target, f.getName())));
        System.out.println("Copied resources");
    }

    public static void copyNpmResources() {
        try {
            File nodeModules = new File("node_modules");
            Properties properties = new Properties();
            properties.load(new FileReader("npm-include.properties"));
            properties.forEach((k, v) -> copy(new File(nodeModules, (String) k), new File(target, (String) v)));
            System.out.println("Copied NPM resources");
        } catch (IOException e) {
            throw new RuntimeException(e);
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
                runnable.run();
                take.reset();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void browserSync() throws IOException {
        new ProcessBuilder("browser-sync", "start", "--server", "--directory", "--files", "*.*")
                .directory(target)
                .start();
        System.out.println("Started browser sync");
    }

    private static void copy(File source, File target) {
        try {
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}