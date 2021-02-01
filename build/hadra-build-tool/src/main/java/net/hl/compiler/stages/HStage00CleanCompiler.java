package net.hl.compiler.stages;

import net.hl.compiler.core.HOptions;
import net.hl.compiler.core.HProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;
import net.hl.compiler.HL;
import net.hl.compiler.core.HTask;
import net.hl.compiler.utils.HFileUtils;

public class HStage00CleanCompiler extends AbstractHStage {

    private static final Logger LOG = Logger.getLogger(HStage00CleanCompiler.class.getName());

    @Override
    public HTask[] getTasks() {
        return new HTask[]{HTask.CLEAN};
    }

    @Override
    public boolean isEnabled(HProject project, HL options) {
        return ((options.containsAnyTask(HTask.CLEAN)));
    }

    @Override
    public void processProject(HProject project, HOptions options) {
        Path rootFolder = HFileUtils.getPath(
                HFileUtils.coalesce(options.getClassFolder(), "hl"),
                Paths.get(HFileUtils.coalesce(options.getTargetFolder(), "target"))
        );

        if (Files.isDirectory(rootFolder)) {
            try {
                Files.walkFileTree(rootFolder, new FileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            project.log().jerror("C001", "clean", null, "failed deleting file : {0}", file);
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        try {
                            Files.delete(dir);
                        } catch (IOException e) {
                            project.log().jerror("C001", "clean", null, "failed deleting file : {0}", dir);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException ex) {
                project.log().jerror("C001", "clean", null, "failed clean : {0}", ex);
            }
//                if(new File(".java-generated").exists()){
//                   folder.get
//                }
        }
    }

}
