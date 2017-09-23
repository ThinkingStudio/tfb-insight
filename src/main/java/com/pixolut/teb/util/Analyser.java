package com.pixolut.teb.util;

import act.cli.Command;
import act.util.LogSupport;
import com.alibaba.fastjson.JSON;
import com.pixolut.teb.model.BenchmarkConfig;
import com.pixolut.teb.model.Project;
import org.osgl.util.C;
import org.osgl.util.IO;
import org.osgl.util.S;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/**
 * Analyse TEB workspace and generate project data.
 */
public class Analyser extends LogSupport {

    @Inject
    private Project.Service projectService;

    @Inject
    private Workspace.Loader workspaceLoader;

    @Command(name = "analyse", help = "run analyse on all TEB projects")
    public void doAnalyse() {
        List<Project> projects = doAnalyze(workspaceLoader.load());
        projectService.drop();
        projectService.save(projects);
    }

    private List<Project> doAnalyze(Workspace workspace) {
        List<File> projectRoots = workspace.projectDirs();
        List<Project> projects = new ArrayList<>(projectRoots.size());
        for (File projectRoot : projectRoots) {
            Project project = doAnalyse(projectRoot);
            if (null != project) {
                projects.add(project);
            }
        }
        return projects;
    }

    private Project doAnalyse(File projectRoot) {
        Project project = readConfig(projectRoot);
        if (null != project) {
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
        return null != loc ? loc : -1;
    }

    private Map<String, Integer> locPerLang(List<String> locOutput) {
        Map<String, Integer> map = new HashMap<>();
        for (String line : locOutput) {
            line = line.trim();
            if (line.startsWith("-") || line.startsWith("Language") || line.startsWith("SUM")) {
                continue;
            }
            List<String> tokens = S.fastSplit(line, " ");
            String language = tokens.get(0).toLowerCase();
            if ("c++".equals(language)) {
                language = "c";
            }
            map.put(language, Integer.parseInt(tokens.get(tokens.size() - 1)));
        }
        return map;
    }

    private List<String> locOutput(File projectRoot) {
        File src = new File(projectRoot, "src");
        if (!src.isDirectory()) {
            src = projectRoot;
        }
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"loc", src.getAbsolutePath()});
            process.waitFor();
            return IO.readLines(process.getInputStream());
        } catch (Exception e) {
            logger.warn(e, "Error count source code lines");
        }
        return C.list();
    }

    public static void main(String[] args) {
        File file = new File("/home/luog/p/TEB/frameworks/Java/act/benchmark_config.json");
        String s = IO.readContentAsString(file);
        BenchmarkConfig config = JSON.parseObject(s, BenchmarkConfig.class);
        System.out.println(JSON.toJSONString(config));
    }

}
