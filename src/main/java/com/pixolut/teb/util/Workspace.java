package com.pixolut.teb.util;

import act.cli.Command;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.E;
import org.osgl.util.IO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Setup a workspace in filesystem to download TEB projects from github.
 */
public class Workspace {

    /**
     * The root of TEB project
     */
    private File root;

    private Workspace(File root) {
        this.root = $.notNull(root);
    }

    @Override
    public String toString() {
        return "TEB workspace: " + root.getAbsolutePath();
    }

    public List<File> projectDirs() {
        List<File> list = new ArrayList<>();
        File frameworks = new File(root, "frameworks");
        for (File languages : frameworks.listFiles()) {
            if (!languages.isDirectory()) {
                continue;
            }
            list.addAll(C.listOf(languages.listFiles()));
        }
        return list;
    }

    /**
     * Fetch/load workspace.
     */
    public static class Loader {

        /**
         * Record the workspace dir path.
         */
        public static final String ID_FILE = ".workspace";

        /**
         * The git repository URL for Tech Empower Benchmark project
         */
        public static final String GIT_REPO = "https://github.com/TechEmpower/FrameworkBenchmarks.git";

        @Command(name = "workspace.load", help = "load workspace")
        public Workspace load() {
            String path = findPath();
            return null != path ? load(path) : fetchFromGithub();
        }

        @Command(name = "workspace.fetch", help = "fetch workspace from github")
        public Workspace fetchFromGithub() {
            File root = createTmpDir();
            String[] cmd = {"git", "clone", "--depth", "1", GIT_REPO, root.getAbsolutePath()};
            try {
                Process process = Runtime.getRuntime().exec(cmd);
                process.waitFor();
                if (process.exitValue() != 0) {
                    String error = IO.readContentAsString(process.getErrorStream());
                    throw E.unexpected("Error cloning TEB project: %s", error);
                }
            } catch (Exception e) {
                throw E.unexpected(e, "error clone TEB git repository");
            }
            IO.writeContent(root.getAbsolutePath(), new File(ID_FILE));
            return new Workspace(root);
        }

        private Workspace load(String path) {
            File root = new File(path);
            return root.exists() ? new Workspace(root) : fetchFromGithub();
        }

        private String findPath() {
            File file = new File(ID_FILE);
            return file.exists() ? IO.readContentAsString(file) : null;
        }

        private File createTmpDir() {
            try {
                return Files.createTempDirectory("teb").toFile();
            } catch (IOException e) {
                throw E.ioException(e);
            }
        }

    }

}
