package com.pixolut.teb.util;

import act.Act;
import act.app.conf.AutoConfig;
import act.cli.Command;
import act.cli.Optional;
import act.util.LogSupport;
import com.alibaba.fastjson.JSON;
import com.pixolut.teb.model.BenchmarkConfig;
import com.pixolut.teb.model.Project;
import com.pixolut.teb.model.Test;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.osgl.$;
import org.osgl.util.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/**
 * Analyse TEB workspace and generate project data.
 */
@AutoConfig
public class Analyser extends LogSupport {

    /**
     * Where to fetch performance data.
     */
    public static final Const<String> PERF_URL = $.constant("https://www.techempower.com/benchmarks/previews/round15/results/round15/ph.json");

    @Inject
    private Project.Service projectService;

    @Inject
    private Workspace.Loader workspaceLoader;

    /**
     * Run analysis over workspace.
     *
     * This will generate project data from the workspace.
     * If there are project data generated already, this
     * method will return immediately unless `force` parameter
     * is `true`, in which case the old data will be wiped out
     *
     * @param force
     *      if `true` then force run analysis and wipe out
     *      existing data if there is any
     */
    @Command(name = "analyse", help = "run analyse on all TEB projects")
    public void doAnalyse(@Optional("force run analysis and wipe out old data") boolean force) {
        if (!force && projectService.count() > 0) {
            return;
        }
        Test.Result.RawReport rawReport = fetchRawReport();
        List<Project> projects = doAnalyze(workspaceLoader.load(), rawReport);
        projectService.drop();
        projectService.save(projects);
    }

    private Test.Result.RawReport fetchRawReport() {
        OkHttpClient http = new OkHttpClient.Builder().build();
        try {
            Response resp = http.newCall(new Request.Builder().url(PERF_URL.get()).get().build()).execute();
            return JSON.parseObject(resp.body().string(), Test.Result.RawReport.class);
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    private List<Project> doAnalyze(Workspace workspace, Test.Result.RawReport rawReport) {
        List<File> projectRoots = workspace.projectDirs();
        List<Project> projects = new ArrayList<>(projectRoots.size());
        for (File projectRoot : projectRoots) {
            if (!projectRoot.isDirectory()) {
                // skip README file
                continue;
            }
            Project project = doAnalyse(projectRoot);
            if (null != project && project.hasEffectiveTests()) {
                processTestResult(project, rawReport);
                projects.add(project);
            }
        }
        return projects;
    }

    private void processTestResult(Project project, Test.Result.RawReport rawReport) {
        String framework = project.framework;
        Test.Result.RawData rawData = rawReport.rawData;
        for (Test test : project.tests) {
            String key = testKey(framework, test);
            for (Test.Type type : Test.Type.values()) {
                Map<String, List<Test.Result>> results = type.fetch(rawData);
                List<Test.Result> testResults = results.get(key);
                if (null != testResults) {
                    test.results.put(type, testResults);
                    test.bestResult.put(type, Test.Result.bestOf(testResults));
                }
            }
        }
        project.calculateDensity();
    }

    private String testKey(String framework, Test test) {
        String name = test.name;
        String mapped = (String) Act.appConfig().get(S.pathConcat(framework, '.', name));
        if (null != mapped) {
            name = mapped;
        }
        return ("default".equals(name)) ? framework : S.pathConcat(framework, '-', name);
    }

    private Project doAnalyse(File projectRoot) {
        Project project = readConfig(projectRoot);
        if (null != project && project.hasEffectiveTests()) {
            project.loc = countLoc(projectRoot, project);
        }
        return project;
    }

    private Project readConfig(File projectRoot) {
        File configFile = new File(projectRoot, "benchmark_config.json");
        if (!configFile.canRead()) {
            warn("Cannot read benchmark config in %s", projectRoot.getPath());
            return null;
        }
        BenchmarkConfig config = JSON.parseObject(IO.readContentAsString(configFile), BenchmarkConfig.class);
        return new Project(config, projectRoot);
    }

    private int countLoc(File projectRoot, Project project) {
        List<String> locOutput = locOutput(projectRoot);
        Map<String, Integer> locPerLang = locPerLang(locOutput);
        Integer loc = locPerLang.get(project.language.toLowerCase());
        if (null != loc && "C".equalsIgnoreCase(project.language) || "C++".equalsIgnoreCase(project.language)) {
            Integer headerLoc = locPerLang.get("c/c++ header");
            if (null != headerLoc) {
                loc += headerLoc;
            }
        }
        return null != loc ? loc : -1;
    }

    private Map<String, Integer> locPerLang(List<String> locOutput) {
        Map<String, Integer> map = new HashMap<>();
        for (String line : locOutput) {
            line = line.trim();
            if ("".equals(line)) {
                continue;
            }
            if (line.contains("files") || line.contains("http:")) {
                continue;
            }
            if (line.startsWith("-") || line.startsWith("Language") || line.startsWith("SUM")) {
                continue;
            }
            List<String> tokens = S.fastSplit(line, " ");
            String language = tokens.get(0).toLowerCase();
            int loc = Integer.parseInt(tokens.get(tokens.size() - 1));
            map.put(language, loc);
        }
        return map;
    }

    private List<String> locOutput(File projectRoot) {
        File src = projectRoot;
        processRubyFiles(projectRoot);
        processHHVMIncFile(projectRoot);
        processRubyH2oMrubyFile(projectRoot);
        processPsgiFile(projectRoot);
        processUspFile(projectRoot);
        String cloc = projectRoot.getAbsolutePath().contains("Groovy/") ? "cloc" : "loc";
        try {
            Process process = Runtime.getRuntime().exec(new String[]{cloc, src.getAbsolutePath()});
            process.waitFor();
            return IO.readLines(process.getInputStream());
        } catch (Exception e) {
            logger.warn(e, "Error count source code lines");
        }
        return C.list();
    }

    /**
     * It needs to rename `.ru` file to `.rb` file to count LOC correctly
     * @param projectRoot
     *      the project root
     */
    private void processRubyFiles(File projectRoot) {
        if (!projectRoot.getAbsolutePath().contains("/Ruby/")) {
            return;
        }
        File[] files = projectRoot.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                processRubyFiles(file);
            } else if (file.getName().endsWith(".ru")) {
                File newFile = new File(file.getParentFile(), file.getName() + ".rb");
                IO.copyDirectory(file, newFile);
            }
        }
    }

    /**
     * It needs to rename '.conf' file to '.rb' file to count LOC correctly
     * @param projectRoot
     *      the project root
     */
    private void processRubyH2oMrubyFile(File projectRoot) {
        if (!projectRoot.getAbsolutePath().contains("Ruby/h2o_mruby")) {
            return;
        }
        File file = new File(projectRoot, "h2o.conf");
        File newFile = new File(projectRoot, "h2o.rb");
        IO.copyDirectory(file, newFile);
    }

    /**
     * It needs to rename '.php.inc' file to '.php' file to count LOC correctly
     * @param projectRoot
     *      the project root
     */
    private void processHHVMIncFile(File projectRoot) {
        if (!projectRoot.getAbsolutePath().contains("PHP/hhvm")) {
            return;
        }
        File[] files = projectRoot.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                processHHVMIncFile(file);
            } else if (file.getName().endsWith(".php.inc")) {
                String fileName = file.getName();
                String newFileName = S.beforeLast(fileName, ".inc");
                File newFile = new File(file.getParentFile(), newFileName);
                IO.copyDirectory(file, newFile);
            }
        }
    }

    /**
     * It needs to rename '.psgi' file to '.pl' file to count LOC correctly
     * @param projectRoot
     *      the project root
     */
    private void processPsgiFile(File projectRoot) {
        if (!projectRoot.getAbsolutePath().contains("Perl/plack")) {
            return;
        }
        File[] files = projectRoot.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                processPsgiFile(file);
            } else if (file.getName().endsWith(".psgi")) {
                String fileName = file.getName();
                String newFileName = fileName + ".pl";
                File newFile = new File(file.getParentFile(), newFileName);
                IO.copyDirectory(file, newFile);
            }
        }
    }

    /**
     * It needs to rename '.usp' file to '.cpp' file to count LOC correctly
     * @param projectRoot
     *      the project root
     */
    private void processUspFile(File projectRoot) {
        if (!projectRoot.getAbsolutePath().contains("C++/ulib")) {
            return;
        }
        File[] files = projectRoot.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                processUspFile(file);
            } else if (file.getName().endsWith(".usp")) {
                String fileName = file.getName();
                String newFileName = fileName + ".cpp";
                File newFile = new File(file.getParentFile(), newFileName);
                IO.copyDirectory(file, newFile);
            }
        }
    }

    public static void main(String[] args) {
        Test.Result.RawReport report = new Analyser().fetchRawReport();
        System.out.println(report);
    }

}
